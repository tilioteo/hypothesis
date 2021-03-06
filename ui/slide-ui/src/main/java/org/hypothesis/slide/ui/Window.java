/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Window extends com.vaadin.ui.Window {

	private boolean initialized = false;
	private boolean opened = false;

	private final List<CloseListener> closeListeners = new ArrayList<>();

	private UI futureUI = null;

	public Window() {
		super();
	}

	public void setFutureUI(UI ui) {
		this.futureUI = ui;
	}

	protected void fireOpen() {
		if (!initialized) {
			initialized = true;
			fireEvent(new InitEvent(this));
		}
		fireEvent(new OpenEvent(this));
	}

	@Override
	public void setParent(HasComponents parent) {
		super.setParent(parent);

		if (getParent() != null) {
			opened = true;
		}
	}

	public void open() {
		if (!opened && futureUI != null) {
			futureUI.addWindow(this);
			fireOpen();
		}
	}

	@Override
	public void close() {
		super.close();
		opened = false;
	}

	public boolean isOpened() {
		return opened;
	}

	/**
	 * Add an init listener to the component. The listener is called when the
	 * window is opened for the first time.
	 * 
	 * Use {@link #removeInitListener(InitListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addInitListener(InitListener listener) {
		addListener(InitEvent.class, listener, WINDOW_INIT_METHOD);
	}

	/**
	 * Remove an init listener from the component. The listener should earlier
	 * have been added using {@link #addInitListener(InitListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeInitListener(InitListener listener) {
		removeListener(InitEvent.class, listener, WINDOW_INIT_METHOD);
	}

	/**
	 * Add an open listener to the component. The listener is called whenever
	 * the window is opened.
	 * 
	 * Use {@link #removeOpenListener(OpenListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addOpenListener(OpenListener listener) {
		addListener(OpenEvent.class, listener, WINDOW_OPEN_METHOD);
	}

	/**
	 * Remove an open listener from the component. The listener should earlier
	 * have been added using {@link #addOpenListener(OpenListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeOpenListener(OpenListener listener) {
		removeListener(OpenEvent.class, listener, WINDOW_OPEN_METHOD);
	}

	@Override
	public void addCloseListener(CloseListener listener) {
		super.addCloseListener(listener);
		closeListeners.add(listener);
	}

	public void removeAllCloseListeners() {
		for (CloseListener listener : closeListeners) {
			removeCloseListener(listener);
		}
		closeListeners.clear();
	}

	private static final Method WINDOW_INIT_METHOD;

	static {
		try {
			WINDOW_INIT_METHOD = InitListener.class.getDeclaredMethod("initWindow", InitEvent.class);
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error, window init method not found");
		}
	}

	/**
	 * Class for holding information about a window init event. An
	 * {@link InitEvent} is fired when the <code>Window</code> is opened for the
	 * first time.
	 * 
	 * @author kamil.
	 * @see InitListener
	 */
	public class InitEvent extends Component.Event {

		public InitEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the Window.
		 * 
		 * @return the window.
		 */
		public Window getWindow() {
			return (Window) getSource();
		}
	}

	/**
	 * Interface for listening for a {@link InitEvent} fired by a {@link Window}
	 * when user opens the window for the first time.
	 * 
	 * @see InitEvent
	 * @author kamil
	 */
	public interface InitListener extends Serializable {

		/**
		 * Called when the user opens a window for first time. A reference to
		 * the window is given by {@link InitEvent#getWindow()}.
		 * 
		 * @param event
		 *            An event containing information about the window.
		 */
		void initWindow(InitEvent event);
	}

	private static final Method WINDOW_OPEN_METHOD;

	static {
		try {
			WINDOW_OPEN_METHOD = OpenListener.class.getDeclaredMethod("openWindow", OpenEvent.class);
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error, window open method not found");
		}
	}

	/**
	 * Class for holding information about a window open event. An
	 * {@link OpenEvent} is fired whenever the <code>Window</code> is opened.
	 * 
	 * @author kamil.
	 * @see OpenListener
	 */
	public class OpenEvent extends Component.Event {

		public OpenEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the Window.
		 * 
		 * @return the window.
		 */
		public Window getWindow() {
			return (Window) getSource();
		}
	}

	/**
	 * Interface for listening for a {@link OpenEvent} fired by a {@link Window}
	 * whenever the user opens the window.
	 * 
	 * @see OpenEvent
	 * @author kamil
	 */
	public interface OpenListener extends Serializable {

		/**
		 * Called whenever the user opens a window. A reference to the window is
		 * given by {@link OpenEvent#getWindow()}.
		 * 
		 * @param event
		 *            An event containing information about the window.
		 */
		void openWindow(OpenEvent event);
	}

}
