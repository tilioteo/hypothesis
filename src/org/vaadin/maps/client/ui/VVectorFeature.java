/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.gwtgraphics.client.AbstractDrawingContainer;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.shape.Text;
import org.vaadin.maps.client.drawing.Utils;
import org.vaadin.maps.client.drawing.Utils.PointShape;
import org.vaadin.maps.client.geometry.Coordinate;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.shared.ui.Style;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VVectorFeature extends AbstractDrawingContainer implements CanShift {
	
	public static final String CLASSNAME = "v-vectorfeature";
	
	private Geometry geometry = null;
	private AbstractDrawing drawing = null;
	private String text = null;
	private Text textShape = null;
	private Style style = Style.DEFAULT;
	private Style hoverStyle = null;
	private Coordinate centroid = null;
	private Coordinate textOffset = new Coordinate();
	private boolean hidden = false;
	
	private PointShape pointShape = PointShape.Circle;
	private double pointShapeScale = 1.0;
	
	private HandlerRegistration mouseOverHandler = null;
	private HandlerRegistration mouseOutHandler = null;
	
	private int shiftX = 0;
	private int shiftY = 0;
	
	public VVectorFeature() {
		super();
		setStyleName(CLASSNAME);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		if (geometry != null) {
			if (!geometry.equals(this.geometry)) {
				clear();
				// create new vector object and insert it into feature root element
				drawGeometry(geometry);
			}
		} else {
			clear();
		}

		this.geometry = geometry;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		if (text != null) {
			if (!text.equals(this.text)) {
				drawText(text);
			}
		} else {
			clearText();
		}
	}
	
	public Style getStyle() {
		return style;
	}
	
	public void setStyle(Style style) {
		this.style = style;
		
		setPointShape();
		
		updateDrawingStyle();
		updateTextStyle();
	}
	
	private void setPointShape() {
		if (style != null) {
			pointShape = Utils.pointShapeFromString(style.pointShape);
			if (null == pointShape) {
				pointShape = PointShape.Circle;
			}
		
			pointShapeScale = style.pointShapeScale;
		} else {
			pointShape = PointShape.Circle;
			pointShapeScale = 1.0;
		}
	}

	public Style getHoverStyle() {
		return hoverStyle;
	}
	
	public void setHoverStyle(Style style) {
		this.hoverStyle = style;
		updateHoverStyle();
		//updateTextStyle();
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		if (this.hidden != hidden) {
			this.hidden = hidden;
			
			VVectorFeatureContainer container = null;
			Widget parent = getParent();
			if (parent instanceof VVectorFeatureContainer) {
				container = (VVectorFeatureContainer) parent;
			} else {
				Widget grandParent = parent.getParent();
				if (grandParent instanceof VVectorFeatureContainer) {
					container = (VVectorFeatureContainer) grandParent;
				}
			}
			if (container != null) {
				container.remove(this);
				container.add(this);
			}
		}
	}

	private void updateDrawingStyle() {
		if (drawing != null && style != null) {
			Utils.updateDrawingStyle(drawing, style);
		}
	}

	private void updateHoverStyle() {
		if (drawing != null) {
			if (hoverStyle != null) {
				mouseOverHandler = drawing.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						Utils.updateDrawingStyle(drawing, hoverStyle);
					}
				});
				mouseOutHandler = drawing.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						Utils.updateDrawingStyle(drawing, style);
					}
				});
			} else {
				if (mouseOverHandler != null) {
					mouseOverHandler.removeHandler();
					mouseOverHandler = null;
				}
				if (mouseOutHandler != null) {
					mouseOutHandler.removeHandler();
					mouseOutHandler = null;
				}
			}
		}
	}

	private void updateTextStyle() {
		if (textShape != null && style != null) {
			Utils.updateDrawingStyle(textShape, style);
		}
	}

	@Override
	public void clear() {
		super.clear();
		drawing = null;
	}
	
	public void clearText() {
		if (textShape != null) {
			remove(textShape);
		}
		textShape = null;
		text = null;
	}

	private void drawGeometry(Geometry geometry) {
		drawing = Utils.drawGeometry(geometry, pointShape, pointShapeScale);
		updateDrawingStyle();
		updateHoverStyle();
		add(drawing);
	}
	
	public void setCentroid(Double x, Double y) {
		if (x != null && y != null) {
			centroid = new Coordinate(x, y);
		} else {
			centroid = null;
		}
		updateTextPosition();
	}

	public void setTextOffset(double x, double y) {
		textOffset.setXY(x, y);
		updateTextPosition();
	}

	private void updateTextPosition() {
		if (textShape != null && centroid != null) {
			textShape.setX(Math.round((float)(centroid.getX() + textOffset.getX())));
			textShape.setY(Math.round((float)(centroid.getY() + textOffset.getY())));
		}
	}

	private void drawText(String text) {
		if (null == textShape) {
			textShape = new Text(0, 0, text);
			updateTextPosition();
			updateTextStyle();
			add(textShape);
		} else {
			textShape.setText(text);
		}
		this.text = text;
	}
	
	public AbstractDrawing getDrawing() {
		return drawing;
	}

	/**
	 * Returns type of Group class, constructor will create its implementation as root element 
	 */
	@Override
	protected Class<? extends AbstractDrawing> getType() {
		return Group.class;
	}

	public int getShiftX() {
		return shiftX;
	}

	public int getShiftY() {
		return shiftY;
	}

	@Override
	public void setShift(int x, int y) {
		shiftX = x;
		shiftY = y;
		
		getImpl().setX(getElement(), x, isAttached());
		getImpl().setY(getElement(), y, isAttached());
	}

}
