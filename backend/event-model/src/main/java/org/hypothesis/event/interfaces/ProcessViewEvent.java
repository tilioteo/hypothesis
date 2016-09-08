/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.interfaces;

import org.hypothesis.interfaces.ProcessView;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public interface ProcessViewEvent extends ProcessEvent {

	final class ProcessViewEndEvent implements ProcessViewEvent {
		private final ProcessView view;

		public ProcessViewEndEvent(final ProcessView view) {
			this.view = view;
		}

		public ProcessView getView() {
			return view;
		}
	}
}