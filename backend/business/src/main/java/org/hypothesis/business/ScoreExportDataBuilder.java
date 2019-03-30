package org.hypothesis.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.builder.SlideDataParser;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.server.Messages;
import org.hypothesis.utility.GenderUtility;

public class ScoreExportDataBuilder {

	public static void exportScoresToExcelFile(final List<ExportScore> scores, final File file,
			final AtomicBoolean cancelPending, final Consumer<Float> progressConsumer) throws IOException {
		if (scores != null && file != null) {
			int progress = 0;

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

			header.createCell(15).setCellValue(Messages.getString("Caption.Field.Name"));
			header.createCell(16).setCellValue(Messages.getString("Caption.Field.Surname"));
			header.createCell(17).setCellValue(Messages.getString("Caption.Field.BirthNumber"));
			header.createCell(18).setCellValue(Messages.getString("Caption.Field.DateOfBirth"));
			header.createCell(19).setCellValue(Messages.getString("Caption.Field.Gender"));
			header.createCell(20).setCellValue(Messages.getString("Caption.Field.Education"));
			header.createCell(21).setCellValue(Messages.getString("Caption.Field.Note"));
			header.createCell(22).setCellValue(Messages.getString("Caption.Field.Note") + "2");
			header.createCell(23).setCellValue(Messages.getString("Caption.Field.Note") + "3");

			header.createCell(24).setCellValue("value1");
			header.createCell(25).setCellValue("value2");
			header.createCell(26).setCellValue("value3");
			header.createCell(27).setCellValue("value4");
			header.createCell(28).setCellValue("value5");
			header.createCell(29).setCellValue("value6");
			header.createCell(30).setCellValue("value7");
			header.createCell(31).setCellValue("value8");
			header.createCell(32).setCellValue("value9");
			header.createCell(33).setCellValue("value10");

			int size = scores.size();
			float counter = 0f;
			int lastProgress = 0;

			int outputValueCol = 24;

			int rowNr = 1;
			for (ExportScore score : scores) {
				if (cancelPending != null && cancelPending.get()) {
					workbook.close();
					file.delete();
					return;
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
				String dateOfBirth = DateFormatUtils.format(score.getBirthDate(),
						Messages.getString("Format.Export.Date"));
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

				if (dateOfBirth != null) {
					cell = row.createCell(18);
					cell.setCellValue(dateOfBirth);
					cell.setCellType(Cell.CELL_TYPE_STRING);
				}

				if (gender != null) {
					cell = row.createCell(19);
					cell.setCellValue(GenderUtility.getLocalizedName(gender));
					cell.setCellType(Cell.CELL_TYPE_STRING);
				}

				if (education != null) {
					cell = row.createCell(20);
					cell.setCellValue(education);
					cell.setCellType(Cell.CELL_TYPE_STRING);
				}

				if (note != null) {
					cell = row.createCell(21);
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
					if (progressConsumer != null) {
						progressConsumer.accept(progress / 100.0f);
					}
				}
			}

			// finalize file creation
			FileOutputStream output = new FileOutputStream(file);
			workbook.write(output);
			workbook.close();
			output.close();
		}
	}
}
