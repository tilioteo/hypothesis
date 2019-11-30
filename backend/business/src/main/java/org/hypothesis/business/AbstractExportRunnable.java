package org.hypothesis.business;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.push.Pushable;
import org.hypothesis.ui.ControlledUI;

public abstract class AbstractExportRunnable implements Runnable, Pushable {

    private final MainEventBus bus;
    private final Command finishCommand;

    protected AbstractExportRunnable(MainEventBus bus, Command finishCommand) {
        this.bus = bus;
        this.finishCommand = finishCommand;
    }

    @Override
    public void run() {
        Resource resource = getExportResource();
        ControlledUI ui = ControlledUI.getCurrent();
        if (resource != null && ui != null) {
            ui.setResource("export", resource);
            ResourceReference reference = ResourceReference.create(resource, ui, "export");

            pushCommand(() -> bus.post(new MainUIEvent.ExportFinishedEvent(false)));
            Page.getCurrent().open(reference.getURL(), null);
        } else {
            pushCommand(() -> bus.post(new MainUIEvent.ExportErrorEvent()));
        }

        pushCommand(finishCommand);
    }

    protected void populateProgress(float progress) {
        pushCommand(() -> bus.post(new MainUIEvent.ExportProgressEvent(progress)));
    }

    protected abstract StreamResource getExportResource();

}
