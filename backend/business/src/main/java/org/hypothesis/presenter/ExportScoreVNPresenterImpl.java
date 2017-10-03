/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hypothesis.business.ExportRunnable;
import org.hypothesis.business.ExportScoreRunnableImpl;
import org.hypothesis.business.ExportThread;
import org.hypothesis.business.SessionManager;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Test;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.ExportScorePresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ExportScoreView;
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ExportScoreVNPresenterImpl implements ExportScorePresenter, HasMainEventBus {

	private final TestService testService;
	private final UserService userService;

	private User loggedUser;

	private MainEventBus bus;

	private VerticalLayout content;
	private VerticalLayout testSelection;
	private Button exportButton;
	private Button cancelExportButton;
	private ComboBox exportSelectionType;
	private PopupDateField dateFieldFrom;
	private PopupDateField dateFieldTo;
	private FilterTable table;

	private boolean allTestsSelected = false;

	private HorizontalLayout toolsLayout;
	private ExportThread currentExport = null;
	private ProgressBar exportProgressBar = null;

	private ThreadGroup threadGroup = new ThreadGroup("export-service");

	public ExportScoreVNPresenterImpl() {
		testService = TestService.newInstance();
		userService = UserService.newInstance();

	}

	@Override
	public void setMainEventBus(MainEventBus bus) {
		this.bus = bus;
	}

	@Override
	public MainEventBus getMainEventBus() {
		return bus;
	}

	@Override
	public void attach() {
		if (bus != null) {
			bus.register(this);
		}

		loggedUser = SessionManager.getLoggedUser();
		
		showCurrentDateSelection();
	}

	@Override
	public void detach() {
		if (bus != null) {
			bus.unregister(this);
		}
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
		cancelExportButton = new Button(Messages.getString("Caption.Button.Cancel"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				bus.post(new MainUIEvent.ExportFinishedEvent(true));
			}
		});
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

		exportSelectionType.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				allTestsSelected = exportSelectionType.getValue().equals(Messages.getString("Caption.Item.All"));
				bus.post(new MainUIEvent.PackSelectionChangedEvent());
			}
		});
	}

	private void buildExportButton() {
		exportButton = new Button(Messages.getString("Caption.Button.Export"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				startExport();
			}
		});
		exportButton.setEnabled(false);

	}

	@SuppressWarnings("unchecked")
	private void startExport() {
		setExportProgressIndeterminate();

		Collection<Long> testIds = null;
		if (allTestsSelected) {
			testIds = (Collection<Long>) table.getItemIds();
		} else {
			testIds = (Collection<Long>) table.getValue();
		}

		ExportRunnable runnable = new ExportScoreRunnableImpl(bus, testIds);
		runnable.setFinishCommand(new Command() {
			@Override
			public void execute() {
				HibernateUtil.closeCurrent();
			}
		});

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

		//initPacksSources();

		/*packsSelect = new ComboBox();
		packsSelect.setInputPrompt(Messages.getString("Caption.Button.ChoosePack"));
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
		packsSelect.setTextInputAllowed(false);
		packsSelect.setNullSelectionAllowed(false);
		packsSelect.setRequired(true);
		packsSelect.setRequiredError(Messages.getString("Message.Error.NoPackSelected"));
		packsSelect.setValidationVisible(false);
		form.addComponent(packsSelect);*/
		
		Date now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);

		dateFieldFrom = new PopupDateField(Messages.getString("Caption.Field.DateFrom"));
		//dateFieldFrom.setResolution(Resolution.DAY);
		dateFieldFrom.setDateFormat(Messages.getString("Format.Date"));
		dateFieldFrom.setInputPrompt(Messages.getString("Caption.Field.DateFrom"));
		dateFieldFrom.setImmediate(true);
		dateFieldFrom.setValidationVisible(false);
		dateFieldFrom.setValue(now);
		form.addComponent(dateFieldFrom);

		dateFieldTo = new PopupDateField(Messages.getString("Caption.Field.DateTo"));
		//dateFieldTo.setResolution(Resolution.DAY);
		dateFieldTo.setDateFormat(Messages.getString("Format.Date"));
		dateFieldTo.setInputPrompt(Messages.getString("Caption.Field.DateTo"));
		dateFieldTo.setImmediate(true);
		dateFieldTo.setValidationVisible(false);
		dateFieldTo.setValue(now);
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
		selectionButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					//packsSelect.validate();
					dateFieldFrom.validate();
					dateFieldTo.validate();

					showCurrentDateSelection();

				} catch (InvalidValueException e) {
					//packsSelect.setValidationVisible(!packsSelect.isValid());
					dateFieldFrom.setValidationVisible(!dateFieldFrom.isValid());
					dateFieldTo.setValidationVisible(!dateFieldTo.isValid());
					Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
				}
			}
		});
		form.addComponent(selectionButton);

		return form;
	}

	private void showCurrentDateSelection() {
		//Pack pack = packMap.get(packsSelect.getValue());
		Date dateFrom = (Date) dateFieldFrom.getValue();
		Date dateTo = (Date) dateFieldTo.getValue();

		showTests(dateFrom, dateTo);
	}

	/*private void initPacksSources() {
		Set<Pack> packs = permissionService.findUserPacks2(loggedUser, false);

		sortedPacks.clear();
		packMap.clear();

		if (packs != null) {
			for (Pack pack : packs) {
				String key = Messages.getString("Caption.Item.PackSelect", pack.getName(), pack.getId(),
						pack.getDescription());
				sortedPacks.add(key);
				packMap.put(key, pack);
			}

			Collections.sort(sortedPacks);
		}
	}*/

	protected void showTests(Date dateFrom, Date dateTo) {
		testSelection.removeAllComponents();
		// testSelection.setSpacing(true);

		// MANAGER see only tests created by himself and his users
		List<User> users = null;
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
			//bus.post(new MainUIEvent.PackSelectionChangedEvent());
		}
	}

	private Component buildTestsTable(Collection<Test> tests) {
		table = new FilterTable();

		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setColumnCollapsingAllowed(true);
		table.setSortContainerPropertyId(FieldConstants.USERNAME);

		table.setSortContainerPropertyId(FieldConstants.ID);

		final BeanContainer<Long, Test> dataSource = new BeanContainer<Long, Test>(Test.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_PACK_ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_USERNAME);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_NAME);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_PASSWORD);
		dataSource.addAll(tests);
		table.setContainerDataSource(dataSource);
		
		table.addGeneratedColumn(FieldConstants.STATUS, new ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Test test = dataSource.getItem(itemId).getBean();
				return getStatusName(test.getStatus());
			}
		});

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.NESTED_PACK_ID, FieldConstants.NESTED_USER_ID,
				FieldConstants.NESTED_USER_USERNAME, FieldConstants.NESTED_USER_NAME,
				FieldConstants.NESTED_USER_PASSWORD, FieldConstants.CREATED, FieldConstants.STATUS);

		table.setColumnHeaders(Messages.getString("Caption.Field.TestID"), Messages.getString("Caption.Field.PackID"),
				Messages.getString("Caption.Field.UserID"), Messages.getString("Caption.Field.Surname"),
				Messages.getString("Caption.Field.Name"), Messages.getString("Caption.Field.BirthNumber"),
				Messages.getString("Caption.Field.Created"), Messages.getString("Caption.Field.Status"));

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				bus.post(new MainUIEvent.PackSelectionChangedEvent());
			}
		});

		table.setPageLength(table.size());

		table.setFilterBarVisible(true);
		table.setFilterDecorator(new ScoreTableFilterDecorator());
		table.setFilterFieldVisible(FieldConstants.ID, false);
		table.setFilterFieldVisible(FieldConstants.CREATED, false);
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
		loggedUser = SessionManager.getLoggedUser();

		return new ExportScoreView(this);
	}
	
	private static String getStatusName(Status status) {
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

	public static class ScoreTableFilterDecorator implements FilterDecorator {

		@Override
		public String getEnumFilterDisplayName(Object propertyId, Object value) {
			if (FieldConstants.STATUS.equals(propertyId)) {
				return getStatusName((Status)value);
			}
			return null;
		}

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
			return null;
		}

		@Override
		public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
			return null;
		}

		@Override
		public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
			return null;
		}

		@Override
		public boolean isTextFilterImmediate(Object propertyId) {
			return true;
		}

		@Override
		public int getTextChangeTimeout(Object propertyId) {
			return 250;
		}

		@Override
		public String getFromCaption() {
			return null;
		}

		@Override
		public String getToCaption() {
			return null;
		}

		@Override
		public String getSetCaption() {
			return null;
		}

		@Override
		public String getClearCaption() {
			return null;
		}

		@Override
		public Resolution getDateFieldResolution(Object propertyId) {
			return null;
		}

		@Override
		public String getDateFormatPattern(Object propertyId) {
			return null;
		}

		@Override
		public Locale getLocale() {
			return null;
		}

		@Override
		public String getAllItemsVisibleString() {
			return null;
		}

		@Override
		public NumberFilterPopupConfig getNumberFilterPopupConfig() {
			return null;
		}

		@Override
		public boolean usePopupForNumericProperty(Object propertyId) {
			return false;
		}
		
	}
}
