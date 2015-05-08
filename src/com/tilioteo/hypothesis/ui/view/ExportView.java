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
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "unchecked" })
public class ExportView extends VerticalLayout implements View {

	private static Logger log = Logger.getLogger(ExportView.class);

	PermissionManager permissionManager;
	TestManager testManager;
	PersistenceManager persistenceManager;
	ExportManager exportManager;
	UserManager userManager;
	
	User loggedUser;

	List<String> sortedPacks = new ArrayList<String>();
	HashMap<String, Pack> packMap = new HashMap<String, Pack>();

	VerticalLayout content;
	VerticalLayout testSelection;
	Button exportButton;
	ComboBox selectionType;
	ComboBox packsSelect;
	PopupDateField dateFieldFrom;
	PopupDateField dateFieldTo;
	Table table;
	
	boolean allTestsSelected = false;


	public ExportView() {
        permissionManager = PermissionManager.newInstance();
        testManager = TestManager.newInstance();
        persistenceManager = PersistenceManager.newInstance();
        exportManager = ExportManager.newInstance();
        userManager = UserManager.newInstance();

        loggedUser = (User) VaadinSession.getCurrent()
        		.getAttribute(User.class.getName());
        
        MainEventBus.get().register(this);

		setSizeFull();
		addComponent(buildHeader());
		Component content = buildContent();
		addComponent(content);
		setExpandRatio(content, 1);
	}

	private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setSpacing(true);
        header.setMargin(true);

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
        
        CssLayout buttonGroup = new CssLayout();
        buttonGroup.addStyleName("v-component-group");
        buttonGroup.addComponent(buildSelection());
        buttonGroup.addComponent(buildExportButton());
        tools.addComponent(buttonGroup);
        
        return tools;
    }
	
	private Component buildSelection() {
        selectionType = new ComboBox();
        selectionType.setTextInputAllowed(false);
        selectionType.setNullSelectionAllowed(false);
        selectionType.setEnabled(false);
        
        selectionType.addItem(Messages.getString("Caption.Item.Selected"));
        selectionType.addItem(Messages.getString("Caption.Item.All"));
        selectionType.select(Messages.getString("Caption.Item.Selected"));

        selectionType.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	allTestsSelected = selectionType.getValue().equals(
            			Messages.getString("Caption.Item.All"));
            	MainEventBus.get().post(new HypothesisEvent.TestSelectionChangedEvent());
            }
        });

        return selectionType;
	}
	
	private Component buildExportButton() {
		exportButton = new Button(Messages.getString("Caption.Button.Export"));
		exportButton.setEnabled(false);

		Resource exportResource = getExportResource();
		FileDownloader fileDownloader = new FileDownloader(exportResource);
        fileDownloader.extend(exportButton);
        
		return exportButton;
	}

	private Component buildContent() {
		content = new VerticalLayout();
		content.setSpacing(true);
		
		content.addComponent(buildForm());
		
		testSelection = new VerticalLayout();
		content.addComponent(testSelection);
		content.setExpandRatio(testSelection, 1);

		Label infoLabel = new Label(
				Messages.getString("Caption.Label.ChoosePack"));
		infoLabel.setSizeUndefined();
		testSelection.addComponent(infoLabel);
		testSelection.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
		
		return content;
	}

	private Component buildForm() {
		HorizontalLayout form = new HorizontalLayout();
		form.setMargin(true);
		form.setSpacing(true);
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
			String key = Messages.getString("Caption.Item.PackDescription",
					pack.getName(),	pack.getId(), pack.getDescription());
			sortedPacks.add(key);
			packMap.put(key, pack);
		}

		Collections.sort(sortedPacks);
	}

	protected void showTests(Pack pack, Date dateFrom, Date dateTo) {
		testSelection.removeAllComponents();
		testSelection.setSpacing(true);

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
			selectionType.setEnabled(false);
		} else {
			testSelection.addComponent(buildTestsTable(tests));
			selectionType.setEnabled(true);
			MainEventBus.get().post(new HypothesisEvent.TestSelectionChangedEvent());			
		}
	}

	private Table buildTestsTable(Collection<SimpleTest> tests) {
		table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.DEFAULT);
		table.setWidth("100%");
		
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
				return test.getUser().getId();
			}
		});
		
		table.addGeneratedColumn(FieldConstants.USERNAME, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SimpleTest test = dataSource.getItem(itemId).getBean();
				return test.getUser().getUsername();
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
            	MainEventBus.get().post(new HypothesisEvent.TestSelectionChangedEvent());
            }
        });
		
		table.setPageLength(table.size());

		return table;
	}

	private Resource getExportResource() {
		
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				Collection<Long> testIds = null;
				if (allTestsSelected) {
					testIds = (Collection<Long>) table.getItemIds();
				} else {
					testIds = (Collection<Long>) table.getValue();
				}
				return getExportFile(testIds);
    		}
		};

		String filename = Messages.getString("Caption.Export.TestFileName");
		StreamResource resource = new StreamResource(
				source, filename);

		return resource;
	}

	public InputStream getExportFile(Collection<Long> testIds) {
		log.debug("exportButtonClick");

		try {
			Long lastTestId = null;
			List<ExportEvent> events = exportManager.
					findExportEventsByTestId(testIds);

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
				header.createCell(0).setCellValue("date");
				header.createCell(1).setCellValue("test_id");
				header.createCell(2).setCellValue("user_id");
				header.createCell(3).setCellValue("event_id");
				header.createCell(4).setCellValue("event_timestamp");
				header.createCell(5).setCellValue("event_type");
				header.createCell(6).setCellValue("event_name");
				header.createCell(7).setCellValue("event_data");
				header.createCell(8).setCellValue("branch_id");
				header.createCell(9).setCellValue("task_id");
				header.createCell(10).setCellValue("slide_id");
				header.createCell(11).setCellValue("output_value1");
				header.createCell(12).setCellValue("output_value2");
				header.createCell(13).setCellValue("output_value3");
				header.createCell(14).setCellValue("output_value4");
				header.createCell(15).setCellValue("output_value5");
				header.createCell(16).setCellValue("output_value6");
				header.createCell(17).setCellValue("output_value7");
				header.createCell(18).setCellValue("output_value8");
				header.createCell(19).setCellValue("output_value9");
				header.createCell(20).setCellValue("output_value10");

				int outputValueCol = 11;
				int fieldCol = outputValueCol + 10;

				HashMap<String, Integer> fieldColumnMap = new HashMap<String, Integer>();

				int rowNr = 1;

				if (events != null) {
					for (ExportEvent event : events) {
						Long testId = event.getTestId();
						Long userId = event.getUserId();

						if (!testId.equals(lastTestId)) {
							if (lastTestId != null) {
								++rowNr;
							}
							lastTestId = testId;
						}

						Row row = sheet.createRow(rowNr++);

						Date eventDate = event.getDatetime();
						String eventName = event.getName();
						Long branchId = event.getBranchId();
						Long taskId = event.getTaskId();
						Long slideId = event.getSlideId();

						row.createCell(0).setCellValue(eventDate);
						// write test properties
						row.createCell(1).setCellValue(testId);
						if (userId != null) {
							row.createCell(2).setCellValue(userId);
						}

						// write event properties
						row.createCell(3).setCellValue(event.getId());
						row.createCell(4).setCellValue(eventDate.getTime());
						row.createCell(5).setCellValue(event.getType());
						row.createCell(6).setCellValue(eventName);
						row.createCell(7).setCellValue(event.getXmlData());

						if (branchId != null) {
							row.createCell(8).setCellValue(branchId);
						}
						if (taskId != null) {
							row.createCell(9).setCellValue(taskId);
						}
						if (slideId != null) {
							row.createCell(10).setCellValue(slideId);

							if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
								// write slide output properties
								String xmlData = event.getXmlData();
								int colNr;

								if (xmlData != null) {
									colNr = outputValueCol;
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
							}
						}
						++rowNr;
					}
				}

				FileOutputStream output = new FileOutputStream(tempFile);
	            workbook.write(output);
	            workbook.close();
	            output.close();

				return new FileInputStream(tempFile);

			} catch (IOException e) {
				log.error(e.getMessage());
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	@Subscribe
	public void setExportEnabled(final HypothesisEvent.TestSelectionChangedEvent event) {
    	boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
    	boolean exportEnabled = allTestsSelected || itemsSelected; 
    	exportButton.setEnabled(exportEnabled);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}

}
