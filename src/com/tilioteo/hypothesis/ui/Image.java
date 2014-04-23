/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.event.ImageData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.shared.EventId;
import com.tilioteo.hypothesis.shared.ui.image.ImageServerRpc;
import com.tilioteo.hypothesis.shared.ui.image.ImageState;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Image extends com.vaadin.ui.Image implements SlideComponent {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	
    /**
     * Class for holding information about a image load event. A
     * {@link LoadEvent} is fired when the <code>Image</code> is successfully loaded.
     * 
     * @author kamil.
     * @see LoadListener
     */
	public static class LoadEvent extends Component.Event {

		public LoadEvent(Component source) {
			super(source);
		}
		
	}
	
    /**
     * Interface for listening for a {@link LoadEvent} fired by a
     * {@link Image}.
     * 
     * @see LoadEvent
     * @author kamil
     */
    public interface LoadListener extends ConnectorEventListener {

        public static final Method loadMethod = ReflectTools.findMethod(
                LoadListener.class, "load", LoadEvent.class);

        /**
         * Called when a {@link Image} has been successfully loaded. A reference to the
         * component is given by {@link LoadEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the image.
         */
        public void load(LoadEvent event);
    }

    /**
     * Class for holding information about a image error event. An
     * {@link ErrorEvent} is fired when the <code>Image</code> loading fails.
     * 
     * @author kamil
     * @see ErrorListener
     */
	public static class ErrorEvent extends Component.Event {

		public ErrorEvent(Component source) {
			super(source);
		}
		
	}
	
    /**
     * Interface for listening for a {@link ErrorEvent} fired by a
     * {@link Image}.
     * 
     * @see ErrorEvent
     * @author kamil
     */
    public interface ErrorListener extends ConnectorEventListener {

        public static final Method errorMethod = ReflectTools.findMethod(
                ErrorListener.class, "error", ErrorEvent.class);

        /**
         * Called when a {@link Image} loading failed. A reference to the
         * component is given by {@link ErrorEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the image.
         */
        public void error(ErrorEvent event);
    }

    protected ImageServerRpc rpc = new ImageServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Image.this, mouseDetails));
        }

		@Override
		public void load() {
			fireEvent(new LoadEvent(Image.this));
		}

		@Override
		public void error() {
			fireEvent(new ErrorEvent(Image.this));
		}
    };


	/**
	 * Creates a new empty Image object.
	 */
	public Image() {
        registerRpc(rpc);

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

    @Override
    protected ImageState getState() {
        return (ImageState) super.getState();
    }
    
    /**
     * Add a load listener to the component. The listener is called when
     * the image is successfully loaded.
     *  
     * Use {@link #removeLoadListener(LoadListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addLoadListener(LoadListener listener) {
        addListener(EventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class, listener,
                LoadListener.loadMethod);
    }

    /**
     * Remove a load listener from the component. The listener should earlier
     * have been added using {@link #addLoadListener(LoadListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeLoadListener(LoadListener listener) {
        removeListener(EventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class,
                listener);
    }

    /**
     * Add an error listener to the component. The listener is called when
     * the image loading failed.
     *  
     * Use {@link #removeErrorListener(ErrorListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addErrorListener(ErrorListener listener) {
        addListener(EventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class, listener,
                ErrorListener.errorMethod);
    }

    /**
     * Remove an error listener from the component. The listener should earlier
     * have been added using {@link #addErrorListener(ErrorListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeErrorListener(ErrorListener listener) {
        removeListener(EventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class,
                listener);
    }

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

	}

	private void setClickHandler(String actionId) {
		final ImageData data = new ImageData(this, slideManager);
		final Command componentEvent = CommandFactory
				.createImageClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId, data);

		addClickListener(new MouseEvents.ClickListener() {
			@Override
			public void click(MouseEvents.ClickEvent event) {
				data.setXY(event.getRelativeX(), event.getRelativeY());
				componentEvent.execute();
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
		final ImageData data = new ImageData(this, slideManager);
		final Command componentEvent = CommandFactory
				.createImageLoadEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId, data);

		addLoadListener(new LoadListener() {
			@Override
			public void load(LoadEvent event) {
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

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
