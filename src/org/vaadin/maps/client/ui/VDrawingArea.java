/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.gwtgraphics.client.DrawingArea;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * @author kamil
 *
 */
public class VDrawingArea extends DrawingArea {
	
	public static final String CLASSNAME = "v-drawingarea";
	
	/**
	 * element created by drawing implementation (svg or vml)
	 */
	private Element drawingElement;
	
	public VDrawingArea() {
		super(0, 0);
		setStyleName(CLASSNAME);
	}

	/**
	 * Will be called by inherited constructor
	 * Don't call this method explicitly!
	 */
	@Override
	protected void setElement(Element elem) {
		// create new div as root element
		Element divElement = DOM.createDiv();
		drawingElement = elem;
		divElement.appendChild(elem);
		super.setElement(divElement);
	}

	@Override
	public Element getElement() {
		return drawingElement;
	}
	
	/**
	 * gets the div element of widget
	 * @return {@link Element}
	 */
	public Element getContainerElement() {
		return super.getElement();
	}

	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub
		super.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		// TODO Auto-generated method stub
		super.setHeight(height);
	}

	@Override
	public void setHeight(String height) {
		// TODO Auto-generated method stub
		super.setHeight(height);
	}

	@Override
	public void setWidth(String width) {
		// TODO Auto-generated method stub
		super.setWidth(width);
	}
	
	
	
}
