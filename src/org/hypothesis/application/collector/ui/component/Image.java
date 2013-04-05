/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.CommandFactory;
import org.hypothesis.application.collector.core.SlideFactory;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.events.Command;
import org.hypothesis.application.collector.events.ImageData;
import org.hypothesis.application.collector.slide.AbstractBaseAction;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.application.collector.xml.SlideXmlUtility;
import org.hypothesis.common.StringMap;
import org.hypothesis.common.Strings;
import org.hypothesis.terminal.gwt.client.ui.VImage;

import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ClientWidget;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(VImage.class)
public class Image extends AbstractComponent implements Component {

	public class LoadEvent extends Component.Event {

		/**
		 * New instance of load event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public LoadEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the Image where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public Image getImage() {
			return (Image) getSource();
		}
	}

	/**
	 * Image load listener
	 */
	public interface LoadListener extends Serializable {

		/**
		 * Image has been loaded.
		 * 
		 * @param event
		 *            Image load event.
		 */
		public void loaded(LoadEvent event);

	}

	private static final String CLICK_EVENT = VImage.CLICK_EVENT_IDENTIFIER;

	private static final String LOAD_EVENT = VImage.LOAD_EVENT_IDENTIFIER;

	private static final Method LOAD_METHOD;

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	private HashSet<LoadListener> listeners = new HashSet<LoadListener>();

	/**
	 * Source of the image object.
	 */
	private Resource source = null;

	static {
		try {
			LOAD_METHOD = LoadListener.class.getDeclaredMethod("loaded",
					new Class[] { LoadEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error finding methods in Image");
		}
	}

	/**
	 * Creates a new empty Image object.
	 */
	public Image() {
		this.parentAlignment = new ParentAlignment();
	}

	/**
	 * Creates a new empty Image object with caption.
	 * 
	 * @param caption
	 */
	public Image(String caption) {
		this();
		setCaption(caption);
	}

	/**
	 * Creates a new Image object whose contents is loaded from given resource.
	 * The dimensions are assumed if possible. The type is guessed from
	 * resource.
	 * 
	 * @param caption
	 * @param source
	 *            the Source of the image object.
	 */
	public Image(String caption, Resource source) {
		this(caption);
		setSource(source);
	}

	/**
	 * Add a click listener to the component. The listener is called whenever
	 * the user clicks inside the component. Depending on the content the event
	 * may be blocked and in that case no event is fired.
	 * 
	 * Use {@link #removeListener(ClickListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addListener(ClickListener listener) {
		addListener(CLICK_EVENT, ClickEvent.class, listener,
				ClickListener.clickMethod);
	}

	/**
	 * Adds the image loaded listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(LoadListener listener) {
		listeners.add(listener);
		addListener(LoadEvent.class, listener, LOAD_METHOD);
		if (listeners.size() == 1) {
			requestRepaint();
		}
	}

	private void fireClick(Map<String, Object> parameters) {
		MouseEventDetails mouseDetails = MouseEventDetails
				.deSerialize((String) parameters.get("mouseDetails"));

		fireEvent(new ClickEvent(this, mouseDetails));
	}

	/**
	 * Emits the load event.
	 */
	protected void fireLoad() {
		fireEvent(new LoadEvent(this));
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	/**
	 * Gets the resource contained in the embedded object.
	 * 
	 * @return the Resource
	 */
	public Resource getSource() {
		return source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);
		if (variables.containsKey(LOAD_EVENT)) {
			fireLoad();
		}
		if (variables.containsKey(CLICK_EVENT)) {
			fireClick((Map<String, Object>) variables.get(CLICK_EVENT));
		}

	}

	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

	}

	/**
	 * Invoked when the component state should be painted.
	 */
	@Override
	public void paintContent(PaintTarget target) throws PaintException {

		if (getSource() != null) {
			target.addAttribute("src", getSource());
		}
	}

	/**
	 * Remove a click listener from the component. The listener should earlier
	 * have been added using {@link #addListener(ClickListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeListener(ClickListener listener) {
		removeListener(CLICK_EVENT, ClickEvent.class, listener);
	}

	/**
	 * Removes the link activated listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(LoadListener listener) {
		listeners.remove(listener);
		removeListener(LoadEvent.class, listener, LOAD_METHOD);
		if (listeners.size() == 0) {
			requestRepaint();
		}
	}

	private void setClickHandler(String actionId) {
		final ImageData data = new ImageData(this, slideManager);
		final Command componentEvent = CommandFactory
				.createImageClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addListener(new MouseEvents.ClickListener() {
			public void click(MouseEvents.ClickEvent event) {
				data.setXY(event.getClientX(), event.getClientY());
				componentEvent.execute();
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
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			} else if (name.equals(SlideXmlConstants.LOAD)) {
				setLoadHandler(action);
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

	private void setLoadHandler(String actionId) {
		final Command componentEvent = CommandFactory
				.createImageLoadEventCommand(this, slideManager);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addListener(new Image.LoadListener() {
			public void loaded(Image.LoadEvent event) {
				componentEvent.execute();
				action.execute();
			}
		});
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		setSource(new ExternalResource(
				properties.get(SlideXmlConstants.URL, "")));

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	/**
	 * Sets the object source resource. The dimensions are assumed if possible.
	 * The type is guessed from resource.
	 * 
	 * @param source
	 *            the source to set.
	 */
	public void setSource(Resource source) {
		if (source != null && !source.equals(this.source)) {
			this.source = source;

			requestRepaint();
		}
	}

}
