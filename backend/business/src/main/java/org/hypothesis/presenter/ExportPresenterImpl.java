/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.builder.SlideDataParser;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.ExportPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;
import org.hypothesis.ui.view.ExportView;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
public class ExportPresenterImpl implements ExportPresenter, HasMainEventBus {

	private static Logger log = Logger.getLogger(ExportPresenterImpl.class);

	private PermissionService permissionService;
	private TestService testService;
	private UserService userService;

	private User loggedUser;

	private MainEventBus bus;

	private List<String> sortedPacks = new ArrayList<String>();
	private HashMap<String, Pack> packMap = new HashMap<String, Pack>();

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

	public ExportPresenterImpl() {
		permissionService = PermissionService.newInstance();
		testService = TestService.newInstance();
		userService = UserService.newInstance();

	}

	@Override
	public void setMainEventBus(MainEventBus bus) {
		this.bus = bus;
	}

	@Override
	public void attach() {
		if (bus != null) {
			bus.register(this);
		}

		loggedUser = SessionManager.getLoggedUser();
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

		currentExport = new ExportThread(bus, testIds);
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
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
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
		selectionButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					packsSelect.validate();
					dateFieldFrom.validate();
					dateFieldTo.validate();

					Pack pack = packMap.get(packsSelect.getValue());
					Date dateFrom = (Date) dateFieldFrom.getValue();
					Date dateTo = (Date) dateFieldTo.getValue();

					showTests(pack, dateFrom, dateTo);

				} catch (InvalidValueException e) {
					packsSelect.setValidationVisible(!packsSelect.isValid());
					dateFieldFrom.setValidationVisible(!dateFieldFrom.isValid());
					dateFieldTo.setValidationVisible(!dateFieldTo.isValid());
					Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
				}
			}
		});
		form.addComponent(selectionButton);

		return form;
	}

	private void initPacksSources() {
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
	}

	protected void showTests(Pack pack, Date dateFrom, Date dateTo) {
		testSelection.removeAllComponents();
		// testSelection.setSpacing(true);

		// MANAGER see only tests created by himself and his users
		List<User> users = null;
		if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			users = userService.findOwnerUsers(loggedUser);
			users.add(loggedUser);
		}

		List<SimpleTest> tests = testService.findTestsBy(pack, users, dateFrom, dateTo);

		if (tests.size() == 0) {
			Label label = new Label(Messages.getString("Caption.Label.NoTestsFound"));
			label.setSizeUndefined();
			testSelection.addComponent(label);
			testSelection.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			exportSelectionType.setEnabled(false);
		} else {
			testSelection.addComponent(buildTestsTable(tests));
			exportSelectionType.setEnabled(true);
			bus.post(new MainUIEvent.PackSelectionChangedEvent());
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

		final BeanContainer<Long, SimpleTest> dataSource = new BeanContainer<Long, SimpleTest>(SimpleTest.class);
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
				bus.post(new MainUIEvent.PackSelectionChangedEvent());
			}
		});

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
		initPacksSources();

		packsSelect.removeAllItems();
		testSelection.removeAllComponents();
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
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

		return new ExportView(this);
	}

	private static class ExportThread extends Thread {

		private volatile int progress;

		final AtomicBoolean cancelPending = new AtomicBoolean(false);
		final Collection<Long> testIds;

		private MainEventBus bus;

		public ExportThread(MainEventBus bus, final Collection<Long> testIds) {
			this.bus = bus;
			this.testIds = testIds;
		}

		@Override
		public void run() {
			Resource resource = getExportResource();
			ControlledUI ui = ControlledUI.getCurrent();
			if (resource != null && ui != null) {
				ui.setResource("export", resource);
				ResourceReference reference = ResourceReference.create(resource, ui, "export");

				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						bus.post(new MainUIEvent.ExportFinishedEvent(false));
					}
				});

				Page.getCurrent().open(reference.getURL(), null);
			} else {

				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						bus.post(new MainUIEvent.ExportErrorEvent());
					}
				});
			}

		}

		public void cancel() {
			cancelPending.set(true);
		}

		private StreamResource getExportResource() {

			final InputStream inputStream = getExportFile();

			if (inputStream != null) {
				StreamResource.StreamSource source = new StreamResource.StreamSource() {
					@Override
					public InputStream getStream() {
						return inputStream;
					}
				};

				String filename = Messages.getString("Caption.Export.TestFileName");
				StreamResource resource = new StreamResource(source, filename);
				resource.setMIMEType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

				return resource;
			}

			return null;
		}

		private InputStream getExportFile() {
			ExportService exportService = ExportService.newInstance();

			try {
				List<ExportEvent> events = exportService.findExportEventsByTestId(testIds);

				if (events != null) {
					try {
						File tempFile = File.createTempFile("htsm", null);

						// maps hold informations for legend creation
						HashMap<String, String> fieldCaptionMap = new HashMap<String, String>();
						HashMap<String, HashMap<String, String>> fieldValueCaptionMap = new HashMap<String, HashMap<String, String>>();

						SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
						Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.TestSheetName"));

						// create cell style for date cell
						CreationHelper createHelper = workbook.getCreationHelper();
						CellStyle dateCellStyle = workbook.createCellStyle();
						dateCellStyle.setDataFormat(createHelper.createDataFormat()
								.getFormat(Messages.getString("Format.Export.DateTime")));

						// create header row and freeze it
						Row header = sheet.createRow(0);
						sheet.createFreezePane(0, 1);

						header.createCell(0).setCellValue("test_id");
						header.createCell(1).setCellValue("date");
						header.createCell(2).setCellValue("user_id");
						header.createCell(3).setCellValue("event_id");
						header.createCell(4).setCellValue("pack_id");
						header.createCell(5).setCellValue("pack_name");
						header.createCell(6).setCellValue("branch_id");
						header.createCell(7).setCellValue("branch_name");
						header.createCell(8).setCellValue("task_id");
						header.createCell(9).setCellValue("task_iname");
						header.createCell(10).setCellValue("slide_id");
						header.createCell(11).setCellValue("slide_name");
						header.createCell(12).setCellValue("branch_order_pack");
						header.createCell(13).setCellValue("branch_order");
						header.createCell(14).setCellValue("task_order_pack");
						header.createCell(15).setCellValue("slide_order_task");
						header.createCell(16).setCellValue("slide_order");
						header.createCell(17).setCellValue("event_timestamp");
						header.createCell(18).setCellValue("event_time_diff");
						header.createCell(19).setCellValue("client_timestamp");
						header.createCell(20).setCellValue("event_type");
						header.createCell(21).setCellValue("event_name");
						header.createCell(22).setCellValue("event_data");

						header.createCell(23).setCellValue("output_value1");
						header.createCell(24).setCellValue("output_value2");
						header.createCell(25).setCellValue("output_value3");
						header.createCell(26).setCellValue("output_value4");
						header.createCell(27).setCellValue("output_value5");
						header.createCell(28).setCellValue("output_value6");
						header.createCell(29).setCellValue("output_value7");
						header.createCell(30).setCellValue("output_value8");
						header.createCell(31).setCellValue("output_value9");
						header.createCell(32).setCellValue("output_value10");

						int size = events.size();
						float counter = 0f;
						int lastProgress = 0;

						Long lastTestId = null;
						Long lastBranchId = null;
						Long lastTaskId = null;
						Long lastSlideId = null;

						long startTestTime = 0;
						long relativeTime = 0;
						long lastEventTime = 0;
						long diffTime = 0;

						HashMap<String, Integer> fieldColumnMap = new HashMap<String, Integer>();
						HashMap<Long, Integer> branchCountMap = new HashMap<Long, Integer>();
						HashMap<Long, Integer> slideCountMap = new HashMap<Long, Integer>();

						int outputValueCol = 23;
						int fieldCol = outputValueCol + 10;

						int rowNr = 1;
						int branchOrder = 0;
						int taskOrder = 0;
						int slideOrder = 0;
						int branchCount = 0;
						int slideCount = 0;

						for (ExportEvent event : events) {
							if (cancelPending.get()) {
								workbook.close();
								tempFile.delete();
								return null;
							}

							Long testId = event.getTestId();
							Long userId = event.getUserId();
							Date eventDate = event.getDatetime();
							Date clientDate = event.getClientDatetime();
							long eventTime = eventDate.getTime();
							String eventName = event.getName();
							Long branchId = event.getBranchId();
							String branchName = event.getBranchName();
							Long taskId = event.getTaskId();
							String taskName = event.getTaskName();
							Long slideId = event.getSlideId();
							String slideName = event.getSlideName();

							if ("START_TEST".equalsIgnoreCase(eventName)) {
								startTestTime = eventTime;
								lastEventTime = 0;
							}

							if (!testId.equals(lastTestId)) {
								if (lastTestId != null) {
									++rowNr;
								}
								lastTestId = testId;
								branchOrder = taskOrder = slideOrder = 0;
								lastBranchId = lastTaskId = lastSlideId = null;
							}

							if (branchId != null) {
								if (!branchId.equals(lastBranchId)) {
									++branchOrder;

									Integer count = branchCountMap.get(branchId);
									if (null == count) {
										count = 0;
									}
									branchCount = ++count;
									branchCountMap.put(branchId, branchCount);

									lastBranchId = branchId;
									lastTaskId = null;
								}
							} else {
								lastBranchId = null;
							}

							if (taskId != null) {
								if (!taskId.equals(lastTaskId)) {
									++taskOrder;

									slideCountMap.clear();
									lastTaskId = taskId;
									lastSlideId = null;
								}
							} else {
								lastTaskId = null;
							}

							if (slideId != null) {
								if (!slideId.equals(lastSlideId)) {
									++slideOrder;

									Integer count = slideCountMap.get(slideId);
									if (null == count) {
										count = 0;
									}
									slideCount = ++count;
									slideCountMap.put(slideId, slideCount);

									lastSlideId = slideId;
								}
							} else {
								lastSlideId = null;
							}

							if ("FINISH_TEST".equalsIgnoreCase(eventName)) {
								branchId = null;
							}

							Row row = sheet.createRow(rowNr++);

							Cell cell = row.createCell(0);
							cell.setCellValue(testId);
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							cell = row.createCell(1);
							cell.setCellValue(eventDate);
							cell.setCellStyle(dateCellStyle);

							if (userId != null) {
								cell = row.createCell(2);
								cell.setCellValue(userId);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}

							cell = row.createCell(3);
							cell.setCellValue(event.getId());
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							cell = row.createCell(4);
							cell.setCellValue(event.getPackId());
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							cell = row.createCell(5);
							cell.setCellValue(event.getPackName());
							cell.setCellType(Cell.CELL_TYPE_STRING);

							if (branchId != null) {
								cell = row.createCell(6);
								cell.setCellValue(branchId);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (branchName != null) {
								cell = row.createCell(7);
								cell.setCellValue(branchName);
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}
							if (taskId != null) {
								cell = row.createCell(8);
								cell.setCellValue(taskId);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (taskName != null) {
								cell = row.createCell(9);
								cell.setCellValue(taskName);
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}
							if (slideId != null) {
								cell = row.createCell(10);
								cell.setCellValue(slideId);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (slideName != null) {
								cell = row.createCell(11);
								cell.setCellValue(slideName);
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							if (branchId != null) {
								cell = row.createCell(12);
								cell.setCellValue(branchOrder);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (branchId != null) {
								cell = row.createCell(13);
								cell.setCellValue(branchCount);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (taskId != null) {
								cell = row.createCell(14);
								cell.setCellValue(taskOrder);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if (slideId != null) {
								cell = row.createCell(15);
								cell.setCellValue(slideOrder);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}

							if (slideId != null) {
								cell = row.createCell(16);
								cell.setCellValue(slideCount);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}

							relativeTime = eventTime - startTestTime;
							cell = row.createCell(17);
							cell.setCellValue(relativeTime);
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							if (lastEventTime > 0) {
								diffTime = eventTime - lastEventTime;
								cell = row.createCell(18);
								cell.setCellValue(diffTime);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							lastEventTime = eventTime;

							if (clientDate != null) {
								cell = row.createCell(19);
								cell.setCellValue(clientDate.getTime());
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}

							cell = row.createCell(20);
							cell.setCellValue(event.getType());
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							cell = row.createCell(21);
							cell.setCellValue(eventName);
							cell.setCellType(Cell.CELL_TYPE_STRING);

							String xmlData = event.getData();
							cell = row.createCell(22);
							cell.setCellValue(xmlData);
							cell.setCellType(Cell.CELL_TYPE_STRING);

							if (slideId != null && xmlData != null) {
								int colNr = outputValueCol;

								if ("FINISH_SLIDE".equalsIgnoreCase(eventName)
										|| "ACTION".equalsIgnoreCase(eventName)) {
									// write output properties

									List<String> outputValues = SlideDataParser.parseOutputValues(xmlData);
									for (String outputValue : outputValues) {
										if (outputValue != null) {
											row.createCell(colNr).setCellValue(outputValue);
										}
										++colNr;
									}
								}

								if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
									SlideDataParser.FieldWrapper wrapper = SlideDataParser.parseFields(xmlData);
									Map<String, String> fieldCaptions = wrapper.getFieldCaptionMap();
									Map<String, String> fieldValues = wrapper.getFieldValueMap();
									Map<String, Map<String, String>> fieldValueCaptions = wrapper
											.getFieldValueCaptionMap();

									for (String fieldName : fieldCaptions.keySet()) {
										if (fieldColumnMap.containsKey(fieldName)) {
											colNr = fieldColumnMap.get(fieldName);
										} else {
											colNr = fieldCol++;
											fieldColumnMap.put(fieldName, colNr);
											String fieldCaption = fieldCaptions.get(fieldName);
											if (fieldCaption != null) {
												fieldCaptionMap.put(fieldName, fieldCaption);
											}
											header.createCell(colNr).setCellValue(fieldName);
										}
										String fieldValue = fieldValues.get(fieldName);

										Map<String, String> valueCaptions = fieldValueCaptions.get(fieldName);
										if (valueCaptions != null) {
											String valueCaption = valueCaptions.get(fieldValue);
											if (valueCaption != null) {
												HashMap<String, String> valueCaptionMap = fieldValueCaptionMap
														.get(fieldName);
												if (null == valueCaptionMap) {
													valueCaptionMap = new HashMap<String, String>();
													fieldValueCaptionMap.put(fieldName, valueCaptionMap);
												}
												if (!valueCaptionMap.containsKey(fieldValue)) {
													valueCaptionMap.put(fieldValue, valueCaption);
												}
											}
										}
										row.createCell(colNr).setCellValue(fieldValue);
									}
								}
							}

							if ("NEXT_SLIDE".equalsIgnoreCase(eventName)) {
								lastSlideId = null;
							}

							if ("NEXT_BRANCH".equalsIgnoreCase(eventName)) {
								lastBranchId = lastTaskId = lastSlideId = null;
							}
							++counter;

							progress = (int) ((100.0f * counter) / size);
							if (progress > lastProgress) {
								lastProgress = progress;

								UI.getCurrent().access(new Runnable() {
									@Override
									public void run() {
										bus.post(new MainUIEvent.ExportProgressEvent(progress / 100.0f));
									}
								});
							}
						}

						// create legend sheet only if there are some
						// informations gathered
						if (!fieldCaptionMap.isEmpty() || !fieldValueCaptionMap.isEmpty()) {
							sheet = workbook.createSheet(Messages.getString("Caption.Export.LegendSheetName"));
							rowNr = 0;
							Row row;
							if (!fieldCaptionMap.isEmpty()) {
								row = sheet.createRow(rowNr++);
								row.createCell(0).setCellValue(Messages.getString("Caption.Export.UserColumns"));

								row = sheet.createRow(rowNr++);
								row.createCell(0).setCellValue(Messages.getString("Caption.Export.ColumnName"));
								row.createCell(1).setCellValue(Messages.getString("Caption.Export.ColumnDescription"));

								for (String fieldName : fieldCaptionMap.keySet()) {
									row = sheet.createRow(rowNr++);
									row.createCell(0).setCellValue(fieldName);
									row.createCell(1).setCellValue(fieldCaptionMap.get(fieldName));
								}
								++rowNr;
							}

							if (!fieldValueCaptionMap.isEmpty()) {
								row = sheet.createRow(rowNr++);
								row.createCell(0).setCellValue(Messages.getString("Caption.Export.UserColumnValues"));

								for (String fieldName : fieldValueCaptionMap.keySet()) {
									row = sheet.createRow(rowNr++);
									row.createCell(0).setCellValue(Messages.getString("Caption.Export.ColumnName"));
									row.createCell(1).setCellValue(fieldName);

									row = sheet.createRow(rowNr++);
									row.createCell(0).setCellValue(Messages.getString("Caption.Export.UserValue"));
									row.createCell(1)
											.setCellValue(Messages.getString("Caption.Export.UserValueDescription"));

									HashMap<String, String> valueCaptions = fieldValueCaptionMap.get(fieldName);
									for (String value : valueCaptions.keySet()) {
										row = sheet.createRow(rowNr++);
										row.createCell(0).setCellValue(value);
										row.createCell(1).setCellValue(valueCaptions.get(value));
									}
									++rowNr;
								}

							}
							sheet.autoSizeColumn(0);
							sheet.autoSizeColumn(1);
						}

						// finalize file creation
						FileOutputStream output = new FileOutputStream(tempFile);
						workbook.write(output);
						workbook.close();
						output.close();

						return new FileInputStream(tempFile);

					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}

			} catch (Throwable e) {
				log.error(e.getMessage());
			}

			return null;
		}
	}

}
