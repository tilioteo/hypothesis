/**
 * 
 */
package org.hypothesis.application.manager.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.DateFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.application.ApplicationsContext;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.core.DateUtil;
import org.hypothesis.core.SlideOutputParser;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.SlideOutput;
import org.hypothesis.entity.Test;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.TestManager;

import au.com.bytecode.opencsv.CSVWriter;

import com.vaadin.data.util.BeanItem;
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
import com.vaadin.ui.themes.BaseTheme;

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
	
	public void exportButtonClick(Button.ClickEvent event) {
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
			TestManager testManager = ManagerApplication.getInstance().getTestManager();
			Pack selectedPack = packMap.get(packsSelect.getValue());
			List<Test> tests = testManager.findFinishedTestsBy(
					selectedPack, DateUtil.removeTime((Date)dateFieldFrom.getValue()),
					DateUtil.removeTime((Date)dateFieldTo.getValue()));
			log.debug(String.format("pack id = %d, test count = %d", selectedPack.getId(), tests.size()));
			/*
			//prepare csv file
			try {
				File tempFile = File.createTempFile("htsm", null);
				File tempFile2 = File.createTempFile("htsm", null);
				//CSVWriter csvOutput = new CSVWriter(new FileWriter(tempFile));
				BufferedWriter csv = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
				
				HashMap<String, Integer> fieldColumnMap = new HashMap<String, Integer>();
				ArrayList<String> header = new ArrayList<String>();
				header.add("date");
				header.add("test_id");
				header.add("user_id");
				header.add("event_id");
				header.add("event_timestamp");
				header.add("event_type");
				header.add("event_name");
				header.add("event_data");
				header.add("branch_id");
				header.add("task_id");
				header.add("slide_id");
				header.add("slide_output");
				header.add("slide_data");
				int fieldCol = 12;
				
				if (tests != null) {
					for (Test test : tests) {
						Long testId = test.getId();
						Long userId = test.getUser() != null ? test.getUser().getId() : null;
						
						List<org.hypothesis.entity.Event> events = test.getEvents();
						for (org.hypothesis.entity.Event testEvent : events) {
							if (testEvent != null) {
								ArrayList<String> row = new ArrayList<String>();
								
								Slide slide = testEvent.getSlide();
								Date eventDate = testEvent.getDatetime();
								String eventName = testEvent.getName();
								Long branchId = testEvent.getBranch() != null ? testEvent.getBranch().getId() : null;
								Long taskId = testEvent.getTask() != null ? testEvent.getTask().getId() : null;
								Long slideId = testEvent.getSlide() != null ? testEvent.getSlide().getId() : null;
								
								row.add(eventDate.toString()); // 0
								// write test properties
								row.add(testId.toString()); // 1
								if (userId != null) { // 2
									row.add(userId.toString());
								} else {
									row.add("");
								}
								
								// write event properties
								row.add(testEvent.getId().toString()); // 3
								row.add(String.format("%d", eventDate.getTime())); // 4
								row.add(testEvent.getType().toString()); // 5
								row.add(eventName); // 6
								row.add(testEvent.getXmlData()); // 7
								if (branchId != null) { // 8
									row.add(branchId.toString());
								} else {
									row.add("");
								}
								if (taskId != null) { // 9
									row.add(taskId.toString());
								} else {
									row.add("");
								}
								if (slideId != null) { // 10
									row.add(slideId.toString());
								} else {
									row.add("");
								}
								
								if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
									SlideOutput slideOutput = testManager.findSlideOutput(test, slide);
									// write slide output properties
									if (slideOutput != null) {
										String outputString = SlideOutputParser.parseOutput(slideOutput.getOutput());
										if (outputString != null) { // 11
											row.add(outputString);
										} else {
											row.add("");
										}
										
										String xmlData = slideOutput.getXmlData();
										if (xmlData != null) { // 12
											row.add(xmlData);
										} else {
											row.add("");
										}
										Map<String, String> fieldMap = SlideOutputParser.parseData(xmlData);
										
										for (String fieldName : fieldMap.keySet()) {
											int colNr; 
											if (fieldColumnMap.containsKey(fieldName)) {
												colNr = fieldColumnMap.get(fieldName);
												if (colNr >= row.size()) {
													for (int i = row.size(); i < colNr; ++i) {
														row.add("");
													}
													row.add(fieldMap.get(fieldName));
												} else {
													row.set(colNr, fieldMap.get(fieldName));
												}
											} else {
												colNr = ++fieldCol;
												fieldColumnMap.put(fieldName, colNr);
												// append column header title
												header.add(fieldName);
												row.add(fieldMap.get(fieldName));
											}
										}
									}
								} else {
									row.add("");
									row.add("");
								}
								// write row
								String rowString = listToLine(row);
								csv.write(rowString);
								//csvOutput.writeNext(row.toArray(new String[0]));
							}
						}
						// write empty row
						csv.newLine();
						//csvOutput.writeNext(new String[] {""});
					}
					csv.close();
					//csvOutput.close();
					// write header
					String headerString = listToLine(header);
					
					try {
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile2), "UTF8"));
						
						out.write(headerString);
						out.newLine();
						
						BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
						String line;
						while ((line = in.readLine()) != null) {
							out.write(line);
							out.newLine();
						}
						
						in.close();
						out.close();
					} catch (IOException e) {
						// TODO: handle exception
					}
				}
				
				final InputStream input = new FileInputStream(tempFile2);
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

			} catch(IOException e) {
				ManagerApplication.getInstance().getMainWindow().showError(
						ApplicationMessages.get().getString(Messages.ERROR_EXPORT_CANNOT_CREATE_FILE));
			}
			*/
			
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
				header.createCell(11).setCellValue("slide_output");
				header.createCell(12).setCellValue("slide_data");
				
				int fieldCol = 12;
				HashMap<String, Integer> fieldColumnMap = new HashMap<String, Integer>();
	
				int rowNr = 1;
				
				if (tests != null) {
					for (Test test : tests) {
						Long testId = test.getId();
						Long userId = test.getUser() != null ? test.getUser().getId() : null;
						
						List<org.hypothesis.entity.Event> events = test.getEvents();
						for (org.hypothesis.entity.Event testEvent : events) {
							if (testEvent != null) {
								Row row = sheet.createRow(rowNr++);
								
								Slide slide = testEvent.getSlide();
								Date eventDate = testEvent.getDatetime();
								String eventName = testEvent.getName();
								Long branchId = testEvent.getBranch() != null ? testEvent.getBranch().getId() : null;
								Long taskId = testEvent.getTask() != null ? testEvent.getTask().getId() : null;
								Long slideId = testEvent.getSlide() != null ? testEvent.getSlide().getId() : null;
								
								row.createCell(0).setCellValue(eventDate);
								// write test properties
								row.createCell(1).setCellValue(testId);
								if (userId != null) {
									row.createCell(2).setCellValue(userId);
								}
								
								// write event properties
								row.createCell(3).setCellValue(testEvent.getId());
								row.createCell(4).setCellValue(eventDate.getTime());
								row.createCell(5).setCellValue(testEvent.getType());
								row.createCell(6).setCellValue(eventName);
								row.createCell(7).setCellValue(testEvent.getXmlData());
	
								if (branchId != null) {
									row.createCell(8).setCellValue(branchId);
								}
								if (taskId != null) {
									row.createCell(9).setCellValue(taskId);
								}
								if (slideId != null) {
									row.createCell(10).setCellValue(slideId);
								}
								
								if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
									SlideOutput slideOutput = testManager.findSlideOutput(test, slide);
									// write slide output properties
									if (slideOutput != null) {
										String outputString = SlideOutputParser.parseOutput(slideOutput.getOutput());
										if (outputString != null) {
											row.createCell(11).setCellValue(outputString);
										}
										String xmlData = slideOutput.getXmlData();
										if (xmlData != null) {
											row.createCell(12).setCellValue(xmlData);
										}
										Map<String, String> fieldMap = SlideOutputParser.parseData(xmlData);
										
										for (String fieldName : fieldMap.keySet()) {
											int colNr; 
											if (fieldColumnMap.containsKey(fieldName)) {
												colNr = fieldColumnMap.get(fieldName);
											} else {
												colNr = ++fieldCol;
												fieldColumnMap.put(fieldName, colNr);
												header.createCell(colNr).setCellValue(fieldName);
											}
											row.createCell(colNr).setCellValue(fieldMap.get(fieldName));
										}
									}
								}
								//++rowNr;
							}
						}
						++rowNr;
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
	
			/*
			// prepare excel
			OutputStream output = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(output);
			WritableSheet sheet = workbook.createSheet(ApplicationMessages.get()
					.getString(Messages.TEXT_EXPORT_TESTS_SHEET_NAME), 0);

			// header line
			sheet.addCell(new jxl.write.Label(0, 0, "date"));
			sheet.addCell(new jxl.write.Label(1, 0, "test_id"));
			sheet.addCell(new jxl.write.Label(2, 0, "user_id"));
			sheet.addCell(new jxl.write.Label(3, 0, "event_id"));
			sheet.addCell(new jxl.write.Label(4, 0, "event_timestamp"));
			sheet.addCell(new jxl.write.Label(5, 0, "event_type"));
			sheet.addCell(new jxl.write.Label(6, 0, "event_name"));
			sheet.addCell(new jxl.write.Label(7, 0, "event_data"));
			sheet.addCell(new jxl.write.Label(8, 0, "branch_id"));
			sheet.addCell(new jxl.write.Label(9, 0, "task_id"));
			sheet.addCell(new jxl.write.Label(10, 0, "slide_id"));
			sheet.addCell(new jxl.write.Label(11, 0, "slide_output"));
			//sheet.addCell(new jxl.write.Label(12, 0, "slide_data"));
			
			int fieldCol = 12;
			HashMap<String, Integer> fieldColumnMap = new HashMap<String, Integer>();

			int rowNr = 1;
			
			if (tests != null) {
				for (Test test : tests) {
					Long testId = test.getId();
					Long userId = test.getUser() != null ? test.getUser().getId() : null;
					
					List<org.hypothesis.entity.Event> events = test.getEvents();
					for (org.hypothesis.entity.Event testEvent : events) {
						if (testEvent != null) {
							Slide slide = testEvent.getSlide();
							Date eventDate = testEvent.getDatetime();
							String eventName = testEvent.getName();
							Long branchId = testEvent.getBranch() != null ? testEvent.getBranch().getId() : null;
							Long taskId = testEvent.getTask() != null ? testEvent.getTask().getId() : null;
							Long slideId = testEvent.getSlide() != null ? testEvent.getSlide().getId() : null;
							
							sheet.addCell(new jxl.write.DateTime(0, rowNr, eventDate));
							// write test properties
							sheet.addCell(new jxl.write.Number(1, rowNr, testId));
							if (userId != null) {
								sheet.addCell(new jxl.write.Number(2, rowNr, userId));
							}
							
							// write event properties
							sheet.addCell(new jxl.write.Number(3, rowNr, testEvent.getId()));
							sheet.addCell(new jxl.write.Number(4, rowNr, eventDate.getTime()));
							sheet.addCell(new jxl.write.Number(5, rowNr, testEvent.getType()));
							sheet.addCell(new jxl.write.Label(6, rowNr, eventName));
							sheet.addCell(new jxl.write.Label(7, rowNr, testEvent.getXmlData()));
							if (branchId != null) {
								sheet.addCell(new jxl.write.Number(8, rowNr, branchId));
							}
							if (taskId != null) {
								sheet.addCell(new jxl.write.Number(9, rowNr, taskId));
							}
							if (slideId != null) {
								sheet.addCell(new jxl.write.Number(10, rowNr, slideId));
							}
							
							if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
								SlideOutput slideOutput = testManager.findSlideOutput(test, slide);
								// write slide output properties
								if (slideOutput != null) {
									String outputString = SlideOutputParser.parseOutput(slideOutput.getOutput());
									if (outputString != null) {
										sheet.addCell(new jxl.write.Label(11, rowNr, outputString));
									}
									String xmlData = slideOutput.getXmlData();
									Map<String, String> fieldMap = SlideOutputParser.parseData(xmlData);
									
									for (String fieldName : fieldMap.keySet()) {
										int colNr; 
										if (fieldColumnMap.containsKey(fieldName)) {
											colNr = fieldColumnMap.get(fieldName);
										} else {
											colNr = fieldCol++;
											fieldColumnMap.put(fieldName, colNr);
											sheet.addCell(new jxl.write.Label(colNr, 0, fieldName));
										}
										sheet.addCell(new jxl.write.Label(colNr, rowNr, fieldMap.get(fieldName)));
									}
								}
							}
							++rowNr;
						}
					}
					++rowNr;
				}
			}

			workbook.write();
			workbook.close();

			final InputStream input = new ByteArrayInputStream(
					((ByteArrayOutputStream) output).toByteArray());
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
			*/
		/*} catch (IOException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_CANNOT_CREATE_FILE));
		} catch (RowsExceededException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_ROWS_LIMIT_EXCEEDED));
		} catch (WriteException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_CANNOT_WRITE_TO_FILE));
		*/
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
