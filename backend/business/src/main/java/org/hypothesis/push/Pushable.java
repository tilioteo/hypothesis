package org.hypothesis.push;

import com.vaadin.ui.UI;
import org.hypothesis.interfaces.Command;

public interface Pushable {

    default void pushCommand(final Command command) {
        pushCommand(UI.getCurrent(), command);
    }

    default void pushCommand(final UI ui, final Command command) {
        if (ui != null && command != null) {
            ui.access(() -> {
                command.execute();
                PushUtility.forceManualPush(ui);
            });
        }
    }

}
