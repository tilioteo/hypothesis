package org.hypothesis.terminal.gwt.client.ui;

import org.hypothesis.terminal.gwt.client.ClientTimer;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VLabel;

public class VTimerLabel extends VLabel {

	public static final String CLASSNAME = "v-counterlabel";
	public static final String END_EVENT_IDENTIFIER = "end";

	private ApplicationConnection client;
	private String id;

	private ClientTimer clientTimer;
	private Long time = 0L;
	private boolean visible;

	public VTimerLabel() {
		super();
		setStyleName(CLASSNAME);
		setText("");

		clientTimer = new ClientTimer() {
			@Override
			public void onEnd(boolean stopped) {
				fireEnd(stopped);
			}

			@Override
			public void onUpdate(long estimatedTime) {
				setTimeLabel((estimatedTime / 1000));
			}
		};
	}

	private void fireEnd(boolean stopped) {
		// notify server
		if (ClientTimer.Direction.Down.equals(clientTimer.getDirection()))
			setTimeLabel(-1);

		client.updateVariable(id, END_EVENT_IDENTIFIER, stopped, true);
	}

	public long getTime() {
		return time;
	}

	public void pauseCount() {
		clientTimer.pause();
	}

	public void resumeCount() {
		clientTimer.resume();
	}

	public void setTime(long time) {
		clientTimer.stop();
		this.time = time;
		setTimeLabel(time);
	}

	private void setTimeLabel(long time) {
		if (visible) {
			int hours = (int) (time / 3600);
			int mins = (int) ((time - 3600 * hours) / 60);
			int secs = (int) (time - 3600 * hours - 60 * mins);

			if (ClientTimer.Direction.Down.equals(clientTimer.getDirection()))
				++secs;

			char[] buffer = new String("00:00:00").toCharArray();
			String shour = Integer.toString(hours);
			String smin = Integer.toString(mins);
			String ssec = Integer.toString(secs);
			for (int i = 0; i < 2; ++i) {
				if (i <= shour.length() - 1)
					buffer[1 - i] = shour.charAt(shour.length() - i - 1);
				if (i <= smin.length() - 1)
					buffer[4 - i] = smin.charAt(smin.length() - i - 1);
				if (i <= ssec.length() - 1)
					buffer[7 - i] = ssec.charAt(ssec.length() - i - 1);
			}
			setText(new String(buffer));
		} else
			setText(null);
	}

	public void startCount() {
		if (time != null && time > 0)
			clientTimer.start(time);
	}

	public void stopCount() {
		clientTimer.stop();
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		super.updateFromUIDL(uidl, client);

		this.client = client;
		this.id = uidl.getId();

		Boolean vis = Boolean.valueOf(uidl.getBooleanAttribute("visible"));
		visible = (vis == null || vis);

		String direction = uidl.getStringAttribute("direction");
		try {
			if (direction == null || "up".equalsIgnoreCase(direction)) {
				clientTimer
						.setDirection(org.hypothesis.terminal.gwt.client.ClientTimer.Direction.Up);
			} else if ("down".equalsIgnoreCase(direction)) {
				clientTimer
						.setDirection(org.hypothesis.terminal.gwt.client.ClientTimer.Direction.Down);
			}
		} catch (Throwable t) {
		}

		time = Long.valueOf(uidl.getLongAttribute("time"));

		Boolean start = Boolean.valueOf(uidl.getBooleanAttribute("start"));
		if (time != null && start != null && !clientTimer.isRunning())
			startCount();

		Boolean stop = Boolean.valueOf(uidl.getBooleanAttribute("stop"));
		if (stop != null && stop && clientTimer.isRunning())
			stopCount();

		Boolean pause = Boolean.valueOf(uidl.getBooleanAttribute("pause"));
		if (pause != null && pause && clientTimer.isRunning())
			pauseCount();

		Boolean resume = Boolean.valueOf(uidl.getBooleanAttribute("resume"));
		if (time != null && resume != null && !clientTimer.isRunning())
			resumeCount();

		setTimeLabel((clientTimer.getCounter() / 1000));
	}

}
