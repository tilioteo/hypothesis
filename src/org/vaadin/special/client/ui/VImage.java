/**
 * 
 */
package org.vaadin.special.client.ui;

import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author kamil
 * 
 */
public class VImage extends SimplePanel {

	public static final String CLASSNAME = "v-image";
	
	private Image image;

	public VImage() {
		super();

		setStylePrimaryName(CLASSNAME+"-container");
		
		image = new Image();
		image.setStylePrimaryName(CLASSNAME);
		setWidget(image);
	}

	/**
	 * Sets the URL of the image to be displayed. If the image is in the clipped
	 * state, a call to this method will cause a transition of the image to the
	 * unclipped state. Regardless of whether or not the image is in the clipped
	 * or unclipped state, a load event will be fired.
	 * 
	 * @param url
	 *            the image URL
	 */
	public void setUrl(String url) {
		image.setUrl(url);
	}

	/**
	 * Sets the alternate text of the image for user agents that can't render
	 * the image.
	 * 
	 * @param altText
	 *            the alternate text to set to
	 */
	public void setAltText(String altText) {
		image.setAltText(altText);
	}

	public HandlerRegistration addLoadHandler(LoadHandler handler) {
		return image.addLoadHandler(handler);
	}

	public HandlerRegistration addErrorHandler(ErrorHandler handler) {
		return image.addErrorHandler(handler);
	}
}
