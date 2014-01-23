/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.json.JSONException;

import com.tilioteo.hypothesis.shared.ui.radiobutton.RadioButtonServerRpc;
import com.tilioteo.hypothesis.shared.ui.radiobutton.RadioButtonState;
import com.tilioteo.hypothesis.shared.ui.radiobutton.RadioButtonState.LabelPosition;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RadioButton extends AbstractField<Boolean> {

	private RadioButtonServerRpc rpc = new RadioButtonServerRpc() {

		@Override
		public void setChecked(boolean checked,
				MouseEventDetails mouseEventDetails) {
			if (isReadOnly()) {
				return;
			}

			/*
			 * Client side updates the state before sending the event so we need
			 * to make sure the cached state is updated to match the client. If
			 * we do not do this, a reverting setValue() call in a listener will
			 * not cause the new state to be sent to the client.
			 * 
			 * See #11028, #10030.
			 */
			try {
				getUI().getConnectorTracker().getDiffState(RadioButton.this)
						.put("checked", checked);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			final Boolean oldValue = getValue();
			final Boolean newValue = checked;

			if (!newValue.equals(oldValue)) {
				// The event is only sent if the switch state is changed
				setValue(newValue);
			}

		}
	};

	FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {
		@Override
		protected void fireEvent(Event event) {
			RadioButton.this.fireEvent(event);
		}
	};

	/**
	 * Creates a new radio button. The value of the radio button is false and it
	 * is immediate by default.
	 * 
	 */
	public RadioButton() {
		registerRpc(rpc);
		registerRpc(focusBlurRpc);
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
	 * Creates a new radio button with a caption and a set initial state.
	 * 
	 * @param caption
	 *            the caption of the radio button
	 * @param initialState
	 *            the initial state of the radio button
	 */
	public RadioButton(String caption, boolean initialState) {
		this(caption);
		setValue(initialState);
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
		addClickListener(listener);
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	@Override
	protected RadioButtonState getState() {
		return (RadioButtonState) super.getState();
	}

	/*
	 * Overridden to keep the shared state in sync with the AbstractField
	 * internal value. Should be removed once AbstractField is refactored to use
	 * shared state.
	 * 
	 * See tickets #10921 and #11064.
	 */
	@Override
	protected void setInternalValue(Boolean newValue) {
		super.setInternalValue(newValue);
		if (newValue == null) {
			newValue = false;
		}
		getState().checked = newValue;
	}

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

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link Component}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		public static final Method BUTTON_CLICK_METHOD = ReflectTools
				.findMethod(ClickListener.class, "buttonClick",
						ClickEvent.class);

		/**
		 * Called when a {@link Button} has been clicked. A reference to the
		 * button is given by {@link ClickEvent#getButton()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void buttonClick(ClickEvent event);

	}

	/**
	 * Adds the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		addListener(ClickEvent.class, listener,
				ClickListener.BUTTON_CLICK_METHOD);
	}

	/**
	 * Removes the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener,
				ClickListener.BUTTON_CLICK_METHOD);
	}

	/**
	 * Simulates a button click, notifying all server-side listeners.
	 * 
	 * No action is taken is the button is disabled.
	 */
	public void click() {
		if (isEnabled() && !isReadOnly()) {
			fireClick();
		}
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

	public void addBlurListener(BlurListener listener) {
		addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
				BlurListener.blurMethod);
	}

	public void removeBlurListener(BlurListener listener) {
		removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
	}

	public void addFocusListener(FocusListener listener) {
		addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
				FocusListener.focusMethod);
	}

	public void removeFocusListener(FocusListener listener) {
		removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
	}

	/*
	 * Actions
	 */

	protected ClickShortcut clickShortcut;

	/**
	 * Makes it possible to invoke a click on this button by pressing the given
	 * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
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
		getState().clickShortcutKeyCode = clickShortcut.getKeyCode();
	}

	/**
	 * Removes the keyboard shortcut previously set with
	 * {@link #setClickShortcut(int, int...)}.
	 */
	public void removeClickShortcut() {
		if (clickShortcut != null) {
			removeShortcutListener(clickShortcut);
			clickShortcut = null;
			getState().clickShortcutKeyCode = 0;
		}
	}

	/**
	 * A {@link ShortcutListener} specifically made to define a keyboard
	 * shortcut that invokes a click on the given button.
	 * 
	 */
	public static class ClickShortcut extends ShortcutListener {
		protected RadioButton button;

		/**
		 * Creates a keyboard shortcut for clicking the given button using the
		 * shorthand notation defined in {@link ShortcutAction}.
		 * 
		 * @param button
		 *            to be clicked when the shortcut is invoked
		 * @param shorthandCaption
		 *            the caption with shortcut keycode and modifiers indicated
		 */
		public ClickShortcut(RadioButton button, String shorthandCaption) {
			super(shorthandCaption);
			this.button = button;
		}

		/**
		 * Creates a keyboard shortcut for clicking the given button using the
		 * given {@link KeyCode} and {@link ModifierKey}s.
		 * 
		 * @param button
		 *            to be clicked when the shortcut is invoked
		 * @param keyCode
		 *            KeyCode to react to
		 * @param modifiers
		 *            optional modifiers for shortcut
		 */
		public ClickShortcut(RadioButton button, int keyCode, int... modifiers) {
			super(null, keyCode, modifiers);
			this.button = button;
		}

		/**
		 * Creates a keyboard shortcut for clicking the given button using the
		 * given {@link KeyCode}.
		 * 
		 * @param button
		 *            to be clicked when the shortcut is invoked
		 * @param keyCode
		 *            KeyCode to react to
		 */
		public ClickShortcut(RadioButton button, int keyCode) {
			this(button, keyCode, null);
		}

		@Override
		public void handleAction(Object sender, Object target) {
			button.click();
		}
	}

	public LabelPosition getLabelPosition() {
		return getState().labelPosition;
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		getState().labelPosition = labelPosition;
	}

	public boolean getLabelVisible() {
		return getState().labelVisible;
	}

	public void setLabelVisible(boolean visible) {
		getState().labelVisible = visible;
	}

}
