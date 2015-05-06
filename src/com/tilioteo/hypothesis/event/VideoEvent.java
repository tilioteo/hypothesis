/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Video;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class VideoEvent extends AbstractComponentEvent<Video> {

	public static class Click extends VideoEvent {

		public Click(VideoData data) {
			this(data, null);
		}

		public Click(VideoData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoClick;
		}

	}

	public static class Load extends VideoEvent {

		public Load(VideoData data) {
			this(data, null);
		}

		public Load(VideoData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoLoad;
		}

	}

	public static class Start extends VideoEvent {

		public Start(VideoData data) {
			this(data, null);
		}

		public Start(VideoData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStart;
		}

	}

	public static class Stop extends VideoEvent {

		public Stop(VideoData data) {
			this(data, null);
		}

		public Stop(VideoData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStop;
		}

	}

	protected VideoEvent(VideoData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
}
