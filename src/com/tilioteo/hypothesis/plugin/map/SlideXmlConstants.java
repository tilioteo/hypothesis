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

	public static final String ID = "Id";
	public static final String TYPE = "Type";
	public static final String X = "X";
	public static final String Y = "Y";
	public static final String LAYER_ID = "LayerId";

	public static final String MAP = "Map";
	public static final String LAYERS = "Layers";
	public static final String IMAGES = "Images";
	public static final String IMAGE = "Image";
	public static final String IMAGE_LAYER = "ImageLayer";
	public static final String IMAGE_SEQUENCE_LAYER = "ImageSequenceLayer";
	public static final String FEATURE_LAYER = "FeatureLayer";
	public static final String FEATURES = "Features";
	public static final String FEATURE = "Feature";
	public static final String GEOMETRY = "Geometry";
	public static final String URL = "Url";
	public static final String VALUE = "Value";
	public static final String INDEX = "Index";
	public static final String CLICK = "Click";
	public static final String LOAD = "Load";
	public static final String CHANGE = "Change";
	public static final String TEXT = "Text";
	public static final String OFFSET = "Offset";
	public static final String CONTROLS = "Controls";
	public static final String DRAW_POINT = "DrawPoint";
	public static final String DRAW_PATH = "DrawPath";
	public static final String DRAW_POLYGON = "DrawPolygon";
	
	public static final String DRAW = "Draw";

	public static final String HIDDEN = "Hidden";

	public static final String STYLES = "Styles";
	public static final String STYLE = "Style";
	public static final String HOVER_STYLE = "HoverStyle";
	public static final String CURSOR_STYLE = "CursorStyle";
	public static final String START_POINT_STYLE = "StartPointStyle";
	public static final String LINE_STYLE = "LineStyle";
	public static final String VERTEX_STYLE = "VertexStyle";
	public static final String FINISH_STRATEGY = "FinishStrategy";
	
	public static final String OPACITY = "Opacity";
	public static final String STROKE_COLOR = "StrokeColor";
	public static final String STROKE_WIDTH = "StrokeWidth";
	public static final String STROKE_OPACITY = "StrokeOpacity";
	public static final String FILL_COLOR = "FillColor";
	public static final String FILL_OPACITY = "FillOpacity";
	public static final String POINT_RADIUS = "PointRadius";
	public static final String FONT_FAMILY = "FontFamily";
	public static final String FONT_SIZE = "FontSize";
	public static final String TEXT_COLOR = "TextColor";
	public static final String TEXT_STROKE_COLOR = "TextStrokeColor";
	public static final String TEXT_STROKE_WIDTH = "TextStrokeWidth";
	public static final String TEXT_OPACITY = "TextOpacity";
	public static final String TEXT_FILL_OPACITY = "TextFillOpacity";

	// public static final String ATTRIBUTES = "Attributes";
	// public static final String ATTRIBUTE = "Attribute";

	public static final StringSet VALID_LAYER_ELEMENTS = new StringSet(
			new String[] { IMAGE_LAYER, FEATURE_LAYER, IMAGE_SEQUENCE_LAYER });

	public static final StringSet VALID_CONTROL_ELEMENTS = new StringSet(
			new String[] { DRAW_POINT, DRAW_PATH });

	public static final StringSet VALID_FEATURE_ELEMENTS = new StringSet(
			new String[] { FEATURE });
	
	public static final StringSet STYLE_ATTRIBUTES = new StringSet(
			new String[] { OPACITY, STROKE_COLOR, STROKE_WIDTH, STROKE_OPACITY, FILL_COLOR, FILL_OPACITY,
					POINT_RADIUS, FONT_FAMILY, FONT_SIZE, TEXT_COLOR, TEXT_STROKE_COLOR,
					TEXT_STROKE_WIDTH, TEXT_OPACITY, TEXT_FILL_OPACITY });
}
