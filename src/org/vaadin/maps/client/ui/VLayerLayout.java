/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.StyleConstants;

/**
 * @author morong
 * 
 */
public class VLayerLayout extends ComplexPanel {

	/** Class name, prefix in styling */
	public static final String CLASSNAME = "v-layerlayout";

	protected final Element container = DOM.createDiv();

	protected Map<Widget, LayoutWrapper> widgetLayoutWrappers = new HashMap<Widget, LayoutWrapper>();
	
    private LayoutManager layoutManager;

	/**
	 * Default constructor
	 */
	public VLayerLayout() {
		setElement(Document.get().createDivElement());
		setupElement();

		getElement().appendChild(container);
		setupContainer(container);
	}

	private void setupElement() {
		Style style = getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setOverflow(Overflow.HIDDEN);
		setStyleName(CLASSNAME);
	}

	private void setupContainer(Element container) {
		Style style = container.getStyle();
		style.setLeft(0, Unit.PX);
		style.setTop(0, Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		style.setPosition(Position.ABSOLUTE);
		style.setZIndex(750);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.Panel#add(com.google.gwt.user.client.ui
	 * .Widget)
	 */
	@Override
	public void add(Widget child) {
		LayoutWrapper wrapper = new LayoutWrapper(child);
		wrapper.updateStyleNames();
		widgetLayoutWrappers.put(child, wrapper);
		super.add(wrapper.getWidget(), container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.ComplexPanel#remove(com.google.gwt.user
	 * .client.ui.Widget)
	 */
	@Override
	public boolean remove(Widget w) {
		LayoutWrapper wrapper = getChildWrapper(w);
		if (wrapper != null) {
			widgetLayoutWrappers.remove(w);
			return super.remove(w);
		}
		return super.remove(w);
	}

	/**
	 * Does this layout contain a widget
	 * 
	 * @param widget
	 *            The widget to check
	 * @return Returns true if the widget is in this layout, false if not
	 */
	public boolean contains(Widget widget) {
		return getChildWrapper(widget) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.ComplexPanel#getWidget(int)
	 */
	@Override
	public Widget getWidget(int index) {
		for (int i = 0, j = 0; i < super.getWidgetCount(); i++) {
			Widget w = super.getWidget(i);
			if (widgetLayoutWrappers.get(w) != null) {
				if (j == index) {
					return w;
				} else {
					j++;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.ComplexPanel#getWidgetCount()
	 */
	@Override
	public int getWidgetCount() {
		int counter = 0;
		for (int i = 0; i < super.getWidgetCount(); i++) {
			if (widgetLayoutWrappers.get(super.getWidget(i)) != null) {
				counter++;
			}
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.ComplexPanel#getWidgetIndex(com.google.
	 * gwt.user.client.ui.Widget)
	 */
	@Override
	public int getWidgetIndex(Widget child) {
		for (int i = 0, j = 0; i < super.getWidgetCount(); i++) {
			Widget w = super.getWidget(i);
			if (widgetLayoutWrappers.get(w) != null) {
				if (child == w) {
					return j;
				} else {
					j++;
				}
			}
		}
		return -1;
	}

	/**
	 * Set the position of the widget in the layout. The position is a CSS
	 * property string using properties such as top,left,right,top
	 * 
	 * @param child
	 *            The child widget to set the position for
	 * @param order
	 *            The position string
	 */
	public void setWidgetOrder(Widget child, String order) {
		LayoutWrapper wrapper = getChildWrapper(child);
		if (wrapper != null) {
			wrapper.setOrder(order);
		}
	}

	/**
	 * Get the wrapper for a widget
	 * 
	 * @param child
	 *            The child to get the wrapper for
	 * @return
	 */
	protected LayoutWrapper getChildWrapper(Widget child) {
		for (Widget w : getChildren()) {
			if (w == child) {
				LayoutWrapper wrapper = widgetLayoutWrappers.get(child);
				return wrapper;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.UIObject#setStylePrimaryName(java.lang.
	 * String)
	 */
	@Override
	public void setStylePrimaryName(String style) {
		updateStylenames(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.UIObject#setStyleName(java.lang.String)
	 */
	@Override
	public void setStyleName(String style) {
		super.setStyleName(style);
		updateStylenames(style);
		addStyleName(StyleConstants.UI_LAYOUT);
	}

	/**
	 * Updates all style names contained in the layout
	 * 
	 * @param primaryStyleName
	 *            The style name to use as primary
	 */
	protected void updateStylenames(String primaryStyleName) {
		super.setStylePrimaryName(primaryStyleName);
		container.setClassName(getStylePrimaryName() + "-container");

		for (LayoutWrapper wrapper : widgetLayoutWrappers.values()) {
			wrapper.updateStyleNames();
		}
	}

    /**
     * Set the layout manager for the layout
     * 
     * @param manager
     *            The layout manager to use
     */
    public void setLayoutManager(LayoutManager manager) {
        layoutManager = manager;
    }

    /**
     * Get the layout manager used by this layout
     * 
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }


	/**
	 * Cleanup old wrappers which have been left empty by other inner layouts
	 * moving the widget from the wrapper into their own hierarchy. This usually
	 * happens when a call to setWidget(widget) is done in an inner layout which
	 * automatically detaches the widget from the parent, in this case the
	 * wrapper, and re-attaches it somewhere else. This has to be done in the
	 * layout phase since the order of the hierarchy events are not defined.
	 */
	public void cleanupWrappers() {
		ArrayList<Widget> dirtyWidgets = new ArrayList<Widget>();
		WidgetCollection children = getChildren();
		for (LayoutWrapper wrapper : widgetLayoutWrappers.values()) {
			Widget w = wrapper.getWidget();
			if (!children.contains(w)) {
				dirtyWidgets.add(w);
			}
		}

		for (Widget w : dirtyWidgets) {
			widgetLayoutWrappers.remove(w);
		}
	}

	/**
	 * Sets style names for the widget in wrapper.
	 * 
	 * @param widget
	 *            The widget which wrapper we want to add the stylenames to
	 * @param stylenames
	 *            The style names that should be added to the wrapper
	 */
	public void setWidgetWrapperStyleNames(Widget widget, String... stylenames) {
		LayoutWrapper wrapper = getChildWrapper(widget);
		if (wrapper == null) {
			throw new IllegalArgumentException(
					"No wrapper for widget found, has the widget been added to the layout?");
		}
		wrapper.setWrapperStyleNames(stylenames);
	}
	
	/**
	 * Internal wrapper for wrapping widgets in the Layer layout
	 */
	protected class LayoutWrapper {
		private Widget widget;

		private String css;
		private String left;
		private String top;
		private String zIndex;

		private String[] extraStyleNames;

		/**
		 * Constructor
		 * 
		 * @param child
		 *            The child to wrap
		 */
		public LayoutWrapper(Widget child) {
			setWidget(child);
		}

		public void setWidget(Widget w) {
			// Validate
			if (w == widget) {
				return;
			}

			widget = w;
		}

		public Widget getWidget() {
			return widget;
		}

		/**
		 * Set the position for the wrapper in the layout
		 * 
		 * @param order
		 *            The position string
		 */
		public void setOrder(String order) {
			if (css == null || !css.equals(order)) {
				css = order;
				/*top = right = bottom = left =*/ zIndex = null;
				if (!css.equals("")) {
					String[] properties = css.split(";");
					for (int i = 0; i < properties.length; i++) {
						String[] keyValue = properties[i].split(":");
						if (keyValue[0].equals("left")) {
							left = keyValue[1];
						} else if (keyValue[0].equals("top")) {
							top = keyValue[1];
						} else if (keyValue[0].equals("z-index")) {
							zIndex = keyValue[1];
						}
					}
				}
				// ensure new values
				Style style = widget.getElement().getStyle();
				/*
				 * IE8 dies when nulling zIndex, even in IE7 mode. All other css
				 * properties (and even in older IE's) accept null values just
				 * fine. Assign empty string instead of null.
				 */
				if (zIndex != null) {
					style.setProperty("zIndex", zIndex);
				} else {
					style.setProperty("zIndex", "");
				}
				style.setProperty("top", top);
				style.setProperty("left", left);

				// layers must be positioned absolutely
				style.setPosition(Position.ABSOLUTE);
				// width and height 100%
				style.setWidth(100, Unit.PCT);
				style.setHeight(100, Unit.PCT);
			}
		}

		/**
		 * Sets the style names of the wrapper.
		 * 
		 * @param stylenames
		 *            The wrapper style names
		 */
		public void setWrapperStyleNames(String... stylenames) {
			extraStyleNames = stylenames;
			updateStyleNames();
		}

		/**
		 * Updates the style names using the primary style name as prefix
		 */
		protected void updateStyleNames() {
			if (extraStyleNames != null) {
				for (String stylename : extraStyleNames) {
					widget.addStyleDependentName(stylename);
				}
			}
		}
	}

}
