/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.LinkedHashSet;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class HUI extends UI {

    /**
     * List of windows in this UI.
     */
    private final LinkedHashSet<Timer> timers = new LinkedHashSet<Timer>();

    @Override
	public void setContent(Component content) {
        if (content instanceof Timer) {
            throw new IllegalArgumentException(
                    "A Timer cannot be added using setContent. Use addTimer(Timer timer) instead");
        }
        super.setContent(content);
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
    public void addTimer(Timer timer) throws IllegalArgumentException,
            NullPointerException {

        if (timer == null) {
            throw new NullPointerException("Argument must not be null");
        }

        if (timer.isAttached()) {
            throw new IllegalArgumentException(
                    "Timer is already attached to an application.");
        }

        attachTimer(timer);
    }

    /**
     * Helper method to attach a timer.
     * 
     * @param t
     *            the timer to add
     */
    private void attachTimer(Timer t) {
        timers.add(t);
        t.setParent(this);
        markAsDirty();
    }

    /**
     * Remove the given timer from this UI.
     * 
     * @param timer
     *            Timer to be removed.
     * @return true if the timer was removed, false otherwise
     */
    public boolean removeTimer(Timer timer) {
        if (!timers.remove(timer)) {
            // Timer timer is not in this UI.
            return false;
        }
        timer.stop();
        timer.setParent(null);
        markAsDirty();

        return true;
    }


}
