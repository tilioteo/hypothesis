/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.builder.SlideDataParser;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;
import org.hypothesis.utility.GenderUtility;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ExportScoreRunnableImpl implements ExportRunnable {

	private static final Logger log = Logger.getLogger(ExportRunnableImpl.class);

	private volatile int progress;

	private final AtomicBoolean cancelPending = new AtomicBoolean(false);
	private final Collection<Long> testIds;

	private final MainEventBus bus;

	private Command finishCommand = null;

	public ExportScoreRunnableImpl(MainEventBus bus, final Collection<Long> testIds) {
		this.bus = bus;
		this.testIds = testIds;
	}

	@Override
	public synchronized void setFinishCommand(Command command) {
		this.finishCommand = command;
	}

	@Override
	public boolean isCancelPending() {
		return cancelPending.get();
	}

	@Override
	public synchronized void setCancelPending(boolean value) {
		cancelPending.set(value);
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

		Command.Executor.execute(finishCommand);
	}

	@SuppressWarnings("serial")
	private StreamResource getExportResource() {

		final InputStream inputStream = getExportFile();

		if (inputStream != null) {
			StreamResource.StreamSource source = new StreamResource.StreamSource() {
				@Override
				public InputStream getStream() {
					return inputStream;
				}
			};

			String filename = Messages.getString("Caption.Export.ScoreFileName");
			StreamResource resource = new StreamResource(source, filename);
			resource.setMIMEType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			return resource;
		}

		return null;
	}

	private InputStream getExportFile() {
		ExportService exportService = ExportService.newInstance();

		try {
			List<ExportScore> scores = exportService.findExportScoresByTestId(testIds);

			if (scores != null) {
				try {
					File tempFile = File.createTempFile("htsm", null);

					SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
					Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.ScoreSheetName"));

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
					header.createCell(3).setCellValue("score_id");
					header.createCell(4).setCellValue("pack_id");
					header.createCell(5).setCellValue("pack_name");
					header.createCell(6).setCellValue("branch_id");
					header.createCell(7).setCellValue("branch_name");
					header.createCell(8).setCellValue("task_id");
					header.createCell(9).setCellValue("task_name");
					header.createCell(10).setCellValue("slide_id");
					header.createCell(11).setCellValue("slide_name");
					header.createCell(12).setCellValue("score_timestamp");
					header.createCell(13).setCellValue("action_name");
					header.createCell(14).setCellValue("score_data");

					header.createCell(15).setCellValue("Jméno");
					header.createCell(16).setCellValue("Příjmení");
					header.createCell(17).setCellValue("Rodné číslo");
					header.createCell(18).setCellValue("Pohlaví");
					header.createCell(19).setCellValue("Vzdělání");
					header.createCell(20).setCellValue("Poznámka");
					header.createCell(21).setCellValue("Poznámka2");

					header.createCell(22).setCellValue("value1");
					header.createCell(23).setCellValue("value2");
					header.createCell(24).setCellValue("value3");
					header.createCell(25).setCellValue("value4");
					header.createCell(26).setCellValue("value5");
					header.createCell(27).setCellValue("value6");
					header.createCell(28).setCellValue("value7");
					header.createCell(29).setCellValue("value8");
					header.createCell(30).setCellValue("value9");
					header.createCell(31).setCellValue("value10");

					int size = scores.size();
					float counter = 0f;
					int lastProgress = 0;

					int outputValueCol = 22;

					int rowNr = 1;
					for (ExportScore score : scores) {
						if (cancelPending.get()) {
							workbook.close();
							tempFile.delete();
							return null;
						}

						Long testId = score.getTestId();
						Long userId = score.getUserId();
						Date scoreDate = score.getDatetime();
						long scoreTime = scoreDate.getTime();
						String scoreName = score.getName();
						Long branchId = score.getBranchId();
						String branchName = score.getBranchName();
						Long taskId = score.getTaskId();
						String taskName = score.getTaskName();
						Long slideId = score.getSlideId();
						String slideName = score.getSlideName();

						String firstName = score.getFirstName();
						String username = score.getUsername();
						String pasword = score.getPassword();
						String gender = score.getGender();
						String education = score.getEducation();
						String note = score.getNote();

						Row row = sheet.createRow(rowNr++);

						Cell cell = row.createCell(0);
						cell.setCellValue(testId);
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						cell = row.createCell(1);
						cell.setCellValue(scoreDate);
						cell.setCellStyle(dateCellStyle);

						if (userId != null) {
							cell = row.createCell(2);
							cell.setCellValue(userId);
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						}

						cell = row.createCell(3);
						cell.setCellValue(score.getId());
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						cell = row.createCell(4);
						cell.setCellValue(score.getPackId());
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						cell = row.createCell(5);
						cell.setCellValue(score.getPackName());
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

						cell = row.createCell(12);
						cell.setCellValue(scoreTime);
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						cell = row.createCell(13);
						cell.setCellValue(scoreName);
						cell.setCellType(Cell.CELL_TYPE_STRING);

						String xmlData = score.getData();
						cell = row.createCell(14);
						cell.setCellValue(xmlData);
						cell.setCellType(Cell.CELL_TYPE_STRING);

						if (firstName != null) {
							cell = row.createCell(15);
							cell.setCellValue(firstName);
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (username != null) {
							cell = row.createCell(16);
							cell.setCellValue(username);
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (pasword != null) {
							cell = row.createCell(17);
							cell.setCellValue(pasword);
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (gender != null) {
							cell = row.createCell(18);
							cell.setCellValue(GenderUtility.getLocalizedName(gender));
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (education != null) {
							cell = row.createCell(19);
							cell.setCellValue(education);
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (note != null) {
							cell = row.createCell(20);
							cell.setCellValue(note);
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}

						if (xmlData != null) {
							int colNr = outputValueCol;

							// write output properties
							List<String> scoreValues = SlideDataParser.parseScores(xmlData);
							for (String scoreValue : scoreValues) {
								if (scoreValue != null) {
									row.createCell(colNr).setCellValue(scoreValue);
								}
								++colNr;
							}
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
