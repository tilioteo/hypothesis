/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Video;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class VideoEvent extends AbstractComponentEvent<Video> {

	public static class Click extends VideoEvent {

		public Click(VideoData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoClick;
		}

	}

	public static class Load extends VideoEvent {

		public Load(VideoData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoLoad;
		}

	}

	public static class Start extends VideoEvent {

		public Start(VideoData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStart;
		}

	}

	public static class Stop extends VideoEvent {

		public Stop(VideoData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStop;
		}

	}

	protected VideoEvent(VideoData data) {
		super(data);
	}
}
