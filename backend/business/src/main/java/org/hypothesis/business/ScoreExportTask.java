package org.hypothesis.business;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ScoreExportTask extends RawExportTask {
    private static final Logger log = Logger.getLogger(ScoreExportTask.class);

    public ScoreExportTask(ControlledUI ui, MainEventBus bus, Collection<Long> testIds) {
        super(ui, bus, testIds);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected String getExportFilename() {
        return Messages.getString("Caption.Export.ScoreFileName");
    }

    @Override
    protected void processFile(File file) throws IOException {
        ExportService exportService = ExportService.newInstance();
        List<ExportScore> scores = exportService.findExportScoresByTestId(testIds);

        if (scores != null) {
            workbook = ScoreExportDataBuilder.exportScoresToExcelFile(scores, file, this::populateProgress);
        }
    }

}
