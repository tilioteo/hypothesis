/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import com.tilioteo.hypothesis.common.StringSet;

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
	public static final String TIMERS = "Timers";
	public static final String TIMER = "Timer";
	public static final String INPUT_VALUE = "InputValue";
	public static final String OUTPUT_VALUE = "OutputValue";
	public static final String COMPONENTS = "Components";
	public static final String COMPONENT = "Component";
	public static final String BINDINGS = "Bindings";
	public static final String BIND = "Bind";
	public static final String PANEL = "Panel";
	public static final String VERTICAL_LAYOUT = "VerticalLayout";
	public static final String HORIZONTAL_LAYOUT = "HorizontalLayout";
	public static final String FORM_LAYOUT = "FormLayout";
	public static final String BUTTON = "Button";
	public static final String IMAGE = "Image";
	public static final String VIDEO = "Video";
	public static final String AUDIO = "Audio";
	public static final String BUTTON_PANEL = "ButtonPanel";
	public static final String SELECT_PANEL = "SelectPanel";
	public static final String COMBOBOX = "ComboBox";
	public static final String DATE_FIELD = "DateField";
	public static final String TEXT_FIELD = "TextField";
	public static final String TEXT_AREA = "TextArea";
	public static final String LABEL = "Label";
	public static final String TIMER_LABEL = "TimerLabel";
	public static final String PROPERTIES = "Properties";
	public static final String HANDLERS = "Handlers";
	public static final String VALIDATORS = "Validators";
	public static final String REFERENCE = "Reference";
	public static final String INSTANCE = "Instance";
	public static final String ITEMS = "Items";
	public static final String ITEM = "Item";
	public static final String EXPRESSION = "Expression";
	public static final String IF = "If";
	public static final String SWITCH = "Switch";
	public static final String CASE = "Case";
	public static final String CALL = "Call";
	public static final String TRUE = "True";
	public static final String FALSE = "False";
	public static final String BORDER = "Border";
	public static final String WIDTH = "Width";
	public static final String HEIGHT = "Height";
	public static final String CHILD_WIDTH = "ChildWidth";
	public static final String CHILD_HEIGHT = "ChildHeight";
	public static final String ALIGNMENT = "Alignment";
	public static final String SPACING = "Spacing";
	public static final String URL = "Url";
	public static final String CAPTIONS = "Captions";
	public static final String CAPTION = "Caption";
	public static final String ORIENTATION = "Orientation";
	public static final String LABEL_POSITION = "LabelPosition";
	public static final String MULTI_SELECT = "MultiSelect";
	public static final String READ_ONLY = "ReadOnly";
	public static final String ENABLED = "Enabled";
	public static final String VISIBLE = "Visible";
	public static final String CLICK = "Click";
	public static final String LOAD = "Load";
	public static final String INIT = "Init";
	public static final String SHOW = "Show";
	public static final String SHORTCUT = "Shortcut";
	public static final String OPEN = "Open";
	public static final String CLOSE = "Close";
	public static final String START = "Start";
	public static final String STOP = "Stop";
	public static final String UPDATE = "Update";
	public static final String TIME_FORMAT = "TimeFormat";
	public static final String TIMER_ID = "TimerId";
	public static final String TIME = "Time";
	public static final String DIRECTION = "Direction";
	public static final String INTERVAL = "Interval";
	public static final String MESSAGE = "Message";
	public static final String EMPTY = "Empty";
	public static final String NUMBER = "Number";
	public static final String RANGE = "Range";
	public static final String MIN = "Min";
	public static final String MAX = "Max";

	public static final String TYPE = "Type";
	public static final String ID = "Id";
	public static final String CLASS = "Class";
	public static final String NAME = "Name";
	public static final String VALUE = "Value";
	public static final String VALUES = "Values";
	public static final String FORMAT = "Format";

	public static final String INTEGER = "Integer";
	public static final String BOOLEAN = "Boolean";
	public static final String FLOAT = "Float";
	public static final String STRING = "String";
	public static final String OBJECT = "Object";
	public static final String INTEGER_ARRAY = "IntegerArray";
	public static final String BOOLEAN_ARRAY = "BooleanArray";
	public static final String FLOAT_ARRAY = "FloatArray";
	public static final String STRING_ARRAY = "StringArray";
	public static final String OBJECT_ARRAY = "ObjectArray";
	public static final String KEY = "Key";

	public static final String UID = "UID";
	public static final String TEMPLATE_UID = "TemplateUID";

	public static final String FIELDS = "Fields";
	public static final String FIELD = "Field";

	public static final String EVENT_DATA = "EventData";
	public static final String COMPONENT_DATA = "ComponentData";
	public static final String NAVIGATOR = "Navigator";
	public static final String SOURCES = "Sources";
	public static final String SOURCE = "Source";
	public static final String SELECTED = "Selected";
	public static final String INDEX = "Index";
	public static final String X = "X";
	public static final String Y = "Y";

	// TODO replace with DTD schema
	public static final StringSet VALID_SLIDE_ROOT_ELEMENTS = new StringSet(
			new String[] { SLIDE, SLIDE_TEMPLATE });

	public static final StringSet VALID_PANEL_ELEMENTS = new StringSet(
			new String[] { HORIZONTAL_LAYOUT, VERTICAL_LAYOUT, FORM_LAYOUT });

	public static final StringSet VALID_VIEWPORT_ELEMENTS = new StringSet(
			VALID_PANEL_ELEMENTS, new String[] { PANEL });

	public static final StringSet VALID_CONTAINER_ELEMENTS = new StringSet(
			VALID_VIEWPORT_ELEMENTS, new String[] { BUTTON, COMBOBOX, BUTTON_PANEL,
					DATE_FIELD, IMAGE, VIDEO, AUDIO, LABEL, SELECT_PANEL, TEXT_AREA, TEXT_FIELD, TIMER_LABEL });

	public static final StringSet VALID_WINDOW_ELEMENTS = VALID_PANEL_ELEMENTS;

}
