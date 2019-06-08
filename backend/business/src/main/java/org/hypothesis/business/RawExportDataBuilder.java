package org.hypothesis.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.builder.SlideDataParser;
import org.hypothesis.data.dto.ExportEventDto;
import org.hypothesis.server.Messages;

public class RawExportDataBuilder {

	public static void exportEventsToExcelFile(final List<ExportEventDto> events, final File file,
			final AtomicBoolean cancelPending, final Consumer<Float> progressConsumer) throws IOException {
		if (events != null && file != null) {
			int progress = 0;

			// maps hold informations for legend creation
			HashMap<String, String> fieldCaptionMap = new HashMap<>();
			HashMap<String, HashMap<String, String>> fieldValueCaptionMap = new HashMap<>();

			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
			Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.TestSheetName"));

			// create cell style for date cell
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(
					createHelper.createDataFormat().getFormat(Messages.getString("Format.Export.DateTime")));

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

			HashMap<String, Integer> fieldColumnMap = new HashMap<>();
			HashMap<Long, Integer> branchCountMap = new HashMap<>();
			HashMap<Long, Integer> slideCountMap = new HashMap<>();

			int outputValueCol = 23;
			int fieldCol = outputValueCol + 10;

			int rowNr = 1;
			int branchOrder = 0;
			int taskOrder = 0;
			int slideOrder = 0;
			int branchCount = 0;
			int slideCount = 0;

			for (ExportEventDto event : events) {
				if (cancelPending != null && cancelPending.get()) {
					workbook.close();
					file.delete();
					return;
				}

				Long testId = event.getTestId();
				Long userId = event.getUserId();
				Date eventDate = event.getTimeStamp();
				Date clientDate = event.getClientTimeStamp();
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

					if ("FINISH_SLIDE".equalsIgnoreCase(eventName) || "ACTION".equalsIgnoreCase(eventName)) {
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
						Map<String, Map<String, String>> fieldValueCaptions = wrapper.getFieldValueCaptionMap();

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
									HashMap<String, String> valueCaptionMap = fieldValueCaptionMap.get(fieldName);
									if (null == valueCaptionMap) {
										valueCaptionMap = new HashMap<>();
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
					if (progressConsumer != null) {
						progressConsumer.accept(progress / 100.0f);
					}
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
						row.createCell(1).setCellValue(Messages.getString("Caption.Export.UserValueDescription"));

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
			FileOutputStream output = new FileOutputStream(file);
			workbook.write(output);
			workbook.close();
			output.close();
		}

	}
}
