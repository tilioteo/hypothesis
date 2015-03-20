/**
 * 
 */
package org.hypothesis.application.manager.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.core.DateUtil;
import org.hypothesis.core.SlideDataParser;
import org.hypothesis.entity.ExportEvent;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.ExportManager;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author kamil
 *
 */
public class ExportTests extends VerticalLayout implements Window.CloseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8380777018845635286L;
	
	private static Logger log = Logger.getLogger(ExportTests.class);

	private Table testsTable = ManagerApplication.getInstance().getTestsTable();
	
	List<String> sortedPacks = new ArrayList<String>();
	HashMap<String, Pack> packMap = new HashMap<String, Pack>();

	private NativeSelect packsSelect;
	private PopupDateField dateFieldFrom;
	private PopupDateField dateFieldTo;

	public ExportTests() {
		setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(Messages.TEXT_EXPORT_TESTS_TITLE)
				+ "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		// main layout
		VerticalLayout content = new VerticalLayout();
		content.setWidth("100%");
		content.setSpacing(true);
		addComponent(content);
		
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setSpacing(true);
		content.addComponent(buttonBar);

		//setTestsTable();
		//content.addComponent(testsTable);

		//
		User user = ManagerApplication.getInstance().getUserGroupManager()
				.findUser(ManagerApplication.getInstance().getCurrentUser().getId());
		
		Set<Pack> packs = ManagerApplication.getInstance().getPermissionManager()
				.findUserPacks2(user, false);
		sortedPacks.clear();
		packMap.clear();
		for (Pack pack : packs) {
			String key = String.format("%s (%d) - %s", pack.getName(), pack.getId(), pack.getDescription());
			sortedPacks.add(key);
			packMap.put(key, pack);
		}
		Collections.sort(sortedPacks);
		//
		
		packsSelect = new NativeSelect();
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
		packsSelect.setNullSelectionAllowed(false);
		packsSelect.setImmediate(true);
		
		dateFieldFrom = new PopupDateField();
		dateFieldFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dateFieldFrom.setInputPrompt(ApplicationMessages.get().getString(Messages.TEXT_EXPORT_DATE_FROM));
		dateFieldFrom.setImmediate(true);

		dateFieldTo = new PopupDateField();
		dateFieldTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dateFieldTo.setInputPrompt(ApplicationMessages.get().getString(Messages.TEXT_EXPORT_DATE_TO));
		dateFieldTo.setImmediate(true);

		Button exportButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_EXPORT));
		exportButton.addListener(Button.ClickEvent.class, this,
				"exportButtonClick");

		buttonBar.addComponent(packsSelect);
		buttonBar.addComponent(dateFieldFrom);
		buttonBar.addComponent(dateFieldTo);
		buttonBar.addComponent(exportButton);
	}

	private boolean isEmptyPackSelection() {
		return null == packsSelect.getValue();
	}
	
	private boolean isDateSelected() {
		return dateFieldFrom.getValue() != null || dateFieldTo.getValue() != null;
	}


	private String listToLine(ArrayList<String> arrayList) {
		String result = "";
		for (String item : arrayList) {
			if (result.length() > 0) {
				result += ",";
			}
			result += item;
		}
		return result;
	}
	
	public void exportButtonClick(Button.ClickEvent clickEvent) {
		log.debug("exportButtonClick");
		if (isEmptyPackSelection()) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.TEXT_NO_PACK_SELECTED));
			return;
		}
		
		if (!isDateSelected()) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.TEXT_DATE_FROM_OR_TO_NOT_SELECTED));
			return;
		}

		try {
			Long lastTestId = null;
			ExportManager exportManager = ExportManager.newInstance();
			Pack selectedPack = packMap.get(packsSelect.getValue());
			List<ExportEvent> events = exportManager.findExportEventsBy(
					selectedPack.getId(), DateUtil.removeTime((Date)dateFieldFrom.getValue()),
					DateUtil.removeTime((Date)dateFieldTo.getValue()));
			//log.debug(String.format("pack id = %d, test count = %d", selectedPack.getId(), tests.size()));
			// prepare xlsx

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
					}
				}
				
				FileOutputStream output = new FileOutputStream(tempFile);
	            workbook.write(output);
	            output.close();
	            
				final InputStream input = new FileInputStream(tempFile);
				StreamResource.StreamSource streamSource = new StreamResource.StreamSource() {
					private static final long serialVersionUID = -5733377807459934501L;

					public InputStream getStream() {
						return input;
					}
				};

				String filename = ApplicationMessages.get().getString(
						Messages.TEXT_EXPORT_TESTS_FILE_NAME);
				StreamResource resource = new StreamResource(streamSource,
						filename, ManagerApplication.getInstance());
				resource.getStream().setParameter("Content-Disposition",
						"attachment;filename=\"" + filename + "\"");
				ManagerApplication.getInstance().getMainWindow().open(resource);

			} catch (IOException e) {
				log.error(e.getMessage());
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the table of groups
	 */
	public void setTestsTable() {
		testsTable = ManagerApplication.getInstance().getTestsTable();

		// TODO
		/*
		testsTable.setSelectable(true);
		testsTable.setMultiSelect(true);
		testsTable.setImmediate(true);
		testsTable.setWidth("100%");
		testsTable.setNullSelectionAllowed(true);
		testsTable.setColumnCollapsingAllowed(true);
		testsTable.setSortContainerPropertyId(FieldConstants.NAME);

		testsTable.setVisibleColumns(new String[] { FieldConstants.ID,
				FieldConstants.NAME, FieldConstants.USERS,
				FieldConstants.AVAILABLE_PACKS, FieldConstants.NOTE, });
		testsTable.setColumnHeaders(new String[] {
				ApplicationMessages.get().getString(Messages.TEXT_ID),
				ApplicationMessages.get().getString(Messages.TEXT_NAME),
				ApplicationMessages.get().getString(Messages.TEXT_USER),
				ApplicationMessages.get().getString(Messages.TEXT_ENABLED_PACKS),
				ApplicationMessages.get().getString(Messages.TEXT_NOTE), });
		*/
	}

	@Override
	public void windowClose(CloseEvent e) {
		// nop
	}
	
}
