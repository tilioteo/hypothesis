/**
 * 
 */
package org.hypothesis.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class VImage extends HTML implements Paintable {
	public static final String CLICK_EVENT_IDENTIFIER = "click";
	public static final String LOAD_EVENT_IDENTIFIER = "load";

	private static String CLASSNAME = "v-image";

	private String height;
	@SuppressWarnings("unused")
	private String width;
	private Element browserElement;

	private ApplicationConnection client;
	private String id;

	private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
			CLICK_EVENT_IDENTIFIER) {
		@Override
		protected <H extends EventHandler> HandlerRegistration registerHandler(
				H handler, Type<H> type) {
			return addDomHandler(handler, type);
		}
	};

	public VImage() {
		setStyleName(CLASSNAME);
	}

	/**
	 * Helper to return translated src-attribute from embedded's UIDL
	 * 
	 * @param uidl
	 * @param client
	 * @return
	 */
	private String getSrc(UIDL uidl, ApplicationConnection client) {
		String url = client.translateVaadinUri(uidl.getStringAttribute("src"));
		if (url == null) {
			return "";
		}
		return url;
	}

	private boolean isDynamicHeight() {
		return height == null || height.equals("");
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if (DOM.eventGetType(event) == Event.ONLOAD) {
			sendLoadEvent();
			Util.notifyParentOfSizeChange(this, true);
		}

		client.handleTooltipEvent(event, this);
	}

	@Override
	protected void onDetach() {
		if (BrowserInfo.get().isIE()) {
			// Force browser to fire unload event when component is detached
			// from the view (IE doesn't do this automatically)
			if (browserElement != null) {
				DOM.setElementAttribute(browserElement, "src",
						"javascript:false");
			}
		}
		super.onDetach();
	}

	private void sendLoadEvent() {
		// notify server
		client.updateVariable(id, LOAD_EVENT_IDENTIFIER, true, true);
	}

	@Override
	public void setHeight(String height) {
		this.height = height;
		super.setHeight(height);
	}

	@Override
	public void setWidth(String width) {
		this.width = width;
		if (isDynamicHeight()) {
			int oldHeight = getOffsetHeight();
			super.setWidth(width);
			int newHeight = getOffsetHeight();
			/*
			 * Must notify parent if the height changes as a result of a width
			 * change
			 */
			if (oldHeight != newHeight) {
				Util.notifyParentOfSizeChange(this, false);
			}
		} else {
			super.setWidth(width);
		}

	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}
		this.client = client;
		this.id = uidl.getId();

		boolean clearBrowserElement = true;

		clickEventHandler.handleEventHandlerRegistration(client);

		Element el = null;
		boolean created = false;
		NodeList<Node> nodes = getElement().getChildNodes();
		if (nodes != null && nodes.getLength() == 1) {
			Node n = nodes.getItem(0);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (e.getTagName().equals("IMG")) {
					el = e;
				}
			}
		}
		if (el == null) {
			setHTML("");
			el = DOM.createImg();
			created = true;
			client.addPngFix(el);
			DOM.sinkEvents(el, Event.ONLOAD);
		}

		// Set attributes
		Style style = el.getStyle();
		String w = uidl.getStringAttribute("width");
		if (w != null) {
			style.setProperty("width", w);
		} else {
			style.setProperty("width", "");
		}
		String h = uidl.getStringAttribute("height");
		if (h != null) {
			style.setProperty("height", h);
		} else {
			style.setProperty("height", "");
		}
		DOM.setElementProperty(el, "src", getSrc(uidl, client));

		if (created) {
			// insert in dom late
			getElement().appendChild(el);
		}

		/*
		 * Sink tooltip events so tooltip is displayed when hovering the image.
		 */
		sinkEvents(VTooltip.TOOLTIP_EVENTS);

		if (clearBrowserElement) {
			browserElement = null;
		}

	}
}
