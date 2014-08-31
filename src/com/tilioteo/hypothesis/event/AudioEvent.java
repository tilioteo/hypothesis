/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Audio;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AudioEvent extends AbstractComponentEvent<Audio> {

	public static class Load extends AudioEvent {

		public Load(AudioData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoLoad;
		}

	}

	public static class Start extends AudioEvent {

		public Start(AudioData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStart;
		}

	}

	public static class Stop extends AudioEvent {

		public Stop(AudioData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.VideoStop;
		}

	}

	protected AudioEvent(AudioData data) {
		super(data);
	}
}
