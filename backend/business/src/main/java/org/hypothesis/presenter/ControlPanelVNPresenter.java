package org.hypothesis.presenter;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import org.hypothesis.business.ControlPanelDataManager;
import org.hypothesis.business.data.*;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.data.UIMessage;
import org.hypothesis.interfaces.ControlPresenter;
import org.hypothesis.push.Pushable;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.ui.TinyPackPanel;
import org.hypothesis.ui.UserPanel;
import org.hypothesis.ui.view.ControlView;
import org.hypothesis.utility.DateUtility;
import org.hypothesis.utility.UIMessageUtility;
import org.hypothesis.utility.ViewUtility;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.shared.ui.datefield.Resolution.DAY;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import static com.vaadin.ui.themes.ValoTheme.*;
import static org.hypothesis.business.data.TestState.*;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_USER_TEST_STATE;
import static org.hypothesis.ui.HypothesisTheme.TINYPANEL_PROCESSING;
import static org.hypothesis.ui.HypothesisTheme.USERPANEL_SUSPENDED;

@SuppressWarnings("serial")
public class ControlPanelVNPresenter extends AbstractMainBusPresenter implements ControlPresenter, Broadcaster, Broadcaster.Listener, Pushable {

    private final HashMap<Long, UserPanel> userPanels = new HashMap<>();
    private final UserService userService = UserService.newInstance();
    private final ControlPanelDataManager controlPanelDataManager = new ControlPanelDataManager();
    private ControlView view;
    private VerticalLayout mainLayout;
    private Panel mainPanel;
    private PopupDateField dateField;
    private boolean isEmptyInfo = false;

    @Override
    public void enter(ViewChangeEvent event) {
        refreshView();
    }

    @Override
    public void attach() {
        super.attach();

        listenBroadcasting();
    }

    @Override
    public void detach() {
        unlistenBroadcasting();

        super.detach();
    }

    @Override
    public View createView() {
        view = new ControlView(this);
        return view;
    }

    @Override
    public Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setSpacing(true);

        Label title = new Label(Messages.getString("Caption.Label.ControlPanel"));
        title.addStyleName(BUTTON_HUGE);
        header.addComponent(title);
        header.setExpandRatio(title, 1.0f);

        return header;
    }

    @Override
    public Component buildControl() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.addComponent(buildForm());

        Panel panel = buildContentPanel();
        content.addComponent(panel);
        content.setExpandRatio(panel, 1.0f);

        return content;
    }

    private Panel buildContentPanel() {
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addStyleName(PANEL_BORDERLESS);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        panel.setContent(layout);

        mainPanel = buildMainPanel();
        layout.addComponent(mainPanel);
        layout.setExpandRatio(mainPanel, 1.0f);

        return panel;
    }

    private Panel buildMainPanel() {
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addStyleName(PANEL_BORDERLESS);

        mainLayout = buildMainLayout();
        panel.setContent(mainLayout);

        return panel;
    }

    private VerticalLayout buildMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setHeightUndefined();
        layout.setWidth(100.0f, PERCENTAGE);
        layout.setMargin(true);
        layout.setSpacing(true);

        return layout;
    }

    private Component buildForm() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");

        dateField = new PopupDateField();
        dateField.setResolution(DAY);
        dateField.setDateFormat(Messages.getString("Format.Date"));
        dateField.setInputPrompt(Messages.getString("Caption.Field.DateOfTesting"));
        dateField.setCaption(Messages.getString("Caption.Field.DateOfTesting"));
        dateField.setImmediate(true);
        dateField.setValidationVisible(false);
        dateField.setValue(DateUtility.toDate(LocalDate.now()));
        dateField.addValueChangeListener(e -> refreshView());

        FormLayout form = new FormLayout(dateField);
        form.setMargin(false);
        form.setSizeFull();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSizeFull();

        Button enableButton = new Button(Messages.getString("Caption.Button.EnableTesting"));
        enableButton.addStyleName(BUTTON_SMALL);
        enableButton.addClickListener(e -> enableTesting(true));
        buttons.addComponent(enableButton);

        Button disableButton = new Button(Messages.getString("Caption.Button.DisableTesting"));
        disableButton.addStyleName(BUTTON_SMALL);
        disableButton.addClickListener(e -> enableTesting(false));
        buttons.addComponent(disableButton);
        CssLayout spacer = new CssLayout();
        buttons.addComponent(spacer);
        buttons.setExpandRatio(spacer, 1.0f);

        hl.addComponent(form);
        hl.addComponent(buttons);

        return hl;
    }

    private void enableTesting(boolean enable) {
        List<Long> ids = userPanels.values().stream().map(UserPanel::getUser).map(User::getId)
                .collect(Collectors.toList());
        userService.updateUsersTestingSuspended(ids, !enable);

        ids.forEach(id -> broadcastOthers(UIMessageUtility.createRefreshUserPacksViewMessage(id)));

        refreshView();
    }

    private UserPanel createUserControlPanel(final User user, final UserSessionData userSessionData, List<Pack> packs) {
        UserPanel panel = new UserPanel(user);
        updateUserPanel(userSessionData, packs, panel);

        BeanItem<User> userBeanItem = new BeanItem<>(user);
        panel.setNamePropertyDataSource(userBeanItem.getItemProperty("name"));
        panel.setSurnamePropertyDataSource(userBeanItem.getItemProperty("username"));

        if (user.isTestingSuspended()) {
            panel.addStyleName(USERPANEL_SUSPENDED);
        }

        userPanels.put(user.getId(), panel);

        updatePacksPanel(userSessionData, packs, panel);

        return panel;
    }

    private void updateUserPanel(final UserSessionData userSessionData, final List<Pack> packs, UserPanel panel) {
        final SessionData sessionData;
        if (userSessionData != null && !userSessionData.isEmpty() && packs != null) {
            //FIXME: solve multiple user logins
            sessionData = getLastSessionData(userSessionData.getSessionData());
        } else {
            sessionData = new SessionData("");
        }

        BeanItem<SessionData> sessionBeanItem = new BeanItem<>(sessionData);
        BeanItem<TestStateData> stateBeanItem = new BeanItem<>(
                sessionData.getTestStateData());

        panel.setPositionPropertyDataSource(sessionBeanItem.getItemProperty("position"));
        panel.setAddressPropertyDataSource(sessionBeanItem.getItemProperty("address"));
        panel.setMessagePropertyDataSource(stateBeanItem.getItemProperty("eventName"));
    }

    private SessionData getLastSessionData(List<SessionData> sessionData) {
        return sessionData.stream()
                .sorted(Comparator.comparing(SessionData::getTime).reversed())
                .findFirst()
                .orElse(null);
    }

    private void updatePacksPanel(final UserSessionData userSessionData, final List<Pack> packs, final UserPanel panel) {
        Panel packsPanel = panel.getPacksPanel();
        ComponentContainer container = (ComponentContainer) packsPanel.getContent();

        boolean isSuspended = userSessionData.getUser().isTestingSuspended();

        container.removeAllComponents();
        if (isSuspended) {
            packsPanel.addStyleName(USERPANEL_SUSPENDED);
        } else {
            packsPanel.removeStyleName(USERPANEL_SUSPENDED);
        }

        for (Pack pack : packs) {
            TinyPackPanel packPanel = new TinyPackPanel();
            BeanItem<Pack> packBeanItem = new BeanItem<>(pack);
            packPanel.setDescriptionPropertyDataSource(packBeanItem.getItemProperty("name"));
            if (isSuspended) {
                packPanel.addStyleName(USERPANEL_SUSPENDED);
            } else {
                packPanel.removeStyleName(USERPANEL_SUSPENDED);
            }

            if (!userSessionData.isEmpty()) {
                // FIXME: solve multiple user logins
                final SessionData sessionData = getLastSessionData(userSessionData.getSessionData());
                final TestStateData testStateData = sessionData.getTestStateData();
                final TestState state = testStateData.getState();

                if (pack.getId().equals(testStateData.getPackId())) {
                    if (state == BROKEN || state == FINISHED) {
                        packPanel.addStyleName(USERPANEL_SUSPENDED);
                        testStateData.setPackId(null);
                    } else if (state == RUNNING) {
                        packPanel.removeStyleName(USERPANEL_SUSPENDED);
                        packPanel.addStyleName(TINYPANEL_PROCESSING);
                    } else {
                        packPanel.removeStyleName(TINYPANEL_PROCESSING);
                    }
                } else {
                    packPanel.removeStyleName(TINYPANEL_PROCESSING);
                }
            }

            container.addComponent(packPanel);
        }

    }

    @Override
    public void receiveBroadcast(String message) {
        if (ViewUtility.isActiveView(view)) { // prevent from detached ui
            // deserialize received message
            final UIMessage uiMessage = UIMessage.fromJson(message);

            if (UIMessageUtility.canHandle(uiMessage, getLoggedUser(), null)) {
                handleMessage(uiMessage);
            }
        }
    }

    private void handleMessage(UIMessage message) {
        if (REFRESH_USER_TEST_STATE.equals(message.getType())) {
            pushCommand(view.getUI(), () -> refreshUserPanel(message.getSenderId()));
        }
    }

    private void refreshView() {
        setEmptyInfo();
        userPanels.clear();

        if (dateField.getValue() != null) {
            final ControlPanelData data = controlPanelDataManager.getControlPanelData(dateField.getValue());
            createUserControlPanels(data);
        }

        view.markAsDirty();
    }

    private void createUserControlPanels(ControlPanelData data) {
        data.getUsers().forEach(u -> addUserPanel(createUserControlPanel(u, data.getUserSessionDataMap().get(u.getId()), data.getUserPacksMap().get(u.getId()))));
    }

    public void clearMainLayout() {
        mainLayout.removeAllComponents();
        isEmptyInfo = false;
    }

    public void setEmptyInfo() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Label emptyInfoLabel = new Label(Messages.getString("Message.Info.NoPlannedUsers"), HTML);
        emptyInfoLabel.addStyleName(LABEL_LIGHT);
        emptyInfoLabel.addStyleName(LABEL_LARGE);
        emptyInfoLabel.setWidthUndefined();

        layout.addComponent(emptyInfoLabel);
        layout.setComponentAlignment(emptyInfoLabel, MIDDLE_CENTER);

        mainPanel.setContent(layout);

        isEmptyInfo = true;
    }

    private void addUserPanel(UserPanel userPanel) {
        if (userPanel != null) {
            if (isEmptyInfo) {
                clearMainLayout();
                mainPanel.setContent(mainLayout);

                Panel expand = new Panel();
                expand.setSizeFull();
                expand.addStyleName(PANEL_BORDERLESS);

                mainLayout.addComponent(expand);
                mainLayout.setExpandRatio(expand, 1.0f);
            }

            int count = mainLayout.getComponentCount();
            mainLayout.addComponent(userPanel, count - 1);
        }
    }

    private void refreshUserPanel(Long userId) {
        UserPanel panel = userPanels.get(userId);
        if (panel != null) {
            final User user = panel.getUser();
            final ControlPanelData data = controlPanelDataManager.getControlPanelData(Stream.of(user).collect(Collectors.toList()));
            final UserSessionData userSessionData = data.getUserSessionDataMap().get(userId);
            final List<Pack> packs = data.getUserPacksMap().get(userId);

            updateUserPanel(userSessionData, packs, panel);
            updatePacksPanel(userSessionData, packs, panel);
            panel.markAsDirty();
        }
    }

}