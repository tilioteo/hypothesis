/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.model.AbstractBaseAction;
import com.tilioteo.hypothesis.model.Command;
import com.tilioteo.hypothesis.model.CommandFactory;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Window extends com.vaadin.ui.Window implements SlideComponentContainer {

    private static final Method WINDOW_INIT_METHOD;
    
    static {
        try {
            WINDOW_INIT_METHOD = InitListener.class.getDeclaredMethod(
                    "initWindow", new Class[] { InitEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window init method not found");
        }
    }

    /**
     * Class for holding information about a window init event. An
     * {@link InitEvent} is fired when the <code>Window</code> is opened for the first time.
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
     * Interface for listening for a {@link InitEvent} fired by a
     * {@link Window} when user opens the window for the first time.
     * 
     * @see InitEvent
     * @author kamil
     */
	public interface InitListener extends Serializable {

		/**
		 * Called when the user opens a window for first time. A reference to the
         * window is given by {@link InitEvent#getWindow()}.
		 * 
         * @param event
         *            An event containing information about the window.
		 */
		public void initWindow(InitEvent event);
	}

    private static final Method WINDOW_OPEN_METHOD;
    
    static {
        try {
            WINDOW_OPEN_METHOD = OpenListener.class.getDeclaredMethod(
                    "openWindow", new Class[] { OpenEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window open method not found");
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
     * Interface for listening for a {@link OpenEvent} fired by a
     * {@link Window} whenever the user opens the window.
     * 
     * @see OpenEvent
     * @author kamil
     */
	public interface OpenListener extends Serializable {

		/**
		 * Called whenever the user opens a window. A reference to the
         * window is given by {@link OpenEvent#getWindow()}.
		 * 
         * @param event
         *            An event containing information about the window.
		 */
		public void openWindow(OpenEvent event);
	}

	private SlideManager slideManager;

	private ParentAlignment parentAlignment;

	private boolean initialized = false;

	public Window() {
		this.parentAlignment = new ParentAlignment();
	}

	public Window(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	@Override
	public void addXmlChilds(Element element) {
		List<Element> elements = SlideXmlUtility.getContainerComponents(
				element, SlideXmlConstants.VALID_WINDOW_ELEMENTS);
		for (Element element2 : elements) {
			LayoutComponent layoutComponent = ComponentFactory
					.createComponentFromElement(element2, slideManager);
			if (layoutComponent != null) {
				Component component = layoutComponent.getComponent();

				if (elements.size() == 1 && component instanceof Layout) {
					setContent((Layout) component);
				} else {
					GridLayout gridLayout = new GridLayout(1, 1);
					gridLayout.setSizeFull();
					setContent(gridLayout);
					gridLayout.addComponent(component);
					gridLayout.setComponentAlignment(component,
							layoutComponent.getAlignment());
				}
			}
		}
	}

    /**
     * Add an init listener to the component. The listener is called when
     * the window is opened for the first time.
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



	protected void fireOpen() {
		if (!initialized) {
			initialized = true;
			fireEvent(new InitEvent(this));
		}
		fireEvent(new OpenEvent(this));
	}

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		addXmlChilds(element);
		setHandlers(element);

	}

	public void open() {
		slideManager.getViewport().getComponent().getUI().addWindow(this);
		fireOpen();
	}

	private void setCloseHandler(String actionId) {
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		addCloseListener(new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				action.execute();
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
				.createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.INIT)) {
				setInitHandler(action);
			} else if (name.equals(SlideXmlConstants.OPEN)) {
				setOpenHandler(action);
			} else if (name.equals(SlideXmlConstants.CLOSE)) {
				setCloseHandler(action);
			}

			// TODO add other event handlers
		}
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideUtility.getHandlerElements(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
	}

	private void setInitHandler(String actionId) {
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		addInitListener(new InitListener() {
			@Override
			public void initWindow(InitEvent event) {
				action.execute();
			}
		});
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// set Window specific properties
		// TODO in future set dynamic css
	}

	private void setOpenHandler(String actionId) {
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		addOpenListener(new OpenListener() {
			@Override
			public void openWindow(OpenEvent e) {
				action.execute();
			}
		});
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
