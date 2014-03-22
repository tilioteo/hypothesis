/**
 * 
 */
package org.hypothesis.application.common.xml;

import org.hypothesis.common.StringSet;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Constants for parsing slide xml
 * 
 */
public class SlideXmlConstants {

	public static final String SLIDE_TEMPLATE = "SlideTemplate";
	public static final String SLIDE_CONTENT = "SlideContent";
	public static final String SLIDE = "Slide";
	public static final String SLIDE_DATA = "SlideData";
	public static final String SLIDE_OUTPUT = "SlideOutput";
	public static final String VIEWPORT = "Viewport";
	public static final String WINDOWS = "Windows";
	public static final String WINDOW = "Window";
	public static final String VARIABLES = "Variables";
	public static final String VARIABLE = "Variable";
	public static final String ACTIONS = "Actions";
	public static final String ACTION = "Action";
	public static final String INPUT_VALUE = "InputValue";
	public static final String OUTPUT_VALUE = "OutputValue";
	public static final String COMPONENT = "Component";
	public static final String COMPONENTS = "Components";
	public static final String BINDINGS = "Bindings";
	public static final String BIND = "Bind";
	public static final String PANEL = "Panel";
	public static final String VERTICAL_LAYOUT = "VerticalLayout";
	public static final String HORIZONTAL_LAYOUT = "HorizontalLayout";
	public static final String BUTTON = "Button";
	public static final String IMAGE = "Image";
	// public static final String MAP = "Map";
	public static final String BUTTON_PANEL = "ButtonPanel";
	public static final String RADIO_PANEL = "RadioPanel";
	public static final String FORM_PANEL = "FormPanel";
	public static final String COMBOBOX = "ComboBox";
	public static final String DATEFIELD = "DateField";
	public static final String TEXTFIELD = "TextField";
	public static final String TEXTAREA = "TextArea";
	public static final String LABEL = "Label";
	public static final String TIMERLABEL = "TimerLabel";
	public static final String PROPERTIES = "Properties";
	public static final String HANDLERS = "Handlers";
	public static final String REFERENCE = "Reference";
	// public static final String LAYERS = "Layers";
	// public static final String CONTROLS = "Controls";
	// public static final String ICONS = "Icons";
	// public static final String ICON = "Icon";
	// public static final String MARKERS = "Markers";
	// public static final String MARKER = "Marker";
	// public static final String FEATURES = "Features";
	// public static final String FEATURE = "Feature";
	// public static final String ATTRIBUTES = "Attributes";
	// public static final String COMPONENT = "Component";
	// public static final String PROPERTY = "Property";
	public static final String ITEMS = "Items";
	public static final String ITEM = "Item";
	// public static final String TEXT = "Text";
	public static final String EXPRESSION = "Expression";
	public static final String IF = "If";
	public static final String SWITCH = "Switch";
	public static final String CASE = "Case";
	public static final String CALL = "Call";
	public static final String TRUE = "True";
	public static final String FALSE = "False";

	// public static final String LAYER = "Layer";
	// public static final String CONTROL = "Control";
	// public static final String ATTRIBUTE = "Attribute";
	// public static final String STYLES = "Styles";
	// public static final String STYLE = "Style";
	// public static final String STYLE_MAPS = "StyleMaps";
	// public static final String STYLE_MAP = "StyleMap";
	// public static final String EVALUATION = "Evaluation";
	// public static final String EVALUATE = "Evaluate";

	public static final String BORDER = "Border";
	public static final String WIDTH = "Width";
	public static final String HEIGHT = "Height";
	public static final String ALIGNMENT = "Alignment";
	public static final String SPACING = "Spacing";
	public static final String URL = "Url";
	public static final String CAPTION = "Caption";
	public static final String CAPTIONS = "Captions";
	public static final String ORIENTATION = "Orientation";
	public static final String LABEL_POSITION = "LabelPosition";

	public static final String FINISH = "Finish";
	public static final String CLICK = "Click";
	public static final String LOAD = "Load";
	public static final String INIT = "Init";
	public static final String SHOW = "Show";
	public static final String CLOSE = "Close";

	public static final String TYPE = "Type";
	public static final String ID = "Id";
	public static final String NAME = "Name";
	public static final String VALUE = "Value";

	public static final String INTEGER = "Integer";
	public static final String BOOLEAN = "Boolean";
	public static final String FLOAT = "Float";
	public static final String OBJECT = "Object";

	public static final String UID = "UID";
	public static final String TEMPLATE_UID = "TemplateUID";

	public static final String FIELDS = "Fields";
	public static final String FIELD = "Field";

	public static final String EVENT_DATA = "EventData";
	public static final String SOURCE = "Source";
	public static final String SELECTED = "Selected";
	public static final String INDEX = "Index";
	public static final String X = "X";
	public static final String Y = "Y";

	// public static final String COPY_STYLE_ID = "CopyStyleId";
	// public static final String STYLE_MAP_ID = "StyleMapId";
	// public static final String EVENT = "Event";
	// public static final String NUMBER = "Nr";
	// public static final String ICON_ID = "IconId";
	// public static final String LONLAT = "LonLat";
	// public static final String SIZE = "Size";
	// public static final String OFFSET = "Offset";
	// public static final String IMAGE_URL = "ImageUrl";
	// public static final String GEOMETRY_TYPE = "GeometryType";
	// public static final String GEOMETRY = "Geometry";
	// public static final String GEOMETRY_DATA = "GeometryData";
	// public static final String LAYER_NUMBER = "LayerNr";
	// public static final String POINT_IN_RADIUS = "PointInRadius";
	// public static final String POINT_IN_AREA = "PointInArea";
	// public static final String LINE_IN_AREA = "LineInArea";
	// public static final String FEATURE_ATTRIBUTE = "FeatureAttribute";
	// public static final String POINT = "Point";
	// public static final String RADIUS = "Radius";
	// public static final String AREA = "Area";
	// public static final String BEGIN_POINT = "BeginPoint";
	// public static final String END_POINT = "EndPoint";
	// public static final String BEGIN_RADIUS = "BeginRadius";
	// public static final String END_RADIUS = "EndRadius";
	// public static final String PREVIOUS_TYPE = "PreviousSwitch";
	// public static final String PREVIOUS_ID = "PreviousId";

	/*
	 * public static final String TYPE_TextField = "TextField"; public static
	 * final String TYPE_DateField = "DateField"; public static final String
	 * TYPE_Window = "Window";
	 * 
	 * public static final String PROP_Html = "Html"; public static final String
	 * PROP_Name = "Name"; public static final String PROP_Multiline =
	 * "Multiline"; public static final String PROP_Label = "Label"; public
	 * static final String PROP_AllowBlank = "AllowBlank"; public static final
	 * String PROP_EmptyText = "EmptyText"; public static final String
	 * PROP_Required = "Required"; public static final String PROP_Width =
	 * "Width"; public static final String PROP_Height = "Height"; public static
	 * final String PROP_Border = "Border"; public static final String
	 * PROP_VerticalAlignment = "VerticalAlignment"; public static final String
	 * PROP_HorizontalAlignment = "HorizontalAlignment"; public static final
	 * String PROP_Time = "Time"; public static final String PROP_Start =
	 * "Start"; public static final String PROP_Ratio = "Ratio"; public static
	 * final String PROP_Padding = "Padding"; public static final String
	 * PROP_Spacing = "Spacing"; public static final String
	 * PROP_TextSizePercentage = "TextSizePercentage"; public static final
	 * String PROP_MapType = "MapType"; public static final String PROP_Center =
	 * "Center"; public static final String PROP_ZoomLevel = "ZoomLevel"; public
	 * static final String PROP_MaskTime = "MaskTime"; public static final
	 * String PROP_SolidMask = "SolidMask"; public static final String
	 * PROP_Orientation = "Orientation"; public static final String PROP_Titles
	 * = "Titles"; public static final String PROP_TitlePosition =
	 * "TitlePosition"; public static final String PROP_ShowTitles =
	 * "ShowTitles"; public static final String PROP_WaitData = "WaitData";
	 * public static final String PROP_ButtonsWidth = "ButtonsWidth"; public
	 * static final String PROP_ButtonsHeight = "ButtonsHeight"; public static
	 * final String PROP_ButtonsTextSizePercentage =
	 * "ButtonsTextSizePercentage"; public static final String PROP_ButtonsRatio
	 * = "ButtonsRatio"; public static final String PROP_ButtonsPadding =
	 * "ButtonsPadding"; public static final String PROP_Url = "Url"; public
	 * static final String PROP_Format = "Format"; public static final String
	 * PROP_Layers = "Layers"; public static final String PROP_Handler =
	 * "Handler"; public static final String PROP_Activate = "Activate"; public
	 * static final String PROP_Extent = "Extent"; public static final String
	 * PROP_MaxExtent = "MaxExtent"; public static final String PROP_Size =
	 * "Size"; public static final String PROP_Visible = "Visible"; public
	 * static final String PROP_ClickIconId = "ClickIconId"; public static final
	 * String PROP_Modal = "Modal"; public static final String PROP_SRS = "SRS";
	 * 
	 * public static final String PROP_FillColor = "FillColor"; public static
	 * final String PROP_FillOpacity = "FillOpacity"; public static final String
	 * PROP_StrokeColor = "StrokeColor"; public static final String
	 * PROP_StrokeOpacity = "StrokeOpacity"; public static final String
	 * PROP_StrokeWidth = "StrokeWidth"; public static final String
	 * PROP_StrokeDashstyle = "StrokeDashstyle"; public static final String
	 * PROP_PointRadius = "PointRadius";
	 * 
	 * 
	 * public static final String ACT_OnFinish = "onFinish"; public static final
	 * String ACT_OnUnmask = "onUnmask"; public static final String
	 * ACT_OnDataCompleted = "onDataCompleted"; public static final String
	 * ACT_OnSelectFeature = "onSelectFeature"; public static final String
	 * ACT_OnDrawFeature = "onDrawFeature";
	 */

	public static final StringSet VALID_SLIDE_ROOT_ELEMENTS = new StringSet(
			new String[] { SLIDE, SLIDE_TEMPLATE });

	public static final StringSet VALID_VIEWPORT_ELEMENTS = new StringSet(
			new String[] { HORIZONTAL_LAYOUT, PANEL, VERTICAL_LAYOUT });

	public static final StringSet VALID_WINDOW_ELEMENTS = VALID_VIEWPORT_ELEMENTS;

	public static final StringSet VALID_CONTAINER_ELEMENTS = new StringSet(
			new String[] { BUTTON, BUTTON_PANEL, FORM_PANEL, HORIZONTAL_LAYOUT,
					PANEL, IMAGE, RADIO_PANEL, LABEL, TIMERLABEL, VERTICAL_LAYOUT });

	public static final StringSet VALID_PANEL_ELEMENTS = new StringSet(
			new String[] { HORIZONTAL_LAYOUT, VERTICAL_LAYOUT });

	public static final StringSet VALID_FORM_ELEMENTS = new StringSet(
			new String[] { COMBOBOX, DATEFIELD, TEXTFIELD, TEXTAREA });

}
