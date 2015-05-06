/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Audio;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class AudioEvent extends AbstractComponentEvent<Audio> {

	public static class Load extends AudioEvent {

		public Load(AudioData data) {
			this(data, null);
		}

		public Load(AudioData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoLoad;
		}

	}

	public static class Start extends AudioEvent {

		public Start(AudioData data) {
			this(data, null);
		}

		public Start(AudioData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStart;
		}

	}

	public static class Stop extends AudioEvent {

		public Stop(AudioData data) {
			this(data, null);
		}

		public Stop(AudioData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStop;
		}

	}

	protected AudioEvent(AudioData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
}
