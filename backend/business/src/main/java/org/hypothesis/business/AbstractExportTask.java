package org.hypothesis.business;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import org.apache.log4j.Logger;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.push.Pushable;
import org.hypothesis.ui.ControlledUI;
import org.mpilone.vaadin.uitask.UIAccessor;
import org.mpilone.vaadin.uitask.UITask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public abstract class AbstractExportTask extends UITask<Resource> implements Pushable {

    private final ControlledUI ui;
    private final MainEventBus bus;

    public AbstractExportTask(ControlledUI ui, MainEventBus bus) {
        super(new UIAccessor.Fixed(ui));
        this.ui = ui;
        this.bus = bus;
    }

    protected abstract Logger getLogger();

    protected abstract void processFile(File file) throws IOException;

    protected abstract String getExportFilename();

    protected abstract String getExportMimeType();

    protected abstract void afterDone();

    protected abstract void afterCancel();

    @Override
    protected Resource runInBackground() {
        return getExportResource();
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            Resource resource = null;
            try {
                resource = get();
                if (resource != null) {
                    ui.setResource("export", resource);
                    ResourceReference reference = ResourceReference.create(resource, ui, "export");

                    pushCommand(ui, () -> bus.post(new MainUIEvent.ExportFinishedEvent(false)));
                    Page.getCurrent().open(reference.getURL(), null);
                } else {
                    pushCommand(ui, () -> bus.post(new MainUIEvent.ExportErrorEvent()));
                }
                afterDone();
            } catch (InterruptedException e) {
                pushCommand(ui, () -> bus.post(new MainUIEvent.ExportErrorEvent()));
                getLogger().debug(e.getMessage(), e);
            } catch (ExecutionException e) {
                pushCommand(ui, () -> bus.post(new MainUIEvent.ExportErrorEvent()));
                getLogger().error(e.getMessage(), e);
            }
        } else {
            afterCancel();
        }
    }

    protected StreamResource getExportResource() {
        final InputStream inputStream = getExportFile();

        if (inputStream != null) {
            StreamResource resource = new StreamResource(() -> inputStream, getExportFilename());
            resource.setMIMEType(getExportMimeType());

            return resource;
        }

        return null;
    }

    protected InputStream getExportFile() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("htsm", null);
            processFile(tempFile);
            return new FileInputStream(tempFile);
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        } finally {
        }

        return null;
    }

    protected void populateProgress(float progress) {
        pushCommand(ui, () -> bus.post(new MainUIEvent.ExportProgressEvent(progress)));
    }

}
