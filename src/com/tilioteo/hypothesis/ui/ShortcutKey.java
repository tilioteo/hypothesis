/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ShortcutKey extends AbstractComponent implements Action.ShortcutNotifier {

	public ShortcutKey(int keyCode, int... modifiers) {
		super();
		setShortcut(keyCode, modifiers);
	}

	public ShortcutKey(int keyCode) {
		this(keyCode, null);
	}

	/**
     * Press event. This event is thrown, when the key is pressed.
     * 
     */
    public static class KeyPressEvent extends Component.Event {

        private int keyCode;
        private int[] modifierKeys;

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public KeyPressEvent(Component source) {
            super(source);
        }

        /**
         * Constructor with key code
         * 
         * @param source
         *            The source where the shortcut key took place
         * @param details
         *            Details about the key
         */
        public KeyPressEvent(Component source, int keyCode) {
            super(source);
            this.keyCode = keyCode;
        }

        /**
         * Constructor with key code and modifier keys
         * 
         * @param source
         *            The source where the shortcut key took place
         * @param details
         *            Details about the key
         * @param modifiers
         *            Details about modifier keys
         */
        public KeyPressEvent(Component source, int keyCode, int... modifiers) {
            this(source, keyCode);
            this.modifierKeys = modifiers;
        }

        /**
         * Gets the Button where the event occurred.
         * 
         * @return the Source of the event.
         */
        public ShortcutKey getShortcutKey() {
            return (ShortcutKey) getSource();
        }
        
        private boolean modifiersContain(int modifierKey) {
        	for (int modifier : modifierKeys) {
        		if (modifier == modifierKey) {
        			return true;
        		}
        	}
        	return false;
        }
        
        public int getKeyCode() {
        	return keyCode;
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         * 
         * @return true if Alt was down when the event occured, false otherwise
         *         or if unknown
         */
        public boolean isAltKey() {
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.ALT);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.CTRL);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.META);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.SHIFT);
            } else {
                return false;
            }
        }
    }

    /**
     * Interface for listening for a {@link KeyPressEvent} fired by a
     * {@link Component}.
     * 
     */
    public interface KeyPressListener extends Serializable {

        public static final Method KEY_PRESS_METHOD = ReflectTools.findMethod(
        		KeyPressListener.class, "keyPress", KeyPressEvent.class);

        /**
         * Called when a {@link ShortcutKey} has been clicked. A reference to the
         * shortcut key is given by {@link KeyPressEvent#getShortcutKey()}.
         * 
         * @param event
         *            An event containing information about the key.
         */
        public void keyPress(KeyPressEvent event);

    }

    /**
     * Adds the key press listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addKeyPressListener(KeyPressListener listener) {
        addListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
    }

    /**
     * Removes the key press listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeKeyPressListener(KeyPressListener listener) {
        removeListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
    }

    /**
     * Simulates a key press, notifying all server-side listeners.
     * 
     * No action is taken if the shortcut key is disabled.
     */
    /*public void keyPress() {
        if (isEnabled()) {
            fireKeyPress();
        }
    }*/

    /**
     * Fires a key press event to all listeners without any event details.
     * 
     * In subclasses, override {@link #fireClick(int, int[])} instead of
     * this method.
     */
    /*protected void fireKeyPress() {
        fireEvent(new ShortcutKey.KeyPressEvent(this));
    }*/

    /**
     * Fires a key press event to all listeners.
     * 
     */
    protected void fireKeyPress(int keyCode, int[] modifierKeys) {
        fireEvent(new ShortcutKey.KeyPressEvent(this, keyCode, modifierKeys));
    }
    
    /*
     * Actions
     */

    protected Shortcut shortcut;

    /**
     * Makes it possible to invoke an action by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * The shortcut is global (bound to the containing Window).
     * 
     * @param keyCode
     *            the keycode for invoking the shortcut
     * @param modifiers
     *            the (optional) modifiers for invoking the shortcut, null for
     *            none
     */
    protected void setShortcut(int keyCode, int... modifiers) {
        if (shortcut != null) {
            removeShortcutListener(shortcut);
        }
        shortcut = new Shortcut(this, keyCode, modifiers);
        addShortcutListener(shortcut);
    }

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setShortcut(int, int...)}.
     */
    protected void removeShortcut() {
        if (shortcut != null) {
            removeShortcutListener(shortcut);
            shortcut = null;
        }
    }

    
    
    
    
    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given button.
     * 
     */
    public static class Shortcut extends ShortcutListener {
        protected ShortcutKey shortcutKey;

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        /*public Shortcut(ShortcutKey shortcutKey, String shorthandCaption) {
            super(shorthandCaption);
            this.shortcutKey = shortcutKey;
        }*/

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         * 
         * @param shortcutKey
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         * @param modifiers
         *            optional modifiers for shortcut
         */
        public Shortcut(ShortcutKey shortcutKey, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.shortcutKey = shortcutKey;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode}.
         * 
         * @param shortcutKey
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         */
        public Shortcut(ShortcutKey shortcutKey, int keyCode) {
            this(shortcutKey, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            shortcutKey.fireKeyPress(getKeyCode(), getModifiers());
        }
    }

}
