/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Image;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ImageEvent extends AbstractComponentEvent<Image> {

	public static class Click extends ImageEvent {

		public Click(ImageData data) {
			this(data, null);
		}

		public Click(ImageData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ImageClick;
		}

	}

	public static class Load extends ImageEvent {

		public Load(ImageData data) {
			this(data, null);
		}

		public Load(ImageData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ImageLoad;
		}

	}

	public static class Error extends ImageEvent {

		public Error(ImageData data) {
			this(data, null);
		}

		public Error(ImageData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ImageError;
		}

	}

	protected ImageEvent(ImageData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

}
