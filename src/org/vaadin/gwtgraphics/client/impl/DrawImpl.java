/**
 * 
 */
package org.vaadin.gwtgraphics.client.impl;

import java.util.List;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.gwtgraphics.client.shape.path.PathStep;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author morong
 * 
 */
public abstract class DrawImpl {

	public abstract String getRendererString();

	public abstract String getStyleSuffix();

	public abstract void setStyleName(Element element, String name);

	public abstract void setStyleName(Element element, String name, boolean add);

	public abstract Element createElement(Class<? extends AbstractDrawing> type);

	public abstract void initCanvasSize(Element element, int width, int height);

	public abstract int getX(Element element);

	public abstract void setX(final Element element, final int x,
			final boolean attached);

	public abstract int getY(Element element);

	public abstract void setY(final Element element, final int y,
			final boolean attached);

	public abstract int getWidth(Element element);

	public abstract void setWidth(Element element, int width);
	
	public abstract void setAreaWidth(Element element, String value);

	public abstract int getHeight(Element element);

	public abstract void setHeight(Element element, int height);

	public abstract void setAreaHeight(Element element, String value);

	public abstract String getFillColor(Element element);

	public abstract void setFillColor(Element element, String color);

	public abstract double getFillOpacity(Element element);

	public abstract void setFillOpacity(Element element, double opacity);

	public abstract String getStrokeColor(Element element);

	public abstract void setStrokeColor(Element element, String color);

	public abstract int getStrokeWidth(Element element);

	public abstract void setStrokeWidth(Element element, int width,
			boolean attached);

	public abstract double getStrokeOpacity(Element element);

	public abstract void setStrokeOpacity(Element element, double opacity);
	
	public abstract double getOpacity(Element element);

	public abstract void setOpacity(Element element, double opacity);

	public abstract int getCircleRadius(Element element);

	public abstract void setCircleRadius(Element element, int radius);

	public abstract int getEllipseRadiusX(Element element);

	public abstract void setEllipseRadiusX(Element element, int radiusX);

	public abstract int getEllipseRadiusY(Element element);

	public abstract void setEllipseRadiusY(Element element, int radiusY);

	public abstract int getRectangleRoundedCorners(Element element);

	public abstract void setRectangleRoundedCorners(Element element, int radius);

	public abstract int getLineX2(Element element);

	public abstract void setLineX2(Element element, int x2);

	public abstract int getLineY2(Element element);

	public abstract void setLineY2(Element element, int y2);

	public abstract String getImageHref(Element element);

	public abstract void setImageHref(Element element, String src);

	public abstract String getText(Element element);

	public abstract void setText(Element element, String text, boolean attached);

	public abstract String getTextFontFamily(Element element);

	public abstract void setTextFontFamily(Element element, String family,
			boolean attached);

	public abstract int getTextFontSize(Element element);

	public abstract void setTextFontSize(Element element, int size,
			boolean attached);

	public abstract void drawPath(Element element, List<PathStep> steps);

	public abstract void getPathStepString(Element element, PathStep step);

	public abstract void add(Element root, Element element, boolean attached);

	public abstract void insert(Element root, Element element, int beforeIndex,
			boolean attached);

	public abstract void remove(Element root, Element element);

	public abstract void bringToFront(Element root, Element element);

	public abstract void clear(Element root);

	public abstract void setRotation(final Element element, final int degree,
			final boolean attached);

	public abstract int getRotation(Element element);

	public abstract void onAttach(Element element, boolean attached);

	// Note that VMLImpl uses this same method impl.
	public int getTextWidth(Element element) {
		return measureTextSize(element, true);
	}

	// Note that VMLImpl uses this same method impl.
	public int getTextHeight(Element element) {
		return measureTextSize(element, false);
	}

	protected int measureTextSize(Element element, boolean measureWidth) {
		String text = getText(element);
		if (text == null || "".equals(text)) {
			return 0;
		}

		DivElement measureElement = Document.get().createDivElement();
		Style style = measureElement.getStyle();
		style.setProperty("visibility", "hidden");
		style.setProperty("display", "inline");
		style.setProperty("whiteSpace", "nowrap");
		style.setProperty("fontFamily", getTextFontFamily(element));
		style.setPropertyPx("fontSize", getTextFontSize(element));
		measureElement.setInnerText(text);
		RootPanel.getBodyElement().appendChild(measureElement);
		int measurement;
		if (measureWidth) {
			measurement = measureElement.getOffsetWidth();
		} else {
			measurement = measureElement.getOffsetHeight();
		}
		RootPanel.getBodyElement().removeChild(measureElement);

		return measurement;
	}

	public abstract void setPathFillEvenOdd(Element element);
}
