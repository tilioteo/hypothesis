package org.hypothesis.presenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.builder.SlideDataParser;
import org.hypothesis.common.IntSequence;
import org.hypothesis.data.interfaces.ExportService;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ExportThreadedServiceImpl implements ExportThreadedService {

	static final Logger log = Logger.getLogger(ExportThreadedServiceImpl.class);

	@Inject
	private ExportService exportService;

	@Inject
	private Event<MainUIEvent> mainEvent;

	@SuppressWarnings("serial")
	private static class CancelledException extends Exception {
	}

	private Collection<Long> testIds;

	private transient Thread thread;

	private volatile int progress;

	private final AtomicBoolean cancelPending = new AtomicBoolean(false);

	private final ThreadGroup threadGroup = new ThreadGroup("export-service");

	private final Runnable exportRunnable = () -> {
		Resource resource = getExportResource();
		ControlledUI ui = ControlledUI.getCurrent();
		if (resource != null && ui != null) {
			ui.setResource("export", resource);
			ResourceReference reference = ResourceReference.create(resource, ui, "export");

			UI.getCurrent().access(() -> mainEvent.fire(new MainUIEvent.ExportFinishedEvent(false)));

			Page.getCurrent().open(reference.getURL(), null);
		} else {
			UI.getCurrent().access(() -> mainEvent.fire(new MainUIEvent.ExportErrorEvent()));
		}
		
		exportService.releaseConnection();
	};

	@Override
	public void exportTests(Collection<Long> testIds) {
		this.testIds = testIds;

		thread = new Thread(threadGroup, exportRunnable);
		thread.start();
	}

	/**
	 * Request requestCancel of export
	 */
	@Override
	public synchronized void requestCancel() {
		cancelPending.set(true);
	}

	private StreamResource getExportResource() {

		final InputStream inputStream = getExportFile();

		if (inputStream != null) {
			StreamResource.StreamSource source = () -> inputStream;

			String filename = Messages.getString("Caption.Export.TestFileName");
			StreamResource resource = new StreamResource(source, filename);
			resource.setMIMEType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			return resource;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private InputStream getExportFile() {
		try {
			List<ExportEvent> events = exportService.findExportEventsByTestId(testIds);

			if (events != null) {
				SXSSFWorkbook workbook = null;

				File tempFile;
				try {
					tempFile = File.createTempFile("htsm", null);

					workbook = new SXSSFWorkbook(-1);
					Object[] maps = createDataSheet(workbook, events);
					// maps hold informations for legend creation
					Map<String, String> fieldCaptionMap = (Map<String, String>) maps[0];
					Map<String, Map<String, String>> fieldValueCaptionMap = (Map<String, Map<String, String>>) maps[1];

					// create legend sheet only if there are some
					// informations gathered
					if (!fieldCaptionMap.isEmpty() || !fieldValueCaptionMap.isEmpty()) {
						createLegendSheet(workbook, fieldCaptionMap, fieldValueCaptionMap);
					}

					// finalize file creation
					FileOutputStream output = new FileOutputStream(tempFile);
					workbook.write(output);
					output.close();

					return new FileInputStream(tempFile);

				} catch (IOException | ExportThreadedServiceImpl.CancelledException e) {
					log.error(e.getMessage());
				} finally {
					if (workbook != null) {
						workbook.close();
					}
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}

	private Object[] createDataSheet(SXSSFWorkbook workbook, final List<ExportEvent> events)
			throws ExportThreadedServiceImpl.CancelledException {
		// maps hold informations for legend creation
		Map<String, String> fieldCaptionMap = new HashMap<>();
		Map<String, Map<String, String>> fieldValueCaptionMap = new HashMap<>();

		Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.TestSheetName"));

		// create cell style for date cell
		CellStyle dateCellStyle = createDateCellStyle(workbook);

		Row header = createHeader(sheet);

		int size = events.size();
		float counter = 0f;
		int lastProgress = 0;

		Long lastTestId = null;
		Long lastBranchId = null;
		Long lastTaskId = null;
		Long lastSlideId = null;

		long startTestTime = 0;
		long relativeTime;
		long lastEventTime = 0;
		long diffTime;

		Map<String, Integer> fieldColumnMap = new HashMap<>();
		Map<Long, Integer> branchCountMap = new HashMap<>();
		Map<Long, Integer> slideCountMap = new HashMap<>();

		int outputValueCol = 23;
		final IntSequence colSeq = new IntSequence(outputValueCol + 10 - 1);

		int rowNr = 1;
		int branchOrder = 0;
		int taskOrder = 0;
		int slideOrder = 0;
		int branchCount = 0;
		int slideCount = 0;

		for (ExportEvent event : events) {
			if (cancelPending.get()) {
				throw new CancelledException();
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

			createNumericCell(row, 0, testId);
			createDateCell(row, 1, eventDate, dateCellStyle);

			if (userId != null) {
				createNumericCell(row, 2, userId);
			}

			createNumericCell(row, 3, event.getId());
			createNumericCell(row, 4, event.getPackId());
			createStringCell(row, 5, event.getPackName());

			if (branchId != null) {
				createNumericCell(row, 6, branchId);
			}
			createStringCell(row, 7, branchName);
			if (taskId != null) {
				createNumericCell(row, 8, taskId);
			}
			createStringCell(row, 9, taskName);
			if (slideId != null) {
				createNumericCell(row, 10, slideId);
			}
			createStringCell(row, 11, slideName);

			if (branchId != null) {
				createNumericCell(row, 12, branchOrder);
			}
			if (branchId != null) {
				createNumericCell(row, 13, branchCount);
			}
			if (taskId != null) {
				createNumericCell(row, 14, taskOrder);
			}
			if (slideId != null) {
				createNumericCell(row, 15, slideOrder);
			}

			if (slideId != null) {
				createNumericCell(row, 16, slideCount);
			}

			relativeTime = eventTime - startTestTime;
			createNumericCell(row, 17, relativeTime);

			if (lastEventTime > 0) {
				diffTime = eventTime - lastEventTime;
				createNumericCell(row, 18, diffTime);
			}
			lastEventTime = eventTime;

			if (clientDate != null) {
				createNumericCell(row, 19, clientDate.getTime());
			}

			createNumericCell(row, 20, event.getType());
			createStringCell(row, 21, eventName);

			String xmlData = event.getData();
			createStringCell(row, 22, xmlData);

			if (slideId != null && xmlData != null) {
				final IntSequence seq = new IntSequence(outputValueCol);

				if ("FINISH_SLIDE".equalsIgnoreCase(eventName) || "ACTION".equalsIgnoreCase(eventName)) {
					// write output properties
					SlideDataParser.parseOutputValues(xmlData).forEach(e -> {
						if (e != null) {
							createStringCell(row, seq.current(), e);
						}
						seq.next();
					});
				}

				if ("FINISH_SLIDE".equalsIgnoreCase(eventName)) {
					SlideDataParser.FieldWrapper wrapper = SlideDataParser.parseFields(xmlData);
					Map<String, String> fieldCaptions = wrapper.getFieldCaptionMap();
					Map<String, String> fieldValues = wrapper.getFieldValueMap();
					Map<String, Map<String, String>> fieldValueCaptions = wrapper.getFieldValueCaptionMap();

					fieldCaptions.entrySet().forEach(e -> {
						String fieldName = e.getKey();
						int colNr;
						if (fieldColumnMap.containsKey(fieldName)) {
							colNr = fieldColumnMap.get(fieldName);
						} else {
							colNr = colSeq.next();
							fieldColumnMap.put(fieldName, colNr);
							String fieldCaption = fieldCaptions.get(fieldName);
							if (fieldCaption != null) {
								fieldCaptionMap.put(fieldName, fieldCaption);
							}
							createStringCell(header, colNr, fieldName);
						}
						String fieldValue = fieldValues.get(fieldName);

						if (fieldValueCaptions.get(fieldName) != null) {
							String valueCaption = fieldValueCaptions.get(fieldName).get(fieldValue);
							if (valueCaption != null) {
								Map<String, String> valueCaptionMap = fieldValueCaptionMap.get(fieldName);
								if (null == valueCaptionMap) {
									valueCaptionMap = new HashMap<>();
									fieldValueCaptionMap.put(fieldName, valueCaptionMap);
								}
								if (!valueCaptionMap.containsKey(fieldValue)) {
									valueCaptionMap.put(fieldValue, valueCaption);
								}
							}
						}
						createStringCell(row, colNr, fieldValue);
					});
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

				UI.getCurrent().access(() -> mainEvent.fire(new MainUIEvent.ExportProgressEvent(progress / 100.0f)));
			}
		}
		return new Object[] { fieldCaptionMap, fieldValueCaptionMap };
	}

	private void createStringCell(Row row, int column, String value) {
		if (StringUtils.isNotBlank(value)) {
			Cell cell = row.createCell(column);
			cell.setCellValue(value);
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}
	}

	private void createDateCell(Row row, int column, Date date, CellStyle cellStyle) {
		Cell cell = row.createCell(column);
		cell.setCellValue(date);
		cell.setCellStyle(cellStyle);
	}

	private void createNumericCell(Row row, int column, double value) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	}

	private CellStyle createDateCellStyle(SXSSFWorkbook workbook) {
		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle
				.setDataFormat(createHelper.createDataFormat().getFormat(Messages.getString("Format.Export.DateTime")));
		return cellStyle;
	}

	private Row createHeader(final Sheet sheet) {
		// create header row and freeze it
		Row row = sheet.createRow(0);
		sheet.createFreezePane(0, 1);

		row.createCell(0).setCellValue("test_id");
		row.createCell(1).setCellValue("date");
		row.createCell(2).setCellValue("user_id");
		row.createCell(3).setCellValue("event_id");
		row.createCell(4).setCellValue("pack_id");
		row.createCell(5).setCellValue("pack_name");
		row.createCell(6).setCellValue("branch_id");
		row.createCell(7).setCellValue("branch_name");
		row.createCell(8).setCellValue("task_id");
		row.createCell(9).setCellValue("task_name");
		row.createCell(10).setCellValue("slide_id");
		row.createCell(11).setCellValue("slide_name");
		row.createCell(12).setCellValue("branch_order_pack");
		row.createCell(13).setCellValue("branch_order");
		row.createCell(14).setCellValue("task_order_pack");
		row.createCell(15).setCellValue("slide_order_task");
		row.createCell(16).setCellValue("slide_order");
		row.createCell(17).setCellValue("event_timestamp");
		row.createCell(18).setCellValue("event_time_diff");
		row.createCell(19).setCellValue("client_timestamp");
		row.createCell(20).setCellValue("event_type");
		row.createCell(21).setCellValue("event_name");
		row.createCell(22).setCellValue("event_data");

		row.createCell(23).setCellValue("output_value1");
		row.createCell(24).setCellValue("output_value2");
		row.createCell(25).setCellValue("output_value3");
		row.createCell(26).setCellValue("output_value4");
		row.createCell(27).setCellValue("output_value5");
		row.createCell(28).setCellValue("output_value6");
		row.createCell(29).setCellValue("output_value7");
		row.createCell(30).setCellValue("output_value8");
		row.createCell(31).setCellValue("output_value9");
		row.createCell(32).setCellValue("output_value10");

		return row;
	}

	private void createLegendSheet(SXSSFWorkbook workbook, Map<String, String> fieldCaptionMap,
			Map<String, Map<String, String>> fieldValueCaptionMap) {
		final Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.LegendSheetName"));
		final IntSequence seq = new IntSequence();
		if (!fieldCaptionMap.isEmpty()) {
			Row row = sheet.createRow(seq.next());
			createStringCell(row, 0, Messages.getString("Caption.Export.UserColumns"));

			row = sheet.createRow(seq.next());
			createStringCell(row, 0, Messages.getString("Caption.Export.ColumnName"));
			createStringCell(row, 1, Messages.getString("Caption.Export.ColumnDescription"));

			fieldCaptionMap.entrySet().forEach(e -> {
				Row r = sheet.createRow(seq.next());
				createStringCell(r, 0, e.getKey());
				createStringCell(r, 1, fieldCaptionMap.get(e.getKey()));
			});

			seq.next();
		}

		if (!fieldValueCaptionMap.isEmpty()) {
			Row row = sheet.createRow(seq.next());
			createStringCell(row, 0, Messages.getString("Caption.Export.UserColumnValues"));

			fieldValueCaptionMap.entrySet().forEach(e -> {
				Row r = sheet.createRow(seq.next());
				createStringCell(r, 0, Messages.getString("Caption.Export.ColumnName"));
				createStringCell(r, 1, e.getKey());

				r = sheet.createRow(seq.next());
				createStringCell(r, 0, Messages.getString("Caption.Export.UserValue"));
				createStringCell(r, 1, Messages.getString("Caption.Export.UserValueDescription"));

				final Map<String, String> valueCaptions = e.getValue();
				valueCaptions.entrySet().forEach(i -> {
					Row rr = sheet.createRow(seq.next());
					createStringCell(rr, 0, i.getValue());
					createStringCell(rr, 1, valueCaptions.get(i.getValue()));
				});
				seq.next();
			});
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
	}
}