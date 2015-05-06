/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringConstants;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.evaluable.ExpressionFactory;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.ActionEvent;
import com.tilioteo.hypothesis.event.AudioData;
import com.tilioteo.hypothesis.event.ButtonData;
import com.tilioteo.hypothesis.event.ButtonPanelData;
import com.tilioteo.hypothesis.event.ImageData;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.event.SelectPanelData;
import com.tilioteo.hypothesis.event.SlideData;
import com.tilioteo.hypothesis.event.SlideEvent;
import com.tilioteo.hypothesis.event.TimerData;
import com.tilioteo.hypothesis.event.VideoData;
import com.tilioteo.hypothesis.event.WindowData;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Field;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Action;
import com.tilioteo.hypothesis.processing.Call;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.IfStatement;
import com.tilioteo.hypothesis.processing.SwitchStatement;
import com.tilioteo.hypothesis.processing.Variable;
import com.tilioteo.hypothesis.slide.ui.Button;
import com.tilioteo.hypothesis.slide.ui.ComponentFactory;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideFactory {

	private static HashMap<SlideFascia, SlideFactory> instances = new HashMap<SlideFascia, SlideFactory>();
	
	public static SlideFactory getInstance(SlideFascia slideFascia) {
		SlideFactory slideFactory = instances.get(slideFascia);
		
		if (null == slideFactory) {
			slideFactory = new SlideFactory(slideFascia);
			instances.put(slideFascia, slideFactory);
		}
		return slideFactory;
	}
	
	public static void remove(SlideFascia slideFascia) {
		instances.remove(slideFascia);
	}

	private SlideFascia slideFascia = null;

	private SlideFactory(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	public static void writeButtonData(Element sourceElement, ButtonData buttonData) {
		String id = buttonData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.BUTTON);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		Button sender = buttonData.getSender();
		sourceElement.addText(sender.getCaption());
	}

	public static void writeButtonPanelData(Element sourceElement, ButtonPanelData buttonPanelData) {
		String id = buttonPanelData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.BUTTON_PANEL);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		int index = buttonPanelData.getButtonIndex();
		if (index > 0) {
			Element selectedElement = sourceElement.addElement(SlideXmlConstants.SELECTED);
			selectedElement.addAttribute(SlideXmlConstants.INDEX, String.format("%d", index));
			selectedElement.addText(buttonPanelData.getButton().getCaption());
		}
	}

	public static void writeActionData(Document doc, ActionEvent actionEvent) {
		Element root = doc.getRootElement();
		Element sourceElement = root.addElement(SlideXmlConstants.SOURCE);
		writeSourceData(sourceElement, actionEvent);
	}

	public static void writeComponentData(Element element, AbstractComponentEvent<?> componentEvent) {
		Element sourceElement = element.addElement(SlideXmlConstants.SOURCE);
		writeSourceData(sourceElement, componentEvent);
	}

	public static void writeSlideEventData(Element element, SlideEvent slideEvent) {
		Element sourceElement = element.addElement(SlideXmlConstants.SOURCE);
		writeSourceData(sourceElement, slideEvent);
	}

	public static void writeImageData(Element sourceElement, ImageData imageData) {
		String id = imageData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.IMAGE);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		if (imageData.hasCoordinate()) {
			Element subElement = sourceElement.addElement(SlideXmlConstants.X);
			// use Locale.ROOT for locale neutral formating of decimals
			subElement.addText(String.format(Locale.ROOT, "%g", imageData.getCoordinate().x));
			subElement = sourceElement.addElement(SlideXmlConstants.Y);
			subElement.addText(String.format(Locale.ROOT, "%g",	imageData.getCoordinate().y));
		}
	}

	public static void writeVideoData(Element sourceElement, VideoData videoData) {
		String id = videoData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.VIDEO);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		if (videoData.hasCoordinate()) {
			Element subElement = sourceElement.addElement(SlideXmlConstants.X);
			// use Locale.ROOT for locale neutral formating of decimals
			subElement.addText(String.format(Locale.ROOT, "%g", videoData.getCoordinate().x));
			subElement = sourceElement.addElement(SlideXmlConstants.Y);
			subElement.addText(String.format(Locale.ROOT, "%g",	videoData.getCoordinate().y));
		} if (videoData.getTime() > 0) {
			Element subElement = sourceElement.addElement(SlideXmlConstants.TIME);
			// use Locale.ROOT for locale neutral formating of decimals
			subElement.addText(String.format(Locale.ROOT, "%g", videoData.getTime()));
		}
	}

	public static void writeAudioData(Element sourceElement, AudioData audioData) {
		String id = audioData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.AUDIO);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		} if (audioData.getTime() > 0) {
			Element subElement = sourceElement.addElement(SlideXmlConstants.TIME);
			// use Locale.ROOT for locale neutral formating of decimals
			subElement.addText(String.format(Locale.ROOT, "%g", audioData.getTime()));
		}
	}

	public static void writeTimerData(Element sourceElement, TimerData timerData) {
		String id = timerData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.TIMER);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		Element subElement = sourceElement.addElement(SlideXmlConstants.TIME);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%d", timerData.getTime()));
	}

	public static void writeWindowData(Element sourceElement, WindowData windowData) {
		String id = windowData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.WINDOW);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
	}

	public static void writeSelectPanelData(Element sourceElement, SelectPanelData selectPanelData) {
		String id = selectPanelData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.SELECT_PANEL);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		int index = selectPanelData.getButtonIndex();
		if (index > 0) {
			Element selectedElement = sourceElement.addElement(SlideXmlConstants.SELECTED);
			selectedElement.addAttribute(SlideXmlConstants.INDEX, String.format("%d", index));
			selectedElement.addAttribute(SlideXmlConstants.VALUE, selectPanelData.getButton().getValue() ? "true" : "false");
			selectedElement.addText(selectPanelData.getButton().getCaption());
		}
	}
	
	public static void writeSlideData(Element sourceElement, SlideData slideData) {
		String id = slideData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.SLIDE);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		
		String shortcutKey = slideData.getShortcutKey();
		if (shortcutKey != null) {
			Element subElement = sourceElement.addElement(SlideXmlConstants.SHORTCUT);
			subElement.addAttribute(SlideXmlConstants.KEY, shortcutKey);
		}
	}

	private static void writeSourceData(Element sourceElement, ActionEvent actionEvent) {
		com.tilioteo.hypothesis.interfaces.Action action = actionEvent.getAction();
		String id = action.getId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.ACTION);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
	}
	
	private static void writeSourceData(Element sourceElement, AbstractComponentEvent<?> componentEvent) {
		AbstractComponentData<?> componentData = componentEvent.getComponentData();
		componentData.writeDataToElement(sourceElement);
	}

	private static void writeSourceData(Element sourceElement, SlideEvent slideEvent) {
		SlideData slideData = slideEvent.getComponentData();
		slideData.writeDataToElement(sourceElement);
	}

	private void writeFieldsData(Element element, Map<String, Field> fields) {
		Element fieldsElement = element.addElement(SlideXmlConstants.FIELDS);
		for (Field field : fields.values()) {
			writeFieldData(fieldsElement, field);
		}
	}

	private void writeFieldData(Element element, Field field) {
		Element fieldElement = element.addElement(SlideXmlConstants.FIELD);
		field.writeDataToElement(fieldElement);
	}

	private void writeVariablesData(Element element, Map<String, com.tilioteo.hypothesis.interfaces.Variable<?>> variables) {
		Element variablesElement = element.addElement(SlideXmlConstants.VARIABLES);
		for (com.tilioteo.hypothesis.interfaces.Variable<?> variable : variables.values()) {
			String name = variable.getName();
			if (!(name.equals(SlideXmlConstants.COMPONENT_DATA) ||
					name.equals(SlideXmlConstants.NAVIGATOR))) {
				writeVariableData(variablesElement, variable);
			}
		}
	}

	private void writeVariableData(Element element, com.tilioteo.hypothesis.interfaces.Variable<?> variable) {
		Class<?> type = variable.getType();
		String typeName = "";
		if (type.equals(Integer.class)) {
			typeName = SlideXmlConstants.INTEGER;
		} else if (type.equals(Double.class)) {
			typeName = SlideXmlConstants.FLOAT;
		} else if (type.equals(Boolean.class)) {
			typeName = SlideXmlConstants.BOOLEAN;
		} else if (type.equals(String.class)) {
			typeName = SlideXmlConstants.STRING;
		} else if (type.equals(Object.class)) {
			typeName = SlideXmlConstants.OBJECT;
		}
		
		if (!typeName.isEmpty()) {
			String value = "";
			if (variable.getValue() != null) {
				value = variable.getStringValue();
			}
			Element variableElement = element.addElement(SlideXmlConstants.VARIABLE);
			variableElement.addAttribute(SlideXmlConstants.ID, variable.getName());
			variableElement.addAttribute(SlideXmlConstants.TYPE, typeName);
			variableElement.addText(value);
		}
	}

	private void writeOutputValues(Element element, Map<Integer, ExchangeVariable> outputValues) {
		Element outputValuesElement = element.addElement(SlideXmlConstants.OUTPUT_VALUES);
		for (ExchangeVariable outputValueExpression : outputValues.values()) {
			String indexString = "" + outputValueExpression.getIndex();
			Object value = outputValueExpression.getValue();
			
			if (value != null) {
				Element outputValueElement = outputValuesElement.addElement(SlideXmlConstants.OUTPUT_VALUE);
				outputValueElement.addAttribute(SlideXmlConstants.INDEX, indexString);
				writeOutputValue(outputValueElement, value);
			}
		}
	}

	private void writeOutputValue(Element element, Object value) {
		Class<?> type = value.getClass();
		
		if (type == double.class || type == float.class || type.isAssignableFrom(Double.class)) {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.FLOAT);
			// use Locale.ROOT for locale neutral formating of decimals
			element.addText(String.format(Locale.ROOT, "%g", ((Double) value).doubleValue()));
		} else if (type == int.class || type == short.class || type.isAssignableFrom(Integer.class)) {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.INTEGER);
			element.addText(((Integer) value).toString());
		} else if (type == long.class || type.isAssignableFrom(Long.class)) {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.INTEGER);
			element.addText(((Long) value).toString());
		} else if (type == boolean.class || type.isAssignableFrom(Boolean.class)) {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.BOOLEAN);
			element.addText(((Boolean) value).toString());
		} else if (type.isAssignableFrom(String.class) || value instanceof String) {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.STRING);
			element.addText((String) value);
		} else {
			element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.OBJECT);
			// TODO serialize object type values
		}
	}

	private AbstractBaseAction createAction(Element element) {
		if (element != null) {
			String id = SlideXmlUtility.getId(element);
			return createAction(element, id);
		}
		return null;
	}

	private AbstractBaseAction createAction(Element element, String id) {
		if (element.getName().equals(SlideXmlConstants.ACTION)) {
			return createInnerAction(element, id);
		}
		return null;
	}

	private void createActions(Element rootElement) {
		List<Element> actions = SlideXmlUtility.getActionsElements(rootElement);
		for (Element actionElement : actions) {
			String id = SlideXmlUtility.getId(actionElement);
			if (!Strings.isNullOrEmpty(id)) {
				final AbstractBaseAction action = createAction(actionElement);
				if (action != null) {
					action.setExecuteCommand(new Command() {
						@Override
						public void execute() {
							ProcessEventBus.get().post(new ActionEvent(action));
						}
					});
					slideFascia.setAction(id, action);
				}
			}
		}
	}

	public AbstractBaseAction createAnonymousAction(Element element) {
		if (element != null) {
			String id = UUID.randomUUID().toString();
			AbstractBaseAction action = createInnerAction(element, id);
			if (action != null)
				slideFascia.setAction(id, action);
			return action;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private AbstractBaseAction createInnerAction(Element element, String id) {
		if (element != null && !Strings.isNullOrEmpty(id)) {
			Action action = new Action(slideFascia, id);
			List<Element> elements = element.elements();
			for (Element evaluableElement : elements) {
				Evaluable evaluable = createEvaluable(evaluableElement);
				if (evaluable != null)
					action.add(evaluable);
			}
			return action;
		}
		return null;
	}

	private Call createCall(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.CALL)) {
			String actionId = SlideXmlUtility.getAction(element);
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Call(slideFascia, actionId);
			}
		}
		return null;
	}

	private Evaluable createEvaluable(Element element) {
		if (element != null) {
			String name = element.getName();

			if (name.equals(SlideXmlConstants.EXPRESSION)) {
				return createExpression(element);
			} else if (name.equals(SlideXmlConstants.IF)) {
				return createIfStatement(element);
			} else if (name.equals(SlideXmlConstants.SWITCH)) {
				return createSwitchStatement(element);
			} else if (name.equals(SlideXmlConstants.CALL)) {
				return createCall(element);
			}
		}
		return null;
	}

	private Expression createExpression(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(element.getTextTrim()));
		}
		return null;
	}

	private IfStatement createIfStatement(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.IF)) {
			Element expressionElement = SlideXmlUtility.getExpressionElement(element);
			Element trueElement = SlideXmlUtility.getTrueElement(element);
			Element falseElement = SlideXmlUtility.getFalseElement(element);
			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				IfStatement statement = new IfStatement(slideFascia,
						expression);

				for (int i = 0; i < 2; ++i) {
					@SuppressWarnings("unchecked")
					List<Element> elements = i == 0 ? trueElement != null ? trueElement.elements() : null
							: falseElement != null ? falseElement.elements() : null;
					if (elements != null) {
						for (Element evaluableElement : elements) {
							Evaluable evaluable = createEvaluable(evaluableElement);
							if (evaluable != null) {
								if (i == 0)
									statement.addTrueEvaluable(evaluable);
								else
									statement.addFalseEvaluable(evaluable);
							}
						}
					}
				}

				return statement;
			}
		}

		return null;
	}

	private void createInputExpressions(Element rootElement) {
		List<Element> inputElements = SlideXmlUtility.getInputValueElements(rootElement);
		for (Element inputElement : inputElements) {
			IndexedExpression inputExpression = createValueExpression(inputElement, SlideXmlConstants.INPUT_VALUE);
			if (inputExpression != null) {
				slideFascia.getInputs().put(inputExpression.getIndex(), inputExpression);
			}
		}
	}

	private void createOutputExpressions(Element rootElement) {
		List<Element> outputElements = SlideXmlUtility.getOutputValueElements(rootElement);
		for (Element outputElement : outputElements) {
			IndexedExpression outputValue = createValueExpression(outputElement, SlideXmlConstants.OUTPUT_VALUE);
			if (outputValue != null) {
				slideFascia.getOutputs().put(outputValue.getIndex(), outputValue);
			}
		}
	}

	private IndexedExpression createValueExpression(Element element, String prefix) {
		String indexString = element.getName().replace(prefix, "");
		
		if (indexString.isEmpty()) {
			indexString = "1";
		}
		
		try {
			int index = Integer.parseInt(indexString);
			Expression expression = createExpression(SlideXmlUtility.getExpressionElement(element));
			
			return new IndexedExpression(index, expression);
		} catch (NumberFormatException e) {}
		
		return null;
	}

	public void createSlideControls() {
		Document doc = slideFascia.getSlideXml();
		if (SlideXmlUtility.isValidSlideXml(doc)) {
			Element rootElement = doc.getRootElement(); 
			createActions(rootElement);
			ComponentFactory.createTimers(rootElement, slideFascia);

			createInputExpressions(rootElement);
			createOutputExpressions(rootElement);

			ComponentFactory.createWindows(rootElement,	slideFascia);
			ComponentFactory.createViewportComponent(rootElement, slideFascia);

			createVariables(rootElement);
		}
	}

	public Document createSlideData() {
		Document doc = SlideXmlFactory.createEventDataXml();
		Element root = doc.getRootElement();
		// add identification
		Element sourceElement = root.addElement(SlideXmlConstants.SOURCE);
		String id = slideFascia.getSlide().getId().toString();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.SLIDE);
		sourceElement.addAttribute(SlideXmlConstants.ID, id);
		
		// add fields data
		if (slideFascia.getFields().size() > 0) {
			writeFieldsData(root, slideFascia.getFields());
		}
		// add variables
		if (slideFascia.getVariables().size() > 0) {
			writeVariablesData(root, slideFascia.getVariables());
		}
		// add output values
		if (slideFascia.getOutputs().size() > 0) {
			writeOutputValues(root, slideFascia.getOutputs());
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	private SwitchStatement createSwitchStatement(Element element) {
		if (element != null
				&& element.getName().equals(SlideXmlConstants.SWITCH)) {
			Element expressionElement = SlideXmlUtility.getExpressionElement(element);
			List<Element> caseElements = SlideXmlUtility.getCaseElements(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(slideFascia, expression);

				for (Element caseElement : caseElements) {
					String caseValue = SlideXmlUtility.getValue(caseElement);
					List<Element> elements = caseElement.elements();
					if (elements != null) {
						for (Element evaluableElement : elements) {
							Evaluable evaluable = createEvaluable(evaluableElement);
							if (evaluable != null) {
								statement.addCaseEvaluable(caseValue, evaluable);
							}
						}
					}
				}

				return statement;
			}
		}

		return null;
	}

	private Variable<?> createVariable(Element element) {
		if (element.getName().equals(SlideXmlConstants.VARIABLE)) {
			String id = SlideXmlUtility.getId(element);
			String type = SlideXmlUtility.getType(element);
			String value = SlideXmlUtility.getValue(element);
			String values = SlideXmlUtility.getValues(element);
			Variable<?> variable = null;

			if (SlideXmlConstants.OBJECT.equalsIgnoreCase(type)) {
				variable = new Variable<Object>(id);
				Element reference = SlideXmlUtility.getReferenceSubElement(element);
				if (reference != null) {
					String referenceId = SlideXmlUtility.getId(reference);
					if (!Strings.isNullOrEmpty(referenceId)) {
						SlideComponent component = null;
						if (reference.getName().equals(SlideXmlConstants.COMPONENT)) {
							component = slideFascia.getComponent(referenceId);
						} else if (reference.getName().equals(SlideXmlConstants.TIMER)) {
							component = slideFascia.getTimer(referenceId);
						} else if (reference.getName().equals(SlideXmlConstants.WINDOW)) {
							component = slideFascia.getWindow(referenceId);
						}
						
						if (component != null) {
							variable.setRawValue(component);
						}
					}
				} else {
					Element instance = SlideXmlUtility.getInstanceSubElement(element);
					if (instance != null) {
						if (instance.getName().equals(SlideXmlConstants.CLASS)) {
							String className = SlideXmlUtility.getName(instance);
							if (!Strings.isNullOrEmpty(className)) {
								try {
									Class<?> clazz = Class.forName(className);
									Constructor<?> ctor = clazz.getConstructor(new Class[] {});
									Object object = ctor.newInstance(new Object[] {});
									if (object != null) {
										variable.setRawValue(object);
									}
								} catch(Throwable e) {
								
								}
							}
						}
					}
				}
			} else if (SlideXmlConstants.INTEGER.equalsIgnoreCase(type))
				variable = new Variable<Integer>(id, Strings.toInteger(value));
			else if (SlideXmlConstants.BOOLEAN.equalsIgnoreCase(type))
				variable = new Variable<Boolean>(id, Boolean.parseBoolean(value));
			else if (SlideXmlConstants.FLOAT.equalsIgnoreCase(type))
				variable = new Variable<Double>(id, Strings.toDouble(value));
			else if (SlideXmlConstants.STRING.equalsIgnoreCase(type))
				variable = new Variable<String>(id, value);
			
			else if (SlideXmlConstants.INTEGER_ARRAY.equalsIgnoreCase(type)) {
				variable = new Variable<Object>(id);
				ArrayList<Integer> array = new ArrayList<Integer>();
				Integer[] integers = Strings.toIntegerArray(values, StringConstants.STR_COMMA);
				if (integers != null) {
					for (Integer integer : integers) {
						if (integer != null) {
							array.add(integer);
						}
					}
				}
				variable.setRawValue(array);
			} else if (SlideXmlConstants.FLOAT_ARRAY.equalsIgnoreCase(type)) {
				variable = new Variable<Object>(id);
				ArrayList<Double> array = new ArrayList<Double>();
				Double[] doubles = Strings.toDoubleArray(values, StringConstants.STR_COMMA);
				if (doubles != null) {
					for (Double dbl : doubles) {
						if (dbl != null) {
							array.add(dbl);
						}
					}
				}
				variable.setRawValue(array);
			} else if (SlideXmlConstants.STRING_ARRAY.equalsIgnoreCase(type)) {
				variable = new Variable<Object>(id);
				ArrayList<String> array = new ArrayList<String>();
				String[] strings = Strings.toStringArray(values, StringConstants.STR_COMMA, StringConstants.STR_QUOTED_STRING_SPLIT_PATTERN);
				if (strings != null) {
					for (String string : strings) {
						if (string != null) {
							array.add(string);
						}
					}
				}
				variable.setRawValue(array);
			}


			return variable;
		} else
			return null;
	}

	private void createVariables(Element rootElement) {
		List<Element> variables = SlideXmlUtility
				.getVariablesElements(rootElement);
		for (Element variableElement : variables) {
			String id = SlideXmlUtility.getId(variableElement);
			if (!Strings.isNullOrEmpty(id)) {
				Variable<?> variable = createVariable(variableElement);
				if (variable != null)
					slideFascia.getVariables().put(variable.getName(), variable);
			}
		}
		// create and add Navigator object variable
		com.tilioteo.hypothesis.interfaces.Variable<Object> navigator = createNavigatorObject();
		slideFascia.getVariables().put(navigator.getName(), navigator);

	}

	private com.tilioteo.hypothesis.interfaces.Variable<Object> createNavigatorObject() {
		// TODO invent naming for system objects and mark navigator like a
		// system object
		Variable<Object> variable = new Variable<Object>(SlideXmlConstants.NAVIGATOR);
		Navigator navigator = new Navigator(slideFascia);
		variable.setRawValue(navigator);
		return variable;
	}

	public void addComponentDataVariable(AbstractComponentData<?> data) {
		com.tilioteo.hypothesis.interfaces.Variable<?> variable = slideFascia.getVariables().get(SlideXmlConstants.COMPONENT_DATA);
		if (null == variable) {
			variable = new Variable<Object>(SlideXmlConstants.COMPONENT_DATA);
			slideFascia.getVariables().put(SlideXmlConstants.COMPONENT_DATA, variable);
		}
		variable.setRawValue(data);
	}
	
	public void clearComponentDataVariable() {
		com.tilioteo.hypothesis.interfaces.Variable<?> variable = slideFascia.getVariables().get(SlideXmlConstants.COMPONENT_DATA);
		if (variable != null) {
			variable.setRawValue(null);
		}
	}

}
