package org.hypothesis.presenter;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.shared.ui.datefield.Resolution.DAY;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_HUGE;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_SMALL;
import static com.vaadin.ui.themes.ValoTheme.LABEL_LARGE;
import static com.vaadin.ui.themes.ValoTheme.LABEL_LIGHT;
import static com.vaadin.ui.themes.ValoTheme.PANEL_BORDERLESS;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_USER_TEST_STATE;
import static org.hypothesis.ui.HypothesisTheme.TINYPANEL_PROCESSING;
import static org.hypothesis.ui.HypothesisTheme.USERPANEL_SUSPENDED;
import static org.hypothesis.utility.PushUtility.pushCommand;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.UserControlServiceImpl;
import org.hypothesis.business.VNAddressPositionManager;
import org.hypothesis.business.data.UserControlData;
import org.hypothesis.business.data.UserSession;
import org.hypothesis.business.data.UserTestState;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.data.UIMessage;
import org.hypothesis.interfaces.ControlPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.servlet.BroadcastService.BroadcastListener;
import org.hypothesis.ui.TinyPackPanel;
import org.hypothesis.ui.UserPanel;
import org.hypothesis.ui.view.ControlView;
import org.hypothesis.utility.DateUtility;
import org.hypothesis.utility.UIMessageUtility;
import org.hypothesis.utility.ViewUtility;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ControlPanelVNPresenter extends AbstractMainBusPresenter implements ControlPresenter, BroadcastListener {

	private ControlView view;
	private VerticalLayout mainLayout;
	private Panel mainPanel;
	private PopupDateField dateField;

	private final HashMap<Long, UserPanel> userPanels = new HashMap<>();

	private UserControlServiceImpl userControlService = new UserControlServiceImpl();
	private final UserService userService = UserService.newInstance();

	private boolean isEmptyInfo = false;

	private Properties positionProperties;

	@Override
	public void enter(ViewChangeEvent event) {
		refreshView();
	}

	@Override
	public void attach() {
		super.attach();

		BroadcastService.register(this);
	}

	@Override
	public void detach() {
		BroadcastService.unregister(this);

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
		dateField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				refreshView();
			}
		});

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

		ids.forEach(id -> BroadcastService.broadcast(UIMessageUtility.createRefreshUserPacksViewMessage(id)));

		refreshView();
	}

	private UserPanel createUserControlPanel(UserControlData data) {
		UserPanel panel = new UserPanel(data.getUser());
		updateUserPanel(data, panel);

		BeanItem<User> userBeanItem = new BeanItem<>(data.getUser());
		panel.setNamePropertyDataSource(userBeanItem.getItemProperty("name"));
		panel.setSurnamePropertyDataSource(userBeanItem.getItemProperty("username"));

		User user = data.getUser();
		if (user.isTestingSuspended()) {
			panel.addStyleName(USERPANEL_SUSPENDED);
		}

		userPanels.put(user.getId(), panel);

		updatePacksPanel(data, panel);

		return panel;
	}

	private void updateUserPanel(UserControlData data, UserPanel panel) {
		UserSession session = data.getSessions().isEmpty() ? new UserSession(null) : data.getSessions().get(0);
		updatePosition(session);

		BeanItem<UserSession> sessionBeanItem = new BeanItem<>(session);
		BeanItem<UserTestState> stateBeanItem = new BeanItem<>(
				session.getState() != null ? session.getState() : new UserTestState());

		panel.setPositionPropertyDataSource(sessionBeanItem.getItemProperty("position"));
		panel.setAddressPropertyDataSource(sessionBeanItem.getItemProperty("address"));

		panel.setMessagePropertyDataSource(stateBeanItem.getItemProperty("eventName"));
	}

	private void updatePosition(UserSession session) {
		if (session != null) {
			session.setPosition(session.getAddress() != null
					? Optional.of(session.getAddress())//
							.map(positionProperties::getProperty)//
							.filter(StringUtils::isNotBlank)//
							.orElse("<N/A>")//
					: null);
		}
	}

	private void updatePacksPanel(UserControlData data, UserPanel panel) {
		Panel packsPanel = panel.getPacksPanel();
		ComponentContainer container = (ComponentContainer) packsPanel.getContent();

		boolean isSuspended = data.getUser().isTestingSuspended();

		container.removeAllComponents();
		if (isSuspended) {
			packsPanel.addStyleName(USERPANEL_SUSPENDED);
		}

		for (Pack pack : data.getPacks()) {
			TinyPackPanel packPanel = new TinyPackPanel();
			BeanItem<Pack> packBeanItem = new BeanItem<Pack>(pack);
			packPanel.setDescriptionPropertyDataSource(packBeanItem.getItemProperty("name"));
			if (isSuspended) {
				packPanel.addStyleName(USERPANEL_SUSPENDED);
			}

			if (!data.getSessions().isEmpty()) {
				UserSession session = data.getSessions().get(0);
				if (session.getState() != null && pack.getId().equals(session.getState().getPackId())) {
					packPanel.removeStyleName(USERPANEL_SUSPENDED);
					packPanel.addStyleName(TINYPANEL_PROCESSING);
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

			if (UIMessageUtility.canHandle(uiMessage, getLoggedUser())) {
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
		positionProperties = VNAddressPositionManager.getProperties();

		setEmptyInfo();
		userPanels.clear();

		if (dateField.getValue() != null) {
			List<User> users = userService.findPlannedUsers(dateField.getValue());
			List<UserControlData> data = users.stream()//
					.map(userControlService::ensureUserControlData)//
					.map(userControlService::updateUserControlData)//
					.collect(Collectors.toList());

			data.forEach(ucd -> addUserPanel(createUserControlPanel(ucd)));
		}

		view.markAsDirty();
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
			UserControlData data = userControlService.ensureUserControlData(panel.getUser());
			userControlService.updateUserControlData(data);
			updateUserPanel(data, panel);
			updatePacksPanel(data, panel);
			panel.markAsDirty();
		}
	}

}