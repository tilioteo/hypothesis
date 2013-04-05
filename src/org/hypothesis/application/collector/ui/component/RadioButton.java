/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import org.hypothesis.terminal.gwt.client.ui.VRadioButton;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(VRadioButton.class)
public class RadioButton extends AbstractField implements
		FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

	/**
	 * Click event. This event is thrown, when the radio button is clicked.
	 * 
	 */
	public class ClickEvent extends Component.Event {

		private final MouseEventDetails details;

		/**
		 * New instance of text change event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ClickEvent(Component source) {
			super(source);
			details = null;
		}

		/**
		 * Constructor with mouse details
		 * 
		 * @param source
		 *            The source where the click took place
		 * @param details
		 *            Details about the mouse click
		 */
		public ClickEvent(Component source, MouseEventDetails details) {
			super(source);
			this.details = details;
		}

		/**
		 * Returns the mouse position (x coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor x position or -1 if unknown
		 */
		public int getClientX() {
			if (null != details) {
				return details.getClientX();
			} else {
				return -1;
			}
		}

		/**
		 * Returns the mouse position (y coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor y position or -1 if unknown
		 */
		public int getClientY() {
			if (null != details) {
				return details.getClientY();
			} else {
				return -1;
			}
		}

		/**
		 * Gets the Button where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public RadioButton getRadioButton() {
			return (RadioButton) getSource();
		}

		/**
		 * Returns the relative mouse position (x coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor x position relative to the clicked layout
		 *         component or -1 if no x coordinate available
		 */
		public int getRelativeX() {
			if (null != details) {
				return details.getRelativeX();
			} else {
				return -1;
			}
		}

		/**
		 * Returns the relative mouse position (y coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor y position relative to the clicked layout
		 *         component or -1 if no y coordinate available
		 */
		public int getRelativeY() {
			if (null != details) {
				return details.getRelativeY();
			} else {
				return -1;
			}
		}

		/**
		 * Checks if the Alt key was down when the mouse event took place.
		 * 
		 * @return true if Alt was down when the event occured, false otherwise
		 *         or if unknown
		 */
		public boolean isAltKey() {
			if (null != details) {
				return details.isAltKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Ctrl key was down when the mouse event took place.
		 * 
		 * @return true if Ctrl was pressed when the event occured, false
		 *         otherwise or if unknown
		 */
		public boolean isCtrlKey() {
			if (null != details) {
				return details.isCtrlKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Meta key was down when the mouse event took place.
		 * 
		 * @return true if Meta was pressed when the event occured, false
		 *         otherwise or if unknown
		 */
		public boolean isMetaKey() {
			if (null != details) {
				return details.isMetaKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Shift key was down when the mouse event took place.
		 * 
		 * @return true if Shift was pressed when the event occured, false
		 *         otherwise or if unknown
		 */
		public boolean isShiftKey() {
			if (null != details) {
				return details.isShiftKey();
			} else {
				return false;
			}
		}
	}

	/* Private members */

	// boolean disableOnClick = false;

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link Component}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		/**
		 * Called when a {@link Button} has been clicked. A reference to the
		 * button is given by {@link ClickEvent#getButton()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void radioButtonClick(ClickEvent event);

	}

	/**
	 * A {@link ShortcutListener} specifically made to define a keyboard
	 * shortcut that invokes a click on the given button.
	 * 
	 */
	public static class ClickShortcut extends ShortcutListener {
		protected RadioButton radioButton;

		/**
		 * Creates a keyboard shortcut for clicking the given radio button using
		 * the given {@link KeyCode}.
		 * 
		 * @param radioButton
		 *            to be clicked when the shortcut is invoked
		 * @param keyCode
		 *            KeyCode to react to
		 */
		public ClickShortcut(RadioButton radioButton, int keyCode) {
			this(radioButton, keyCode, null);
		}

		/**
		 * Creates a keyboard shortcut for clicking the given radio button using
		 * the given {@link KeyCode} and {@link ModifierKey}s.
		 * 
		 * @param radioButton
		 *            to be clicked when the shortcut is invoked
		 * @param keyCode
		 *            KeyCode to react to
		 * @param modifiers
		 *            optional modifiers for shortcut
		 */
		public ClickShortcut(RadioButton radioButton, int keyCode,
				int... modifiers) {
			super(null, keyCode, modifiers);
			this.radioButton = radioButton;
		}

		/**
		 * Creates a keyboard shortcut for clicking the given button using the
		 * shorthand notation defined in {@link ShortcutAction}.
		 * 
		 * @param radioButton
		 *            to be clicked when the shortcut is invoked
		 * @param shorthandCaption
		 *            the caption with shortcut keycode and modifiers indicated
		 */
		public ClickShortcut(RadioButton radioButton, String shorthandCaption) {
			super(shorthandCaption);
			this.radioButton = radioButton;
		}

		@Override
		public void handleAction(Object sender, Object target) {
			if (radioButton.isEnabled() && !radioButton.isReadOnly()) {
				radioButton.fireClick();
			}
		}
	}

	public enum LabelPosition {
		Left, Right, Top, Bottom
	}

	private boolean labelVisible = true;

	private LabelPosition labelPosition = LabelPosition.Right;

	private static final Method RADIO_BUTTON_CLICK_METHOD;

	static {
		try {
			RADIO_BUTTON_CLICK_METHOD = ClickListener.class.getDeclaredMethod(
					"radioButtonClick", new Class[] { ClickEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error finding methods in RadioButton");
		}
	}

	protected ClickShortcut clickShortcut;

	/**
	 * Creates a new radio button. The value of the radio button is false and it
	 * is immediate by default.
	 * 
	 */
	public RadioButton() {
		setValue(Boolean.FALSE);
	}

	/**
	 * Creates a new radio button.
	 * 
	 * The value of the radio button is false and it is immediate by default.
	 * 
	 * @param caption
	 *            the RadioButton caption.
	 */
	public RadioButton(String caption) {
		this();
		setCaption(caption);
	}

	/**
	 * Creates a new radio button with click listener.
	 * 
	 * @param caption
	 *            the RadioButton caption.
	 * @param listener
	 *            the RadioButton click listener.
	 */
	public RadioButton(String caption, ClickListener listener) {
		this(caption);
		addListener(listener);
	}

	/**
	 * Creates a new radio button with a method listening button clicks. Using
	 * this method is discouraged because it cannot be checked during
	 * compilation. Use
	 * {@link #RadioButton(String, org.hypothesis.application.collector.ui.component.RadioButton.ClickListener)}
	 * instead. The method must have either no parameters, or only one parameter
	 * of RadioButton.ClickEvent type.
	 * 
	 * @param caption
	 *            the RadioButton caption.
	 * @param target
	 *            the Object having the method for listening button clicks.
	 * @param methodName
	 *            the name of the method in target object, that receives button
	 *            click events.
	 */
	public RadioButton(String caption, Object target, String methodName) {
		this(caption);
		addListener(ClickEvent.class, target, methodName);
	}

	public void addListener(BlurListener listener) {
		addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
				BlurListener.blurMethod);
	}

	/**
	 * Adds the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(ClickListener listener) {
		addListener(ClickEvent.class, listener, RADIO_BUTTON_CLICK_METHOD);
	}

	public void addListener(FocusListener listener) {
		addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
				FocusListener.focusMethod);
	}

	/* Click event */

	/**
	 * Get the boolean value of the button state.
	 * 
	 * @return True iff the radio button is checked.
	 */
	public boolean booleanValue() {
		Boolean value = (Boolean) getValue();
		return (null == value) ? false : value.booleanValue();
	}

	/**
	 * Fires a click event to all listeners without any event details.
	 * 
	 * In subclasses, override {@link #fireClick(MouseEventDetails)} instead of
	 * this method.
	 */
	protected void fireClick() {
		fireEvent(new RadioButton.ClickEvent(this));
	}

	/**
	 * Fires a click event to all listeners.
	 * 
	 * @param details
	 *            MouseEventDetails from which keyboard modifiers and other
	 *            information about the mouse click can be obtained. If the
	 *            button was clicked by a keyboard event, some of the fields may
	 *            be empty/undefined.
	 */
	protected void fireClick(MouseEventDetails details) {
		fireEvent(new RadioButton.ClickEvent(this, details));
	}

	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	public boolean getLabelVisible() {
		return labelVisible;
	}

	/**
	 * The type of the button as a property.
	 * 
	 * @see com.vaadin.data.Property#getType()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getType() {
		return Boolean.class;
	}

	/**
	 * Invoked when the value of a variable has changed. RadioButton listeners
	 * are notified if the radio button is clicked.
	 * 
	 * @param source
	 * @param variables
	 */
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		if (variables.containsKey("disabledOnClick")) {
			// Could be optimized so the button is not repainted because of this
			// (client side has already disabled the button)
			setEnabled(false);
		}

		if (!isReadOnly() && variables.containsKey("state")) {
			// Gets the new and old button states
			final Boolean newValue = (Boolean) variables.get("state");
			final Boolean oldValue = (Boolean) getValue();

			// Only send click event if the button is pushed
			if (newValue.booleanValue()) {
				if (variables.containsKey("mousedetails")) {
					fireClick(MouseEventDetails.deSerialize((String) variables
							.get("mousedetails")));
				} else {
					// for compatibility with custom implementations which
					// don't send mouse details
					fireClick();
				}
			}

			// If the button is true for some reason, release it
			if (null == oldValue || oldValue.booleanValue()) {
				setValue(Boolean.FALSE);
			}
		}

		if (variables.containsKey(FocusEvent.EVENT_ID)) {
			fireEvent(new FocusEvent(this));
		}
		if (variables.containsKey(BlurEvent.EVENT_ID)) {
			fireEvent(new BlurEvent(this));
		}
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param event
	 *            the PaintEvent.
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		target.addVariable(this, "state", booleanValue());

		target.addAttribute("labelVisible", labelVisible);
		target.addAttribute("labelPosition", labelPosition.ordinal());

		// if (isDisableOnClick()) {
		// target.addAttribute(VRadioButton.ATTR_DISABLE_ON_CLICK, true);
		// }
		if (clickShortcut != null) {
			target.addAttribute("keycode", clickShortcut.getKeyCode());
		}
	}

	/**
	 * Removes the keyboard shortcut previously set with
	 * {@link #setClickShortcut(int, int...)}.
	 */
	public void removeClickShortcut() {
		if (clickShortcut != null) {
			removeShortcutListener(clickShortcut);
			clickShortcut = null;
		}
	}

	public void removeListener(BlurListener listener) {
		removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
	}

	/**
	 * Removes the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener, RADIO_BUTTON_CLICK_METHOD);
	}

	public void removeListener(FocusListener listener) {
		removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);

	}

	/**
	 * Makes it possible to invoke a click on this radio button by pressing the
	 * given {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
	 * The shortcut is global (bound to the containing Window).
	 * 
	 * @param keyCode
	 *            the keycode for invoking the shortcut
	 * @param modifiers
	 *            the (optional) modifiers for invoking the shortcut, null for
	 *            none
	 */
	public void setClickShortcut(int keyCode, int... modifiers) {
		if (clickShortcut != null) {
			removeShortcutListener(clickShortcut);
		}
		clickShortcut = new ClickShortcut(this, keyCode, modifiers);
		addShortcutListener(clickShortcut);
	}

	/*
	 * Actions
	 */

	/**
	 * Sets immediate mode. Push buttons can not be set in non-immediate mode.
	 * 
	 * @see com.vaadin.ui.AbstractComponent#setImmediate(boolean)
	 */
	@Override
	public void setImmediate(boolean immediate) {
		// Push buttons are always immediate
		super.setImmediate(immediate);
	}

	@Override
	protected void setInternalValue(Object newValue) {
		// Make sure only booleans get through
		if (null != newValue && !(newValue instanceof Boolean)) {
			throw new IllegalArgumentException(getClass().getSimpleName()
					+ " only accepts Boolean values");
		}
		super.setInternalValue(newValue);
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		if (this.labelPosition != labelPosition) {
			this.labelPosition = labelPosition;
			requestRepaint();
		}
	}

	public void setLabelVisible(boolean visible) {
		if (this.labelVisible != visible) {
			this.labelVisible = visible;
			requestRepaint();
		}
	}

	/**
	 * Determines if a radio button is automatically disabled when clicked. See
	 * {@link #setDisableOnClick(boolean)} for details.
	 * 
	 * @return true if the button is disabled when clicked, false otherwise
	 */
	// public boolean isDisableOnClick() {
	// return disableOnClick;
	// }

	/**
	 * Determines if a button is automatically disabled when clicked. If this is
	 * set to true the button will be automatically disabled when clicked,
	 * typically to prevent (accidental) extra clicks on a button.
	 * 
	 * @param disableOnClick
	 *            true to disable button when it is clicked, false otherwise
	 */
	// public void setDisableOnClick(boolean disableOnClick) {
	// this.disableOnClick = disableOnClick;
	// requestRepaint();
	// }
}
