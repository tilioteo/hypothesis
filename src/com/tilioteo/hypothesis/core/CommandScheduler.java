/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.processing.Command;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public interface CommandScheduler {
	
	public final class Scheduler {
		public static final void scheduleCommand(Command command) {
			if (command != null) {
				UI ui = UI.getCurrent();
				if (ui instanceof CommandScheduler) {
					((CommandScheduler)ui).scheduleCommand(command);
				}
			}
		}
	}
	
	public void scheduleCommand(Command command);

}
