/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.ui.component.Image;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ImageEvent extends AbstractComponentEvent<Image> {

	public static class Click extends ImageEvent {

		public Click(/* ProcessEvent event, */ImageData data) {
			super(/* event, */data);
		}

		public String getName() {
			return ProcessEvents.ImageClick;
		}

	}

	public static class Load extends ImageEvent {

		public Load(/* ProcessEvent event, */ImageData data) {
			super(/* event, */data);
		}

		public String getName() {
			return ProcessEvents.ImageLoad;
		}

	}

	protected ImageEvent(/* ProcessEvent event, */ImageData data) {
		super(/* event, */data);
	}

}
