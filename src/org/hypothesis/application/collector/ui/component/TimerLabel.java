/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.common.StringMap;
import org.hypothesis.terminal.gwt.client.ClientTimer.Direction;
import org.hypothesis.terminal.gwt.client.ui.VTimerLabel;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.Label;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
@ClientWidget(VTimerLabel.class)
public class TimerLabel extends Label implements SlideComponent {

	public class FinishEvent extends SlideComponent.Event {

		private boolean stopped;

		/**
		 * New instance of finish event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public FinishEvent(SlideComponent source, boolean stopped) {
			super(source);
			this.stopped = stopped;
		}

		/**
		 * Gets the TimerLabel where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public TimerLabel getCounterLabel() {
			return (TimerLabel) getSource();
		}

		public boolean getStopped() {
			return stopped;
		}
	}

	/**
	 * TimerLabel finish listener
	 */
	public interface FinishListener extends Serializable {

		/**
		 * Counting has been finished.
		 * 
		 * @param event
		 *            TimerLabel finish event.
		 */
		public void finished(FinishEvent event);

	}

	private static String FINISH_EVENT = VTimerLabel.END_EVENT_IDENTIFIER;

	private static final Method FINISH_METHOD;
	private HashSet<FinishListener> listeners = new HashSet<FinishListener>();
	private long time = 0L;
	private Direction direction = Direction.Up;

	@SuppressWarnings("unused")
	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	private boolean running = false;
	private boolean visible = true;
	private boolean startPending = false;
	private boolean stopPending = false;

	private boolean pausePending = false;

	private boolean resumePending = false;

	static {
		try {
			FINISH_METHOD = FinishListener.class.getDeclaredMethod("finished",
					new Class[] { FinishEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error finding methods in TimerLabel");
		}
	}

	public TimerLabel() {
		/*
		 * setContentMode(Label.CONTENT_XHTML); setCaption("&nbsp;");
		 */
		super("");
		this.parentAlignment = new ParentAlignment();
	}

	public TimerLabel(long time) {
		this();
		setTime(time);
	}

	public TimerLabel(long time, Direction direction) {
		this(time);
		setDirection(direction);
	}

	/**
	 * Adds the counter label finished listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(FinishListener listener) {
		listeners.add(listener);
		addListener(FinishEvent.class, listener, FINISH_METHOD);
		if (listeners.size() == 1) {
			requestRepaint();
		}
	}

	/**
	 * Emits the finish event.
	 */
	private void fireEnd(boolean stopped) {
		fireEvent(new FinishEvent(this, stopped));
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public Direction getDirection() {
		return direction;
	}

	public long getTime() {
		return time;
	}

	public boolean getVisible() {
		return visible;
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		if (variables.containsKey(FINISH_EVENT)) {
			Boolean stopped = (Boolean) variables.get(FINISH_EVENT);
			running = false;
			fireEnd(stopped);
		}
	}

	public void loadFromXml(Element element) {

		setProperties(element);

	}

	/**
	 * Invoked when the component state should be painted.
	 */
	@Override
	public void paintContent(PaintTarget target) throws PaintException {

		if (getTime() > 0) {
			target.addAttribute("time", getTime());
		}

		if (getDirection() != null) {
			target.addAttribute("direction", getDirection().toString());
		}

		target.addAttribute("visible", getVisible());

		if (startPending) {
			target.addAttribute("start", true);
			startPending = false;
		}

		if (stopPending) {
			target.addAttribute("stop", true);
			stopPending = false;
		}

		if (pausePending) {
			target.addAttribute("pause", true);
			pausePending = false;
		}

		if (resumePending) {
			target.addAttribute("resume", true);
			resumePending = false;
		}
	}

	public void pause() {
		if (running) {
			pausePending = true;
			running = false;

			requestRepaint();
		}
	}

	/**
	 * Removes the finish listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(FinishListener listener) {
		listeners.remove(listener);
		removeListener(FinishEvent.class, listener, FINISH_METHOD);
		if (listeners.size() == 0) {
			requestRepaint();
		}
	}

	public void resume() {
		if (!running) {
			resumePending = true;
			running = true;

			requestRepaint();
		}
	}

	public void setDirection(Direction direction) {
		if (direction != null && !direction.equals(this.direction)) {
			this.direction = direction;

			requestRepaint();
		}
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	public void setTime(long time) {
		if (time != this.time) {
			this.time = time;

			requestRepaint();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;

			requestRepaint();
		}
	}

	public void start() {
		if (!running) {
			startPending = true;
			running = true;

			requestRepaint();
		}
	}

	public void stop() {
		if (running) {
			stopPending = true;
			running = false;

			requestRepaint();
		}
	}

}
