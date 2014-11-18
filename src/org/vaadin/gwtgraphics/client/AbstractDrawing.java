/*
 * Copyright 2011 Henri Kerola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.gwtgraphics.client;

import org.vaadin.gwtgraphics.client.impl.DrawImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract base class for drawing objects the DrawingArea can contain.
 * 
 * renamed from VectorObject
 * 
 * @author Henri Kerola
 */
public abstract class AbstractDrawing extends Widget implements
		HasClickHandlers, HasAllMouseHandlers, HasDoubleClickHandlers {

	private Widget parent;

	protected static final DrawImpl impl = GWT.create(DrawImpl.class);

	protected AbstractDrawing() {
		setElement(impl.createElement(getType()));
	}

	protected DrawImpl getImpl() {
		return impl;
	}

	protected abstract Class<? extends AbstractDrawing> getType();

	public int getRotation() {
		return getImpl().getRotation(getElement());
	}

	public void setRotation(int degree) {
		getImpl().setRotation(getElement(), degree, isAttached());
	}

	@Override
	public Widget getParent() {
		return parent;
	}

	/**
	 * Sets this widget's parent. This method should only be called by
	 * {@link Panel} and {@link Composite}.
	 * 
	 * @param parent
	 *            the widget's new parent
	 * @throws IllegalStateException
	 *             if <code>parent</code> is non-null and the widget already has
	 *             a parent
	 */
	protected void setParent(Widget parent) {
		Widget oldParent = this.parent;
		if (parent == null) {
			try {
				if (oldParent != null && oldParent.isAttached()) {
					onDetach();
					assert !isAttached() : "Failure of "
							+ this.getClass().getName()
							+ " to call super.onDetach()";
				}
			} finally {
				// Put this in a finally in case onDetach throws an exception.
				this.parent = null;
			}
		} else {
			if (oldParent != null) {
				throw new IllegalStateException(
						"Cannot set a new parent without first clearing the old parent");
			}
			this.parent = parent;
			if (parent.isAttached()) {
				onAttach();
				assert isAttached() : "Failure of " + this.getClass().getName()
						+ " to call super.onAttach()";
			}
		}
	}

	@Override
	public void setStyleName(String style) {
		getImpl().setStyleName(getElement(), style);
	}
	
	@Override
	public void setStyleName(String style, boolean add) {
		getImpl().setStyleName(getElement(), style, add);
	}

	@Override
	public void setHeight(String height) {
		// nop
		/*throw new UnsupportedOperationException(
				"Vector drawing doesn't support setHeight");
		*/
	}

	@Override
	public void setWidth(String width) {
		// nop
		/*throw new UnsupportedOperationException(
				"Vector drawing doesn't support setHeight");
		*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.
	 * google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler
	 * (com.google.gwt.event.dom.client.DoubleClickHandler)
	 */
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return addDomHandler(handler, DoubleClickEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler
	 * (com.google.gwt.event.dom.client.MouseDownHandler)
	 */
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(
	 * com.google.gwt.event.dom.client.MouseUpHandler)
	 */
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler
	 * (com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler
	 * (com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler
	 * (com.google.gwt.event.dom.client.MouseMoveHandler)
	 */
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler
	 * (com.google.gwt.event.dom.client.MouseWheelHandler)
	 */
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		getImpl().onAttach(getElement(), isAttached());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();
	}

	/**
	 * Removes this drawing from its parent drawing, if one exists.
	 * 
	 * @throws IllegalStateException
	 *             if this widget's parent does not support removal
	 */
	@Override
	public void removeFromParent() {
		/*
		 * if (parent == null) { // If the widget had no parent, check to see if
		 * it was in the detach // list // and remove it if necessary. if
		 * (RootPanel.isInDetachList(this)) { RootPanel.detachNow(this); } }
		 * else if (parent instanceof HasWidgets) { ((HasWidgets)
		 * parent).remove(this); } else if (parent != null) { throw new
		 * IllegalStateException(
		 * "This widget's parent does not implement HasWidgets"); }
		 */
	}
	
	@Override
	public String getStylePrimaryName() {
		return "";
	}
}