package org.hypothesis.utility;

import org.hypothesis.interfaces.Command;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

public class PushUtility {

	public static void pushCommand(final UI ui, final Command command) {
		if (ui != null && command != null) {
			ui.access(() -> {
				command.execute();
				forceManualPush(ui);
			});
		}
	}

	public static void pushCommand(final Command command) {
		pushCommand(UI.getCurrent(), command);
	}

	private static void forceManualPush(final UI ui) {
		if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
			try {
				ui.push();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
