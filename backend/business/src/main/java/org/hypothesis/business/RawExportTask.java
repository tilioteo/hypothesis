package org.hypothesis.business;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.data.service.ExportService;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ControlledUI;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class RawExportTask extends AbstractExportTask {

    private static final Logger log = Logger.getLogger(RawExportTask.class);

    protected final Collection<Long> testIds;
    protected Workbook workbook;

    public RawExportTask(ControlledUI ui, MainEventBus bus, Collection<Long> testIds) {
        super(ui, bus);
        this.testIds = testIds;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected String getExportFilename() {
        return Messages.getString("Caption.Export.TestFileName");
    }

    @Override
    protected String getExportMimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    protected void afterDone() {
        HibernateUtil.closeCurrent();
    }

    @Override
    protected void afterCancel() {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void processFile(File file) throws IOException {
        ExportService exportService = ExportService.newInstance();
        List<ExportEvent> events = exportService.findExportEventsByTestId(testIds);

        if (events != null) {
            workbook = RawExportDataBuilder.exportEventsToExcelFile(events, file, this::populateProgress);
        }
    }

}
