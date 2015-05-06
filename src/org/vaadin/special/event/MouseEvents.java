/**
 * 
 */
package org.vaadin.special.event;

import java.lang.reflect.Method;

import org.vaadin.special.shared.EventId;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
public interface MouseEvents {

	/**
	 * Class for holding information about a mouse click event. A
	 * {@link ClickEvent} is fired when the user clicks on a
	 * <code>Component</code>.
	 * 
	 * The information available for click events are terminal dependent.
	 * Correct values for all event details cannot be guaranteed.
	 */
	@SuppressWarnings("serial")
	public static class ClickEvent extends ComponentEvent {

		private MouseEventDetails details;

		public ClickEvent(long timestamp, Component source, MouseEventDetails mouseEventDetails) {
			super(timestamp, source);
			details = mouseEventDetails;
		}

		/**
		 * Returns an identifier describing which mouse button the user pushed.
		 * Compare with {@link MouseButton#LEFT},{@link MouseButton#MIDDLE},
		 * {@link MouseButton#RIGHT} to find out which button it is.
		 * 
		 * @return one of {@link MouseButton#LEFT}, {@link MouseButton#MIDDLE},
		 *         {@link MouseButton#RIGHT}.
		 */
		public MouseButton getButton() {
			return details.getButton();
		}

		/**
		 * Returns the mouse position (x coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor x position
		 */
		public int getClientX() {
			return details.getClientX();
		}

		/**
		 * Returns the mouse position (y coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor y position
		 */
		public int getClientY() {
			return details.getClientY();
		}

		/**
		 * Returns the relative mouse position (x coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor x position relative to the clicked layout
		 *         component or -1 if no x coordinate available
		 */
		public int getRelativeX() {
			return details.getRelativeX();
		}

		/**
		 * Returns the relative mouse position (y coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor y position relative to the clicked layout
		 *         component or -1 if no y coordinate available
		 */
		public int getRelativeY() {
			return details.getRelativeY();
		}

		/**
		 * Checks if the event is a double click event.
		 * 
		 * @return true if the event is a double click event, false otherwise
		 */
		public boolean isDoubleClick() {
			return details.isDoubleClick();
		}

		/**
		 * Checks if the Alt key was down when the mouse event took place.
		 * 
		 * @return true if Alt was down when the event occured, false otherwise
		 */
		public boolean isAltKey() {
			return details.isAltKey();
		}

		/**
		 * Checks if the Ctrl key was down when the mouse event took place.
		 * 
		 * @return true if Ctrl was pressed when the event occured, false
		 *         otherwise
		 */
		public boolean isCtrlKey() {
			return details.isCtrlKey();
		}

		/**
		 * Checks if the Meta key was down when the mouse event took place.
		 * 
		 * @return true if Meta was pressed when the event occured, false
		 *         otherwise
		 */
		public boolean isMetaKey() {
			return details.isMetaKey();
		}

		/**
		 * Checks if the Shift key was down when the mouse event took place.
		 * 
		 * @return true if Shift was pressed when the event occured, false
		 *         otherwise
		 */
		public boolean isShiftKey() {
			return details.isShiftKey();
		}

		/**
		 * Returns a human readable string representing which button has been
		 * pushed. This is meant for debug purposes only and the string returned
		 * could change. Use {@link #getButton()} to check which button was
		 * pressed.
		 * 
		 * @since 6.3
		 * @return A string representation of which button was pushed.
		 */
		public String getButtonName() {
			return details.getButtonName();
		}
	}

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link Component}.
	 * 
	 */
	public interface ClickListener extends ConnectorEventListener {

		public static final Method clickMethod = ReflectTools.findMethod(
				ClickListener.class, EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class);

		/**
		 * Called when a {@link Component} has been clicked. A reference to the
		 * component is given by {@link ClickEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);
	}

	/**
	 * Class for holding additional event information for DoubleClick events.
	 * Fired when the user double-clicks on a <code>Component</code>.
	 */
	@SuppressWarnings("serial")
	public static class DoubleClickEvent extends ComponentEvent {

		public DoubleClickEvent(long timestamp, Component source) {
			super(timestamp, source);
		}
	}

	/**
	 * Interface for listening for a {@link DoubleClickEvent} fired by a
	 * {@link Component}.
	 */
	public interface DoubleClickListener extends ConnectorEventListener {

		public static final Method doubleClickMethod = ReflectTools.findMethod(
				DoubleClickListener.class, EventId.DOUBLECLICK_EVENT_IDENTIFIER, DoubleClickEvent.class);

		/**
		 * Called when a {@link Component} has been double clicked. A reference
		 * to the component is given by {@link DoubleClickEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the double click.
		 */
		public void doubleClick(DoubleClickEvent event);
	}

}
