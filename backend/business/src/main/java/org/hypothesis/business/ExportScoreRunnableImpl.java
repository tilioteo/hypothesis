/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import static org.hypothesis.utility.PushUtility.pushCommand;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ExportScoreRunnableImpl implements CancelableExportRunnable {

	private static final Logger log = Logger.getLogger(ExportRunnableImpl.class);

	private final AtomicBoolean cancelPending = new AtomicBoolean(false);
	private final Collection<Long> testIds;

	private final MainEventBus bus;

	private final Command finishCommand;

	public ExportScoreRunnableImpl(MainEventBus bus, final Collection<Long> testIds, Command finishCommand) {
		this.bus = bus;
		this.testIds = testIds;
		this.finishCommand = finishCommand;
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

			pushCommand(ui, () -> bus.post(new MainUIEvent.ExportFinishedEvent(false)));
			Page.getCurrent().open(reference.getURL(), null);
		} else {
			pushCommand(() -> bus.post(new MainUIEvent.ExportErrorEvent()));
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

	private void populateProgress(float progress) {
		pushCommand(() -> bus.post(new MainUIEvent.ExportProgressEvent(progress)));
	}

	private InputStream getExportFile() {
		ExportService exportService = ExportService.newInstance();

		try {
			List<ExportScore> scores = exportService.findExportScoresByTestId(testIds);

			if (scores != null) {
				try {
					File tempFile = File.createTempFile("htsm", null);

					ScoreExportDataBuilder.exportScoresToExcelFile(scores, tempFile, cancelPending,
							this::populateProgress);

					if (!cancelPending.get()) {
						return new FileInputStream(tempFile);
					}

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
