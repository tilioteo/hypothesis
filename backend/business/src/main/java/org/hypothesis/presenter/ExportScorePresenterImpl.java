/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import net.engio.mbassy.listener.Handler;
import org.hypothesis.business.CancelableExportRunnable;
import org.hypothesis.business.ExportScoreRunnableImpl;
import org.hypothesis.business.ExportThread;
import org.hypothesis.business.ThreadUtility;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Test;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.ExportScorePresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ExportScoreView;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class ExportScorePresenterImpl extends AbstractMainBusPresenter implements ExportScorePresenter {

    private final TestService testService;
    private final UserService userService;

    private VerticalLayout content;
    private VerticalLayout testSelection;
    private Button exportButton;
    private Button cancelExportButton;
    private ComboBox exportSelectionType;
    private PopupDateField dateFieldFrom;
    private PopupDateField dateFieldTo;
    private Table table;

    private boolean allTestsSelected = false;

    private HorizontalLayout toolsLayout;
    private ExportThread currentExport = null;
    private ProgressBar exportProgressBar = null;

    private ThreadGroup threadGroup = ThreadUtility.createExportGroup();

    public ExportScorePresenterImpl() {
        testService = TestService.newInstance();
        userService = UserService.newInstance();
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    @Override
    public Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setSpacing(true);

        Label title = new Label(Messages.getString("Caption.Label.ScoresExport"));
        title.addStyleName("huge");
        header.addComponent(title);
        header.addComponent(buildTools());
        header.setExpandRatio(title, 1);

        return header;
    }

    private Component buildTools() {
        toolsLayout = new HorizontalLayout();
        toolsLayout.setSpacing(true);

        buildExportControls();

        setExportSelection();

        return toolsLayout;
    }

    private void setExportSelection() {
        toolsLayout.removeAllComponents();

        CssLayout layout = new CssLayout();
        layout.addStyleName("v-component-group");

        layout.addComponent(exportSelectionType);
        layout.addComponent(exportButton);

        toolsLayout.addComponent(layout);
    }

    private void setExportProgressIndeterminate() {
        toolsLayout.removeAllComponents();

        exportProgressBar.setValue(0f);
        exportProgressBar.setIndeterminate(true);
        CssLayout layout = new CssLayout();
        layout.addComponent(exportProgressBar);

        cancelExportButton.setEnabled(false);

        toolsLayout.addComponent(layout);
        toolsLayout.addComponent(cancelExportButton);
    }

    private void setExportProgress() {
        toolsLayout.removeAllComponents();

        exportProgressBar.setIndeterminate(false);

        cancelExportButton.setEnabled(true);

        toolsLayout.addComponent(exportProgressBar);
        toolsLayout.addComponent(cancelExportButton);
    }

    private void buildExportControls() {
        buildSelection();
        buildExportButton();
        buildProgress();
        buildExportCancelButton();
    }

    private void buildExportCancelButton() {
        cancelExportButton = new Button(Messages.getString("Caption.Button.Cancel"),
                e -> getBus().post(new MainUIEvent.ExportFinishedEvent(true)));
    }

    private void buildProgress() {
        exportProgressBar = new ProgressBar();
        exportProgressBar.setCaption(Messages.getString("Caption.Label.ExportProgress"));
        exportProgressBar.setWidth("200px");
    }

    private void buildSelection() {
        exportSelectionType = new ComboBox();
        exportSelectionType.setTextInputAllowed(false);
        exportSelectionType.setNullSelectionAllowed(false);
        exportSelectionType.setEnabled(false);

        exportSelectionType.addItem(Messages.getString("Caption.Item.Selected"));
        exportSelectionType.addItem(Messages.getString("Caption.Item.All"));
        exportSelectionType.select(Messages.getString("Caption.Item.Selected"));

        exportSelectionType.addValueChangeListener(e -> {
            allTestsSelected = exportSelectionType.getValue().equals(Messages.getString("Caption.Item.All"));
            getBus().post(new MainUIEvent.PackSelectionChangedEvent());
        });
    }

    private void buildExportButton() {
        exportButton = new Button(Messages.getString("Caption.Button.Export"), e -> startExport());
        exportButton.setEnabled(false);

    }

    @SuppressWarnings("unchecked")
    private void startExport() {
        setExportProgressIndeterminate();

        final Collection<Long> testIds;
        if (allTestsSelected) {
            testIds = (Collection<Long>) table.getItemIds();
        } else {
            testIds = (Collection<Long>) table.getValue();
        }

        CancelableExportRunnable runnable = new ExportScoreRunnableImpl(getBus(), testIds, HibernateUtil::closeCurrent);

        currentExport = new ExportThread(threadGroup, runnable);
        currentExport.start();

        UI.getCurrent().setPollInterval(1000);
    }

    @Override
    public Component buildContent() {
        content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);

        content.addComponent(buildForm());

        testSelection = new VerticalLayout();
        testSelection.setSizeFull();
        content.addComponent(testSelection);
        content.setExpandRatio(testSelection, 1);

        Label infoLabel = new Label(Messages.getString("Caption.Label.ChoosePack"));
        infoLabel.setSizeUndefined();
        testSelection.addComponent(infoLabel);
        testSelection.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);

        return content;
    }

    private Component buildForm() {
        HorizontalLayout form = new HorizontalLayout();
        form.setWidth("100%");

        dateFieldFrom = new PopupDateField();
        dateFieldFrom.setResolution(Resolution.SECOND);
        dateFieldFrom.setDateFormat(Messages.getString("Format.DateTime"));
        dateFieldFrom.setInputPrompt(Messages.getString("Caption.Field.DateFrom"));
        dateFieldFrom.setImmediate(true);
        dateFieldFrom.setValidationVisible(false);
        form.addComponent(dateFieldFrom);

        dateFieldTo = new PopupDateField();
        dateFieldTo.setResolution(Resolution.SECOND);
        dateFieldTo.setDateFormat(Messages.getString("Format.DateTime"));
        dateFieldTo.setInputPrompt(Messages.getString("Caption.Field.DateTo"));
        dateFieldTo.setImmediate(true);
        dateFieldTo.setValidationVisible(false);
        form.addComponent(dateFieldTo);

        Validator dateValidator = value -> {
            if (dateFieldFrom.getValue() == null && dateFieldTo.getValue() == null) {
                throw new InvalidValueException(Messages.getString("Message.Error.NoDateSelected"));
            }
        };
        dateFieldFrom.addValidator(dateValidator);
        dateFieldTo.addValidator(dateValidator);

        Button selectionButton = new Button(Messages.getString("Caption.Button.ShowTests"));
        selectionButton.addClickListener(event -> {
            try {
                dateFieldFrom.validate();
                dateFieldTo.validate();

                Date dateFrom = dateFieldFrom.getValue();
                Date dateTo = dateFieldTo.getValue();

                showTests(dateFrom, dateTo);

            } catch (InvalidValueException e) {
                dateFieldFrom.setValidationVisible(!dateFieldFrom.isValid());
                dateFieldTo.setValidationVisible(!dateFieldTo.isValid());
                Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
            }
        });
        form.addComponent(selectionButton);

        return form;
    }

    protected void showTests(Date dateFrom, Date dateTo) {
        testSelection.removeAllComponents();

        // MANAGER see only tests created by himself and his users
        List<User> users = null;
        User loggedUser = getLoggedUser();
        if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
            users = userService.findOwnerUsers(loggedUser);
            users.add(loggedUser);
        }

        List<Test> tests = testService.findTestScoresBy(users, dateFrom, dateTo);

        if (tests.isEmpty()) {
            Label label = new Label(Messages.getString("Caption.Label.NoTestsFound"));
            label.setSizeUndefined();
            testSelection.addComponent(label);
            testSelection.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
            exportSelectionType.setEnabled(false);
        } else {
            testSelection.addComponent(buildTestsTable(tests));
            exportSelectionType.setEnabled(true);
            // bus.post(new MainUIEvent.PackSelectionChangedEvent());
        }
    }

    private Table buildTestsTable(Collection<Test> tests) {
        table = new Table();
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.setSizeFull();

        table.setSortContainerPropertyId(FieldConstants.ID);

        final BeanContainer<Long, Test> dataSource = new BeanContainer<>(Test.class);
        dataSource.setBeanIdProperty(FieldConstants.ID);
        dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_ID);
        dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_USERNAME);
        dataSource.addAll(tests);
        table.setContainerDataSource(dataSource);

        table.addGeneratedColumn(FieldConstants.PACK_ID, (source, itemId, columnId) -> {
            Test test = dataSource.getItem(itemId).getBean();
            return test.getPack().getId();
        });

        table.addGeneratedColumn(FieldConstants.USER_ID, (source, itemId, columnId) -> {
            Test test = dataSource.getItem(itemId).getBean();
            return test.getUser() != null ? test.getUser().getId() : null;
        });

        table.addGeneratedColumn(FieldConstants.USERNAME, (source, itemId, columnId) -> {
            Test test = dataSource.getItem(itemId).getBean();
            return test.getUser() != null ? test.getUser().getUsername() : null;
        });

        table.addGeneratedColumn(FieldConstants.STATUS, (source, itemId, columnId) -> {
            Test test = dataSource.getItem(itemId).getBean();
            Status status = test.getStatus();
            if (status != null) {
                switch (status) {
                    case CREATED:
                        return Messages.getString("Status.Created");
                    case STARTED:
                        return Messages.getString("Status.Started");
                    case BROKEN_BY_CLIENT:
                        return Messages.getString("Status.BrokenClient");
                    case BROKEN_BY_ERROR:
                        return Messages.getString("Status.BrokenError");
                    case FINISHED:
                        return Messages.getString("Status.Finished");
                    default:
                        break;
                }
            }
            return null;
        });

        table.setVisibleColumns(FieldConstants.ID, FieldConstants.PACK_ID, FieldConstants.USER_ID,
                FieldConstants.CREATED, FieldConstants.STATUS);

        table.setColumnHeaders(Messages.getString("Caption.Field.TestID"), Messages.getString("Caption.Field.PackID"),
                Messages.getString("Caption.Field.UserID"),
                Messages.getString("Caption.Field.Created"), Messages.getString("Caption.Field.Status"));

        table.addValueChangeListener(e -> getBus().post(new MainUIEvent.PackSelectionChangedEvent()));

        table.setPageLength(table.size());

        return table;
    }

    @SuppressWarnings("unchecked")
    @Handler
    public void setExportEnabled(final MainUIEvent.PackSelectionChangedEvent event) {
        boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
        boolean exportEnabled = allTestsSelected || itemsSelected;
        exportButton.setEnabled(exportEnabled);
    }

    @Handler
    public void updateExportProgress(final MainUIEvent.ExportProgressEvent event) {
        if (exportProgressBar.isIndeterminate() && event.getProgress() >= 0) {
            setExportProgress();
        }
        exportProgressBar.setValue(event.getProgress());
    }

    @Handler
    public void exportFinished(final MainUIEvent.ExportFinishedEvent event) {
        afterExportFinnished(event.isCanceled());
    }

    @Handler
    public void exportError(final MainUIEvent.ExportErrorEvent event) {
        afterExportFinnished(false);
        Notification.show("Export failed", null, Type.WARNING_MESSAGE);
    }

    @Handler
    public void changeUserPacks(final MainUIEvent.UserPacksChangedEvent event) {
    }

    private void afterExportFinnished(boolean canceled) {
        if (currentExport != null) {
            if (canceled) {
                currentExport.cancel();
            }
            currentExport = null;
        }
        setExportSelection();
        UI.getCurrent().setPollInterval(-1);
    }

    @Override
    public View createView() {
        return new ExportScoreView(this);
    }

}
