/**
 * 
 */
package org.vaadin.maps.shared.ui;

import java.io.Serializable;

import com.gwtent.reflection.client.Reflectable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Reflectable
public class Style implements Serializable {
	
	public static final Style DEFAULT = new Style();
	
	public static final Style DEFAULT_DRAW_CURSOR = new Style() {
		{
			strokeColor = "blue";
			fillColor = "cyan";
			fillOpacity = 0.3;
		}
	};
	
	public static final Style DEFAULT_DRAW_START_POINT = new Style() {
		{
			fillColor = "cyan";
			fillOpacity = 0.3;
		}
	};
	
	public static final Style DEFAULT_DRAW_VERTEX = new Style() {
		{
			strokeColor = "blue";
			fillOpacity = 0.0;
			pointRadius = 2;
		}
	};
	
	public static final Style DEFAULT_DRAW_LINE = new Style() {
		{
			strokeColor = "cyan";
			fillColor = "";
			fillOpacity = 0.0;
		}
	};
	
	public Style() {
		opacity = 1.0;
		strokeColor = "black";
		strokeWidth = 1;
		strokeOpacity = 1.0;
		fillColor = "white";
		fillOpacity = 1.0;
		pointRadius = 5;
		fontFamily = "Arial";
		fontSize = 20;
		textColor = "black";
		textStrokeColor = "";
		textStrokeWidth = 0;
		textOpacity = 1.0;
		textFillOpacity = 1.0;
	}
	
	public double opacity;
	public String strokeColor;
	public int strokeWidth;
	public double strokeOpacity;
	public String fillColor;
	public double fillOpacity;
	public int pointRadius;
	public String fontFamily;
	public int fontSize;
	public String textColor;
	public String textStrokeColor;
	public int textStrokeWidth;
	public double textOpacity;
	public double textFillOpacity;
	
}
