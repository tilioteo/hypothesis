/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import com.vaadin.server.StreamResource;
import org.apache.log4j.Logger;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.server.Messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@Deprecated
public class ExportScoreRunnableImpl extends AbstractExportRunnable implements CancelableExportRunnable {

    private static final Logger log = Logger.getLogger(ExportScoreRunnableImpl.class);

    private final AtomicBoolean cancelPending = new AtomicBoolean(false);
    private final Collection<Long> testIds;

    public ExportScoreRunnableImpl(MainEventBus bus, final Collection<Long> testIds, Command finishCommand) {
        super(bus, finishCommand);
        this.testIds = testIds;
    }

    @Override
    public boolean isCancelPending() {
        return cancelPending.get();
    }

    @Override
    public synchronized void setCancelPending(boolean value) {
        cancelPending.set(value);
    }

    protected StreamResource getExportResource() {

        final InputStream inputStream = getExportFile();

        if (inputStream != null) {
            String filename = Messages.getString("Caption.Export.ScoreFileName");
            StreamResource resource = new StreamResource(() -> inputStream, filename);
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

                    /*ScoreExportDataBuilder.exportScoresToExcelFile(scores, tempFile, cancelPending,
                            this::populateProgress);*/

                    if (!cancelPending.get()) {
                        return new FileInputStream(tempFile);
                    }

                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
