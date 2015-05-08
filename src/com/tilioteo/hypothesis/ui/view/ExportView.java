package com.tilioteo.hypothesis.ui.view;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.core.SlideDataParser;
import com.tilioteo.hypothesis.entity.ExportEvent;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.ExportManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.PersistenceManager;
import com.tilioteo.hypothesis.persistence.RoleManager;
import com.tilioteo.hypothesis.persistence.TestManager;
import com.tilioteo.hypothesis.persistence.UserManager;
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
import com.vaadin.server.VaadinSession;
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

@SuppressWarnings({ "serial", "unchecked" })
public class ExportView extends VerticalLayout implements View {

	private static Logger log = Logger.getLogger(ExportView.class);

	PermissionManager permissionManager;
	TestManager testManager;
	PersistenceManager persistenceManager;
	UserManager userManager;
	
	User loggedUser;

	List<String> sortedPacks = new ArrayList<String>();
	HashMap<String, Pack> packMap = new HashMap<String, Pack>();

	VerticalLayout content;
	VerticalLayout testSelection;
	Button exportButton;
	Button cancelExportButton;
	ComboBox exportSelectionType;
	ComboBox packsSelect;
	PopupDateField dateFieldFrom;
	PopupDateField dateFieldTo;
	Table table;
	
	boolean allTestsSelected = false;

	private CssLayout exportControlLayout;
	private ExportThread currentExport = null;
	private ProgressBar exportProgressBar = null;

	public ExportView() {
        permissionManager = PermissionManager.newInstance();
        testManager = TestManager.newInstance();
        persistenceManager = PersistenceManager.newInstance();
        //exportManager = ExportManager.newInstance();
        userManager = UserManager.newInstance();

        loggedUser = (User) VaadinSession.getCurrent()
        		.getAttribute(User.class.getName());
        
        MainEventBus.get().register(this);

		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		addComponent(buildHeader());
		Component content = buildContent();
		addComponent(content);
		setExpandRatio(content, 1);
	}

	private Component buildHeader() {
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
		HorizontalLayout tools = new HorizontalLayout();
		tools.setSpacing(true);
        
		exportControlLayout = new CssLayout();
		exportControlLayout.addStyleName("v-component-group");
        
        buildExportControls();
        
        setExportSelection();
        
        tools.addComponent(exportControlLayout);
        
        return tools;
    }
	
	private void setExportSelection() {
		exportControlLayout.removeAllComponents();
		exportControlLayout.addComponent(exportSelectionType);
		exportControlLayout.addComponent(exportButton);
	}
	
	private void setExportProgress() {
		exportControlLayout.removeAllComponents();
		
		exportProgressBar.setValue(0f);
		exportProgressBar.setIndeterminate(true);
		cancelExportButton.setEnabled(false);
		
		exportControlLayout.addComponent(exportProgressBar);
		exportControlLayout.addComponent(cancelExportButton);
	}

	private void buildExportControls() {
		buildSelection();
		buildExportButton();
		buildProgress();
		buildExportCancelButton();
	}
	
	private void buildExportCancelButton() {
		cancelExportButton = new Button("Cancel", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				MainEventBus.get().post(new HypothesisEvent.ExportFinishedEvent(true));
			}
		});
	}

	private void buildProgress() {
		exportProgressBar = new ProgressBar();
		exportProgressBar.setCaption("Exporting...");
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
            	allTestsSelected = exportSelectionType.getValue().equals(
            			Messages.getString("Caption.Item.All"));
            	MainEventBus.get().post(new HypothesisEvent.PackSelectionChangedEvent());
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

		/*Resource exportResource = getExportResource();
		FileDownloader fileDownloader = new FileDownloader(exportResource);
        fileDownloader.extend(exportButton);*/
	}

	private void startExport() {
		setExportProgress();
		
		Collection<Long> testIds = null;
		if (allTestsSelected) {
			testIds = (Collection<Long>) table.getItemIds();
		} else {
			testIds = (Collection<Long>) table.getValue();
		}

		currentExport = new ExportThread(testIds);
		currentExport.start();
		
		UI.getCurrent().setPollInterval(1000);
		//getUI().access(currentExport);
	}

	private Component buildContent() {
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
				if (dateFieldFrom.getValue() == null &&
						dateFieldTo.getValue() == null) {
					throw new InvalidValueException(Messages.getString("Message.Error.NoDateSelected"));
				}

			}
		};
		dateFieldFrom.addValidator(dateValidator);
		dateFieldTo.addValidator(dateValidator);

		Button selectionButton = new Button(
				Messages.getString("Caption.Button.ShowTests"));
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
		Set<Pack> packs = permissionManager.findUserPacks2(loggedUser, false);

		sortedPacks.clear();
		packMap.clear();

		for (Pack pack : packs) {
			String key = Messages.getString("Caption.Item.PackSelect",
					pack.getName(),	pack.getId(), pack.getDescription());
			sortedPacks.add(key);
			packMap.put(key, pack);
		}

		Collections.sort(sortedPacks);
	}

	protected void showTests(Pack pack, Date dateFrom, Date dateTo) {
		testSelection.removeAllComponents();
		//testSelection.setSpacing(true);

		// MANAGER see only tests created by himself and his users
		List<User> users = null;
		if (!loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			users = userManager.findOwnerUsers(loggedUser);
			users.add(loggedUser);
		}

		List<SimpleTest> tests = testManager.findTestsBy(
				pack, users, dateFrom, dateTo);
		
		if (tests.size() == 0) {
			Label label = new Label(Messages.getString("Caption.Label.NoTestsFound"));
			label.setSizeUndefined();
			testSelection.addComponent(label);
			testSelection.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			exportSelectionType.setEnabled(false);
		} else {
			testSelection.addComponent(buildTestsTable(tests));
			exportSelectionType.setEnabled(true);
			MainEventBus.get().post(new HypothesisEvent.PackSelectionChangedEvent());			
		}
	}

	private Table buildTestsTable(Collection<SimpleTest> tests) {
		table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.DEFAULT);
		//table.setWidth("100%");
		table.setSizeFull();
		
		table.setSortContainerPropertyId(FieldConstants.ID);
		
		final BeanContainer<Long, SimpleTest> dataSource =
				new BeanContainer<Long, SimpleTest>(SimpleTest.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);
		dataSource.addNestedContainerProperty(
				FieldConstants.NESTED_USER_ID);
		dataSource.addNestedContainerProperty(
				FieldConstants.NESTED_USER_USERNAME);
		dataSource.addAll(tests);
		table.setContainerDataSource(dataSource);
		
		table.addGeneratedColumn(FieldConstants.USER_ID, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getId() : null;
			}
		});
		
		table.addGeneratedColumn(FieldConstants.USERNAME, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getUsername() : null;
			}
		});

		table.setVisibleColumns(FieldConstants.ID,
				FieldConstants.USER_ID,
				FieldConstants.USERNAME,
				FieldConstants.NESTED_USER_ID,
				FieldConstants.NESTED_USER_USERNAME,
				FieldConstants.CREATED);
		
		table.setColumnHeaders(Messages.getString("Caption.Field.TestID"),
				Messages.getString("Caption.Field.UserID"),
    			Messages.getString("Caption.Field.Username"),
				Messages.getString("Caption.Field.UserID"),
    			Messages.getString("Caption.Field.Username"),
    			Messages.getString("Caption.Field.Created"));
		
		table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
            	MainEventBus.get().post(new HypothesisEvent.PackSelectionChangedEvent());
            }
        });
		
		table.setPageLength(table.size());

		return table;
	}


	@Subscribe
	public void setExportEnabled(final HypothesisEvent.PackSelectionChangedEvent event) {
    	boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
    	boolean exportEnabled = allTestsSelected || itemsSelected; 
    	exportButton.setEnabled(exportEnabled);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	@Subscribe
	public void updateExportProgress(final HypothesisEvent.ExportProgressEvent event) {
		if (exportProgressBar.isIndeterminate() && event.getProgress() >= 0) {
			exportProgressBar.setIndeterminate(false);
			cancelExportButton.setEnabled(true);
		}
		exportProgressBar.setValue(event.getProgress());
	}
	
	@Subscribe
	public void exportFinished(final HypothesisEvent.ExportFinishedEvent event) {
		if (currentExport != null) {
			if (event.isCanceled()) {
				currentExport.cancel();
			}
			currentExport = null;
		}
		setExportSelection();
		UI.getCurrent().setPollInterval(-1);
	}

	private static class ExportThread extends Thread {
		
		private volatile int progress;
		
		final AtomicBoolean cancelPending = new AtomicBoolean(false);
		final Collection<Long> testIds;
		
		public ExportThread(final Collection<Long> testIds) {
			this.testIds = testIds;
		}
		
		@Override
		public void run() {
			Resource resource = getExportResource();
			com.tilioteo.hypothesis.ui.UI ui = com.tilioteo.hypothesis.ui.UI.getCurrent();
			if (resource != null && ui != null) {
				ui.setResource("export", resource);
				ResourceReference reference = ResourceReference.create(resource, ui, "export");

				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						MainEventBus.get().post(new HypothesisEvent.ExportFinishedEvent(false));
					}
				});
				
				Page.getCurrent().open(reference.getURL(), null);
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
			ExportManager exportManager = ExportManager.newInstance();
			
			try {
				List<ExportEvent> events = exportManager.findExportEventsByTestId(testIds);

				/*ExportManager exportManager = ExportManager.newInstance();
				List<ExportEvent> events = exportManager.findExportEventsBy(
						pack.getId(), dateFrom, dateTo);
				//log.debug(String.format("pack id = %d, test count = %d", selectedPack.getId(), tests.size()));
				// prepare xlsx*/

				try {
					File tempFile = File.createTempFile("htsm", null);

					SXSSFWorkbook workbook = new SXSSFWorkbook(100);
					Sheet sheet = workbook.createSheet();

					// write header
					Row header = sheet.createRow(0);
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
					header.createCell(13).setCellValue("task_order_pack");
					header.createCell(14).setCellValue("slide_order_task");
					header.createCell(15).setCellValue("event_timestamp");
					header.createCell(16).setCellValue("event_time_diff");
					header.createCell(17).setCellValue("event_type");
					header.createCell(18).setCellValue("event_name");
					header.createCell(19).setCellValue("event_data");

					header.createCell(20).setCellValue("output_value1");
					header.createCell(21).setCellValue("output_value2");
					header.createCell(22).setCellValue("output_value3");
					header.createCell(23).setCellValue("output_value4");
					header.createCell(24).setCellValue("output_value5");
					header.createCell(25).setCellValue("output_value6");
					header.createCell(26).setCellValue("output_value7");
					header.createCell(27).setCellValue("output_value8");
					header.createCell(28).setCellValue("output_value9");
					header.createCell(29).setCellValue("output_value10");

					if (events != null) {
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
						int outputValueCol = 20;
						int fieldCol = outputValueCol + 10;

						int rowNr = 1;
						int branchOrder = 0;
						int taskOrder = 0;
						int slideOrder = 0;

						for (ExportEvent event : events) {
							if (cancelPending.get()) {
								workbook.close();
								tempFile.delete();
								return null;
							}
							
							Long testId = event.getTestId();
							Long userId = event.getUserId();
							Date eventDate = event.getDatetime();
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
									lastBranchId = branchId;
									lastTaskId = null;
								}
							} else {
								lastBranchId = null;
							}
							
							if (taskId != null) {
								if (!taskId.equals(lastTaskId)) {
									++taskOrder;
									lastTaskId = taskId;
									lastSlideId = null;
								}
							} else {
								lastTaskId = null;
							}
							
							if (slideId != null) {
								if (!slideId.equals(lastSlideId)) {
									++slideOrder;
									lastSlideId = slideId;
								}
							} else {
								lastSlideId = null;
							}

							Row row = sheet.createRow(rowNr++);

							row.createCell(0).setCellValue(testId);
							row.createCell(1).setCellValue(eventDate);

							if (userId != null) {
								row.createCell(2).setCellValue(userId);
							}

							row.createCell(3).setCellValue(event.getId());
							row.createCell(4).setCellValue(event.getPackId());
							row.createCell(5).setCellValue(event.getPackName());

							if (branchId != null) {
								row.createCell(6).setCellValue(branchId);
							}
							if (branchName != null) {
								row.createCell(7).setCellValue(branchName);
							}
							if (taskId != null) {
								row.createCell(8).setCellValue(taskId);
							}
							if (taskName != null) {
								row.createCell(9).setCellValue(taskName);
							}
							if (slideId != null) {
								row.createCell(10).setCellValue(slideId);
							}
							if (slideName != null) {
								row.createCell(11).setCellValue(slideName);
							}
							
							if (branchId != null) {
								row.createCell(12).setCellValue(branchOrder);
							}
							if (taskId != null) {
								row.createCell(13).setCellValue(taskOrder);
							}
							if (slideId != null) {
								row.createCell(14).setCellValue(slideOrder);
							}
							
							// TODO event_timestamp
							relativeTime = eventTime - startTestTime;
							row.createCell(15).setCellValue(relativeTime);
							
							if (lastEventTime > 0) {
								diffTime = eventTime - lastEventTime;
								row.createCell(16).setCellValue(diffTime);
							}
							lastEventTime = eventTime;
							
							row.createCell(17).setCellValue(event.getType());
							row.createCell(18).setCellValue(eventName);
							
							String xmlData = event.getXmlData();
							row.createCell(19).setCellValue(xmlData);

							if (slideId != null && xmlData != null && "FINISH_SLIDE".equalsIgnoreCase(eventName)) {
								// write slide output properties
								int colNr = outputValueCol;

								List<String> outputValues = SlideDataParser.parseOutputValues(xmlData);
								for (String outputValue : outputValues) {
									if (outputValue != null) {
										row.createCell(colNr).setCellValue(outputValue);
									}
									++colNr;
								}

								Map<String, String> fieldMap = SlideDataParser.parseFields(xmlData);
								for (String fieldName : fieldMap.keySet()) {
									if (fieldColumnMap.containsKey(fieldName)) {
										colNr = fieldColumnMap.get(fieldName);
									} else {
										colNr = fieldCol++;
										fieldColumnMap.put(fieldName, colNr);
										header.createCell(colNr).setCellValue(fieldName);
									}
									row.createCell(colNr).setCellValue(fieldMap.get(fieldName));
								}
							}
							
							if ("FINISH_BRANCH".equalsIgnoreCase(eventName)) {
								lastBranchId = lastTaskId = lastSlideId = null; 
							}
							//++rowNr;
							++counter;
							
							progress = (int) ((100.0f * counter) / size);
							if (progress > lastProgress) {
								lastProgress = progress;

								UI.getCurrent().access(new Runnable() {
									@Override
									public void run() {
										MainEventBus.get().post(new HypothesisEvent.ExportProgressEvent(progress/100.0f));
									}
								});
							}
						}
					}

					FileOutputStream output = new FileOutputStream(tempFile);
		            workbook.write(output);
		            workbook.close();
		            output.close();

		            return new FileInputStream(tempFile);
					//return new FileResource(tempFile);

				} catch (IOException e) {
					log.error(e.getMessage());
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}
