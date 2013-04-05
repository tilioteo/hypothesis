/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.CommandFactory;
import org.hypothesis.application.collector.core.SlideFactory;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.events.Command;
import org.hypothesis.application.collector.slide.AbstractBaseAction;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.application.collector.xml.SlideXmlUtility;
import org.hypothesis.common.StringMap;
import org.hypothesis.common.Strings;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Window extends com.vaadin.ui.Window implements Component {

	public class InitEvent extends Component.Event {

		/**
		 * 
		 * @param source
		 */
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
	 * An interface used for listening to Window init events. Add the
	 * InitListener to a browser level window or a sub window and
	 * {@link InitListener#windowInit(InitEvent)} will be called whenever the
	 * user opens the window for first time.
	 * 
	 */
	public interface InitListener extends Serializable {
		/**
		 * Called when the user opens a window for first time. Use
		 * {@link InitEvent#getWindow()} to get a reference to the
		 * {@link Window} that was initialized (opened for first time).
		 * 
		 * @param e
		 *            Event containing
		 */
		public void windowInit(InitEvent e);
	}

	public class OpenEvent extends Component.Event {

		/**
		 * 
		 * @param source
		 */
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
	 * An interface used for listening to Window open events. Add the
	 * OpenListener to a browser level window or a sub window and
	 * {@link OpenListener#windowOpen(OpenEvent)} will be called whenever the
	 * user opens the window.
	 * 
	 */
	public interface OpenListener extends Serializable {
		/**
		 * Called when the user opens a window. Use
		 * {@link OpenEvent#getWindow()} to get a reference to the
		 * {@link Window} that was opened.
		 * 
		 * @param e
		 *            Event containing
		 */
		public void windowOpen(OpenEvent e);
	}

	private SlideManager slideManager;

	private ParentAlignment parentAlignment;

	private boolean isInitialized = false;

	private static final Method WINDOW_OPEN_METHOD;

	static {
		try {
			WINDOW_OPEN_METHOD = OpenListener.class.getDeclaredMethod(
					"windowOpen", new Class[] { OpenEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error, window open method not found");
		}
	}

	private static final Method WINDOW_INIT_METHOD;

	static {
		try {
			WINDOW_INIT_METHOD = InitListener.class.getDeclaredMethod(
					"windowInit", new Class[] { InitEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error, window init method not found");
		}
	}

	public Window() {
		this.parentAlignment = new ParentAlignment();
	}

	public Window(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	private void addChilds(Element element) {
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
	 * Adds a InitListener to the window.
	 * 
	 * @param listener
	 *            the InitListener to add.
	 */
	public void addListener(InitListener listener) {
		addListener(InitEvent.class, listener, WINDOW_INIT_METHOD);
	}

	/**
	 * Adds a OpenListener to the window.
	 * 
	 * @param listener
	 *            the OpenListener to add.
	 */
	public void addListener(OpenListener listener) {
		addListener(OpenEvent.class, listener, WINDOW_OPEN_METHOD);
	}
	protected void fireOpen() {
		if (!isInitialized) {
			isInitialized = true;
			fireEvent(new Window.InitEvent(this));
		}
		fireEvent(new Window.OpenEvent(this));
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		addChilds(element);
		setHandlers(element);

	}

	public void open() {
		slideManager.getViewport().getComponent().getWindow().addWindow(this);
		fireOpen();
	}

	/**
	 * Removes the InitListener from the window.
	 * 
	 * <p>
	 * For more information on InitListeners see {@link InitListener}.
	 * </p>
	 * 
	 * @param listener
	 *            the InitListener to remove.
	 */
	public void removeListener(InitListener listener) {
		removeListener(InitEvent.class, listener, WINDOW_INIT_METHOD);
	}

	/**
	 * Removes the OpenListener from the window.
	 * 
	 * <p>
	 * For more information on OpenListeners see {@link OpenListener}.
	 * </p>
	 * 
	 * @param listener
	 *            the OpenListener to remove.
	 */
	public void removeListener(OpenListener listener) {
		removeListener(OpenEvent.class, listener, WINDOW_OPEN_METHOD);
	}
	private void setCloseHandler(String actionId) {
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				action.execute();
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = SlideXmlUtility.getAction(element);
		if (Strings.isNullOrEmpty(action)) {
			AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
					.createAnonymousAction(
							SlideXmlUtility.getActionElement(element));
			if (anonymousAction != null)
				action = anonymousAction.getId();
		}

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.INIT)) {
				setInitHandler(action);
			} else if (name.equals(SlideXmlConstants.SHOW)) {
				setShowHandler(action);
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
		addListener(new InitListener() {
			public void windowInit(InitEvent e) {
				action.execute();
			}
		});
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// set Button specific properties
		// TODO in future set dynamic css
	}

	private void setShowHandler(String actionId) {
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		addListener(new OpenListener() {
			public void windowOpen(OpenEvent e) {
				action.execute();
			}
		});
	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
