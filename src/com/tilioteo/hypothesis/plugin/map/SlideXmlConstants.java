/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import com.tilioteo.hypothesis.common.StringSet;

/**
 * @author Kamil Morong - Tilioteo Ltd
 * 
 *         Constants for parsing slide xml 
 * 
 */
public class SlideXmlConstants {
	
	public static final String NAMESPACE = "maps";
	
	public static final String MAP = "Map";
	public static final String LAYERS = "Layers";
	public static final String IMAGE_LAYER = "ImageLayer";
	public static final String FEATURE_LAYER = "FeatureLayer";
	public static final String FEATURES = "Features";
	public static final String FEATURE = "Feature";
	public static final String GEOMETRY = "Geometry";
	public static final String CONTROLS = "Controls";
	public static final String DRAW_POINT = "DrawPoint";
	public static final String DRAW_PATH = "DrawPath";
	//public static final String DRAW_POLYGON = "DrawPolygon";
	// public static final String ATTRIBUTES = "Attributes";
	// public static final String ATTRIBUTE = "Attribute";

	public static final StringSet VALID_LAYER_ELEMENTS = new StringSet(
			new String[] { IMAGE_LAYER, FEATURE_LAYER });

	public static final StringSet VALID_CONTROL_ELEMENTS = new StringSet(
			new String[] { DRAW_POINT, DRAW_PATH });

	public static final StringSet VALID_FEATURE_ELEMENTS = new StringSet(
			new String[] { FEATURE });
}
