/**
 * 
 */
package com.tilioteo.hypothesis.event.interfaces;

import com.tilioteo.hypothesis.interfaces.ProcessView;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public interface ProcessViewEvent extends ProcessEvent {

	public static final class ProcessViewEndEvent implements ProcessViewEvent {
		private final ProcessView view;

		public ProcessViewEndEvent(final ProcessView view) {
			this.view = view;
		}

		public ProcessView getView() {
			return view;
		}
	}
}