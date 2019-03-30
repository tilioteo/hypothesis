package org.hypothesis.business;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;

public class SimpleExportScoreRunnableImpl implements SimpleExportRunnable {

	private static final Logger log = Logger.getLogger(SimpleExportRunnable.class);

	private final long testId;
	private final String path;

	private final Consumer<String> finishConsumer;

	public SimpleExportScoreRunnableImpl(final long testId, final String path, final Consumer<String> finishConsumer) {
		this.testId = testId;
		this.path = path;
		this.finishConsumer = finishConsumer;
	}

	@Override
	public void run() {
		String fileName = createExportFile();

		if (fileName != null && finishConsumer != null) {
			finishConsumer.accept(fileName);
		}
	}

	private String createExportFile() {
		ExportService exportService = ExportService.newInstance();
		List<Long> testIds = Stream.of(testId).collect(Collectors.toList());

		try {
			List<ExportScore> scores = exportService.findExportScoresByTestId(testIds);

			if (scores != null && StringUtils.isNotBlank(path)) {
				try {
					File file = new File(FilenameUtils.concat(path, String.format("sc%010d.xlsx", testId)));

					ScoreExportDataBuilder.exportScoresToExcelFile(scores, file, new AtomicBoolean(false), null);

					return file.getName();

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
