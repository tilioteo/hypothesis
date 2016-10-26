/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.ExportPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ExportView;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
@Default
public class ExportPresenterImpl implements ExportPresenter {

	static final Logger log = Logger.getLogger(ExportPresenterImpl.class);

	@Inject
	private PermissionService permissionService;
	
	@Inject
	private TestService testService;
	
	@Inject
	private UserService userService;

	@Inject
	private Event<MainUIEvent> mainEvent;

	private List<String> sortedPacks = new ArrayList<>();
	private HashMap<String, Pack> packMap = new HashMap<>();

	private VerticalLayout content;
	private VerticalLayout testSelection;
	private Button exportButton;
	private Button cancelExportButton;
	private ComboBox exportSelectionType;
	private ComboBox packsSelect;
	private PopupDateField dateFieldFrom;
	private PopupDateField dateFieldTo;
	private Table table;

	boolean allTestsSelected = false;

	private HorizontalLayout toolsLayout;
	private ExportThread currentExport = null;
	private ProgressBar exportProgressBar = null;

	@Override
	public void enter(ViewChangeEvent event) {
		// nop
	}

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.TestsExport"));
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
				e -> bus.post(new MainUIEvent.ExportFinishedEvent(true)));
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
				mainEvent.fire(new MainUIEvent.PackSelectionChangedEvent());
		});
	}

	private void buildExportButton() {
		exportButton = new Button(Messages.getString("Caption.Button.Export"), e -> startExport());
		exportButton.setEnabled(false);

	}

	@SuppressWarnings("unchecked")
	private void startExport() {
		setExportProgressIndeterminate();

		Collection<Long> testIds;
		if (allTestsSelected) {
			testIds = (Collection<Long>) table.getItemIds();
		} else {
			testIds = (Collection<Long>) table.getValue();
		}

		currentExport = new ExportThread(testIds);
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

		initPacksSources();

		packsSelect = new ComboBox();
		packsSelect.setInputPrompt(Messages.getString("Caption.Button.ChoosePack"));
		sortedPacks.forEach(packsSelect::addItem);

		packsSelect.setTextInputAllowed(false);
		packsSelect.setNullSelectionAllowed(false);
		packsSelect.setRequired(true);
		packsSelect.setRequiredError(Messages.getString("Message.Error.NoPackSelected"));
		packsSelect.setValidationVisible(false);
		form.addComponent(packsSelect);

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

		Validator dateValidator = new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (dateFieldFrom.getValue() == null && dateFieldTo.getValue() == null) {
					throw new InvalidValueException(Messages.getString("Message.Error.NoDateSelected"));
				}

			}
		};
		dateFieldFrom.addValidator(dateValidator);
		dateFieldTo.addValidator(dateValidator);

		Button selectionButton = new Button(Messages.getString("Caption.Button.ShowTests"));
		selectionButton.addClickListener(e -> {
				try {
					packsSelect.validate();
					dateFieldFrom.validate();
					dateFieldTo.validate();

					Pack pack = packMap.get(packsSelect.getValue());
					Date dateFrom = dateFieldFrom.getValue();
					Date dateTo = dateFieldTo.getValue();

					showTests(pack, dateFrom, dateTo);

			} catch (InvalidValueException ex) {
					packsSelect.setValidationVisible(!packsSelect.isValid());
					dateFieldFrom.setValidationVisible(!dateFieldFrom.isValid());
					dateFieldTo.setValidationVisible(!dateFieldTo.isValid());
				Notification.show(ex.getMessage(), Type.WARNING_MESSAGE);
				}
		});
		form.addComponent(selectionButton);

		return form;
	}

	private void initPacksSources() {
		User user = SessionManager.getLoggedUser();
		Set<Pack> packs = permissionService.findUserPacks2(user, false);

		sortedPacks.clear();
		packMap.clear();

		if (packs != null) {
			packs.forEach(e -> {
				String key = Messages.getString("Caption.Item.PackSelect", e.getName(), e.getId(), e.getDescription());
				sortedPacks.add(key);
				packMap.put(key, e);
			});

			Collections.sort(sortedPacks);
		}
	}

	protected void showTests(Pack pack, Date dateFrom, Date dateTo) {
		User user = SessionManager.getLoggedUser();
		
		testSelection.removeAllComponents();
		// testSelection.setSpacing(true);

		// MANAGER see only tests created by himself and his users
		List<User> users = null;
		if (!user.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			users = userService.findOwnerUsers(user);
			users.add(user);
		}

		List<SimpleTest> tests = testService.findTestsBy(pack, users, dateFrom, dateTo);

		if (tests.isEmpty()) {
			Label label = new Label(Messages.getString("Caption.Label.NoTestsFound"));
			label.setSizeUndefined();
			testSelection.addComponent(label);
			testSelection.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			exportSelectionType.setEnabled(false);
		} else {
			testSelection.addComponent(buildTestsTable(tests));
			exportSelectionType.setEnabled(true);
			mainEvent.fire(new MainUIEvent.PackSelectionChangedEvent());
		}
	}

	private Table buildTestsTable(Collection<SimpleTest> tests) {
		table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.DEFAULT);
		// table.setWidth("100%");
		table.setSizeFull();

		table.setSortContainerPropertyId(FieldConstants.ID);

		final BeanContainer<Long, SimpleTest> dataSource = new BeanContainer<>(SimpleTest.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_USERNAME);
		dataSource.addAll(tests);
		table.setContainerDataSource(dataSource);

		table.addGeneratedColumn(FieldConstants.USER_ID, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getId() : null;
			}
		});

		table.addGeneratedColumn(FieldConstants.USERNAME, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getUsername() : null;
			}
		});

		table.addGeneratedColumn(FieldConstants.STATUS, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
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
			}
		});

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.USER_ID,
				// FieldConstants.USERNAME,
				// FieldConstants.NESTED_USER_ID,
				// FieldConstants.NESTED_USER_USERNAME,
				FieldConstants.CREATED, FieldConstants.STATUS);

		table.setColumnHeaders(Messages.getString("Caption.Field.TestID"), Messages.getString("Caption.Field.UserID"),
				// Messages.getString("Caption.Field.Username"),
				// Messages.getString("Caption.Field.UserID"),
				// Messages.getString("Caption.Field.Username"),
				Messages.getString("Caption.Field.Created"), Messages.getString("Caption.Field.Status"));

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				mainEvent.fire(new MainUIEvent.PackSelectionChangedEvent());
			}
		});

		table.setPageLength(table.size());

		return table;
	}

	@SuppressWarnings("unchecked")
	public void setExportEnabled(@Observes final MainUIEvent.PackSelectionChangedEvent event) {
		boolean itemsSelected = !((Set<Object>) table.getValue()).isEmpty();
		boolean exportEnabled = allTestsSelected || itemsSelected;
		exportButton.setEnabled(exportEnabled);
	}

	/**
	 * Update progress of export
	 * 
	 * @param event
	 */
	public void updateExportProgress(@Observes final MainUIEvent.ExportProgressEvent event) {
		if (exportProgressBar.isIndeterminate() && event.getProgress() >= 0) {
			setExportProgress();
		}
		exportProgressBar.setValue(event.getProgress());
	}

	/**
	 * Do when export finished
	 * 
	 * @param event
	 */
	public void exportFinished(@Observes final MainUIEvent.ExportFinishedEvent event) {
		afterExportFinnished(event.isCanceled());
	}

	/**
	 * Show error occured during export
	 * 
	 * @param event
	 */
	public void exportError(@Observes final MainUIEvent.ExportErrorEvent event) {
		afterExportFinnished(false);
		Notification.show("Export failed", null, Type.WARNING_MESSAGE);
	}

	/**
	 * Update ui on user pack change
	 * 
	 * @param event
	 */
	public void changeUserPacks(@Observes final MainUIEvent.UserPacksChangedEvent event) {
		initPacksSources();

		packsSelect.removeAllItems();
		testSelection.removeAllComponents();

		sortedPacks.forEach(packsSelect::addItem);
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

	/*@Override
	public View createView() {
		loggedUser = SessionManager.getLoggedUser();

		return new ExportView(this);
	}*/

}
