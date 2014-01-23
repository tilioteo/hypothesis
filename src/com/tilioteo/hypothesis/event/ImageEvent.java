/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Image;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ImageEvent extends AbstractComponentEvent<Image> {

	public static class Click extends ImageEvent {

		public Click(ImageData data) {
			super(data);
		}

		public String getName() {
			return ProcessEventTypes.ImageClick;
		}

	}

	public static class Load extends ImageEvent {

		public Load(ImageData data) {
			super(data);
		}

		public String getName() {
			return ProcessEventTypes.ImageLoad;
		}

	}

	protected ImageEvent(/* ProcessEvent event, */ImageData data) {
		super(/* event, */data);
	}

}
