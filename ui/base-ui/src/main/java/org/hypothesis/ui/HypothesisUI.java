/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.hypothesis.interfaces.TimerHandler;
import org.vaadin.special.ui.NonVisualComponent;
import org.vaadin.special.ui.ShortcutKey;
import org.vaadin.special.ui.Timer;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class HypothesisUI extends ControlledUI implements TimerHandler {

	/**
	 * List of timers in this UI.
	 */
	private final LinkedHashSet<Timer> timers = new LinkedHashSet<>();

	/**
	 * List of timers in this UI.
	 */
	private final LinkedHashSet<ShortcutKey> shortcuts = new LinkedHashSet<>();

	@Override
	protected void init(VaadinRequest request) {
		super.init(request);

		JavaScript.getCurrent().addFunction("aboutToClose", new JavaScriptFunction() {

			@Override
			public void call(JsonArray arguments) {
				System.out.println("Window/Tab is Closed.");
				// TODO Call Method to Clean the Resource before window/Tab
				// Close.
				getPresenter().cleanup();
			}
		});
		Page.getCurrent().getJavaScript().execute(
				"window.onbeforeunload = function (e) { var e = e || window.event; aboutToClose(); return; };");
	}

	@Override
	public void setContent(Component content) {
		if (content instanceof NonVisualComponent) {
			throw new IllegalArgumentException(
					"A non visual component cannot be added using setContent. Use attachNonVisualComponent(NonVisualComponent) instead.");
		}

		super.setContent(content);
	}

	protected void attachNonVisualComponent(NonVisualComponent component, boolean markAsDirty) {
		component.setParent(this);
		if (markAsDirty) {
			markAsDirty();
		}
	}

	protected void detachNonVisualComponent(NonVisualComponent component, boolean markAsDirty) {
		component.setParent(null);
		if (markAsDirty) {
			markAsDirty();
		}
	}

	/**
	 * Adds a timer as inside this UI.
	 * 
	 * @param timer
	 * @throws IllegalArgumentException
	 *             if the timer is already added to an application
	 * @throws NullPointerException
	 *             if the given <code>Timer</code> is <code>null</code>.
	 */
	@Override
	public void addTimer(AbstractComponent timer) throws IllegalArgumentException, NullPointerException {
		if (timer == null) {
			throw new NullPointerException("Argument cannot be null.");
		}

		if (timer instanceof Timer) {

			if (timer.isAttached()) {
				throw new IllegalArgumentException("Timer is already attached to an application.");
			}

			attachTimer((Timer) timer);

		} else {
			throw new IllegalArgumentException("Timer must be of class " + Timer.class.getCanonicalName());
		}
	}

	/**
	 * Helper method to attach a timer.
	 * 
	 * @param timer
	 *            the timer to add
	 */
	private void attachTimer(Timer timer) {
		timers.add(timer);
		attachNonVisualComponent(timer, true);
	}

	/**
	 * Remove the given timer from this UI.
	 * 
	 * @param timer
	 *            Timer to be removed.
	 * @return true if the timer was removed, false otherwise
	 */
	@Override
	public boolean removeTimer(AbstractComponent timer) throws IllegalArgumentException {
		if (timer instanceof Timer) {
			Timer t = (Timer) timer;
			if (!timers.remove(t)) {
				// Timer timer is not in this UI.
				return false;
			}
			t.stop(true);
			detachNonVisualComponent(t, true);

			return true;
		} else {
			throw new IllegalArgumentException("Timer must be of class " + Timer.class.getCanonicalName());
		}
	}

	@Override
	public void removeAllTimers() {
		Iterator<Timer> iterator;
		while ((iterator = timers.iterator()).hasNext()) {
			Timer timer = iterator.next();
			timer.stop(true);
			detachNonVisualComponent(timer, false);
			timers.remove(timer);
		}
		markAsDirty();
	}

	/**
	 * Adds a shortcut key as inside this UI.
	 * 
	 * @param shortcutKey
	 * @throws IllegalArgumentException
	 *             if the shortcut key is already added to an application
	 * @throws NullPointerException
	 *             if the given <code>ShortcutKey</code> is <code>null</code>.
	 */
	public void addShortcutKey(ShortcutKey shortcutKey) throws IllegalArgumentException, NullPointerException {

		if (shortcutKey == null) {
			throw new NullPointerException("Argument cannot be null.");
		}

		if (shortcutKey.isAttached()) {
			throw new IllegalArgumentException("ShortcutKey is already attached to an application.");
		}

		attachShortcutKey(shortcutKey);
	}

	/**
	 * Helper method to attach a shortcut key.
	 * 
	 * @param shortcutKey
	 *            the shortcut key to add
	 */
	private void attachShortcutKey(ShortcutKey shortcutKey) {
		shortcuts.add(shortcutKey);
		attachNonVisualComponent(shortcutKey, true);
	}

	/**
	 * Remove the given timer from this UI.
	 * 
	 * @param shortcutKey
	 *            ShortcutKey to be removed.
	 * @return true if the shortcut kez was removed, false otherwise
	 */
	public boolean removeShortcutKey(ShortcutKey shortcutKey) {
		if (!shortcuts.remove(shortcutKey)) {
			// ShortcutKey shortcutKey is not in this UI.
			return false;
		}
		detachNonVisualComponent(shortcutKey, true);

		return true;
	}

	public void removeAllShortcutKeys() {
		Iterator<ShortcutKey> iterator;
		while ((iterator = shortcuts.iterator()).hasNext()) {
			ShortcutKey shortcutKey = iterator.next();
			detachNonVisualComponent(shortcutKey, false);
			shortcuts.remove(shortcutKey);
		}
		markAsDirty();
	}
}
