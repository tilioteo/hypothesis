/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.evaluable.ExpressionFactory;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.ActionEvent;
import com.tilioteo.hypothesis.event.ButtonData;
import com.tilioteo.hypothesis.event.ButtonPanelData;
import com.tilioteo.hypothesis.event.ImageData;
import com.tilioteo.hypothesis.event.RadioPanelData;
import com.tilioteo.hypothesis.event.TimerData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Action;
import com.tilioteo.hypothesis.processing.CallAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.Evaluable;
import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.IfStatement;
import com.tilioteo.hypothesis.processing.SwitchStatement;
import com.tilioteo.hypothesis.processing.Variable;
import com.tilioteo.hypothesis.ui.Button;
import com.tilioteo.hypothesis.ui.ButtonPanel;
import com.tilioteo.hypothesis.ui.ComponentFactory;
import com.tilioteo.hypothesis.ui.RadioButton;
import com.tilioteo.hypothesis.ui.RadioPanel;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Field;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideFactory {

	private static SlideFactory instance = null;

	public static SlideFactory getInstatnce() {
		if (instance == null)
			instance = new SlideFactory();

		return instance;
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
		ButtonPanel sender = buttonPanelData.getSender();
		Button button = buttonPanelData.getButton();
		int index = sender.getChildIndex(button) + 1;
		if (index > 0) {
			Element selectedElement = sourceElement.addElement(SlideXmlConstants.SELECTED);
			selectedElement.addAttribute(SlideXmlConstants.INDEX, String.format("%d", index));
			selectedElement.addText(button.getCaption());
		}
	}

	public static void writeActionData(Document doc, ActionEvent actionEvent) {
		Element root = doc.getRootElement();
		Element sourceElement = root.addElement(SlideXmlConstants.SOURCE);
		writeSourceData(sourceElement, actionEvent);
	}

	public static void writeComponentData(Document doc, AbstractComponentEvent<?> componentEvent) {
		Element root = doc.getRootElement();
		Element sourceElement = root.addElement(SlideXmlConstants.SOURCE);
		writeSourceData(sourceElement, componentEvent);
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

	public static void writeRadioPanelData(Element sourceElement, RadioPanelData radioPanelData) {
		String id = radioPanelData.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.RADIO_PANEL);
		if (id != null) {
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		}
		RadioPanel sender = radioPanelData.getSender();
		RadioButton radioButton = radioPanelData.getRadioButton();
		int index = sender.getChildIndex(radioButton) + 1;
		if (index > 0) {
			Element selectedElement = sourceElement.addElement(SlideXmlConstants.SELECTED);
			selectedElement.addAttribute(SlideXmlConstants.INDEX, String.format("%d", index));
			selectedElement.addText(radioButton.getCaption());
		}
	}

	private static void writeSourceData(Element sourceElement, ActionEvent actionEvent) {
		AbstractBaseAction action = actionEvent.getAction();
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

	private SlideManager slideManager = null;

	private SlideFactory() {
		super();
	}

	private void addFieldsToElement(List<Object> fields, Element element) {
		Element fieldsElement = element.addElement(SlideXmlConstants.FIELDS);
		for (Object field : fields) {
			if (field instanceof Field && field instanceof XmlDataWriter)
				addFieldToElement((XmlDataWriter) field, fieldsElement);
		}
	}

	private void addFieldToElement(XmlDataWriter field, Element element) {
		Element fieldElement = element.addElement(SlideXmlConstants.FIELD);
		field.writeDataToElement(fieldElement);
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

	private void createActions(Element rootElement, final SlideManager slideManager) {
		List<Element> actions = SlideXmlUtility.getActionsElements(rootElement);
		for (Element actionElement : actions) {
			String id = SlideXmlUtility.getId(actionElement);
			if (!Strings.isNullOrEmpty(id)) {
				final AbstractBaseAction action = createAction(actionElement);
				if (action != null) {
					action.setExecuteCommand(new Command() {
						@Override
						public void execute() {
							slideManager.getEventManager().fireEvent(new ActionEvent(action));
						}
					});
					slideManager.getActions().put(id, action);
				}
			}
		}
	}

	public AbstractBaseAction createAnonymousAction(Element element) {
		if (element != null) {
			String id = UUID.randomUUID().toString();
			AbstractBaseAction action = createInnerAction(element, id);
			if (action != null)
				slideManager.getActions().put(id, action);
			return action;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private AbstractBaseAction createInnerAction(Element element, String id) {
		if (element != null && !Strings.isNullOrEmpty(id)) {
			Action action = new Action(slideManager, id);
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

	private CallAction createCallAction(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.CALL)) {
			String actionId = SlideXmlUtility.getAction(element);
			if (!Strings.isNullOrEmpty(actionId)) {
				return new CallAction(slideManager, actionId);
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
				return createCallAction(element);
			}
		}
		return null;
	}

	private Expression createExpression(Element element) {
		if (element != null
				&& element.getName().equals(SlideXmlConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(element
					.getTextTrim()));
		}
		return null;
	}

	private IfStatement createIfStatement(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.IF)) {
			Element expressionElement = SlideXmlUtility
					.getExpressionElement(element);
			Element trueElement = SlideXmlUtility.getTrueElement(element);
			Element falseElement = SlideXmlUtility.getFalseElement(element);
			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				IfStatement statement = new IfStatement(slideManager,
						expression);

				for (int i = 0; i < 2; ++i) {
					@SuppressWarnings("unchecked")
					List<Element> elements = i == 0 ? trueElement != null ? trueElement
							.elements() : null
							: falseElement != null ? falseElement.elements()
									: null;
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

	private void createInputValue(Element rootElement) {
		Element inputElement = SlideXmlUtility
				.getInputValueElement(rootElement);
		Expression expression = createExpression(SlideXmlUtility
				.getExpressionElement(inputElement));
		slideManager.setInputExpression(expression);
	}

	private void createOutputValue(Element rootElement) {
		Element outputElement = SlideXmlUtility
				.getOutputValueElement(rootElement);
		Expression expression = createExpression(SlideXmlUtility
				.getExpressionElement(outputElement));
		slideManager.setOutputExpression(expression);
	}

	public void createSlideControls(SlideManager slideManager) {
		this.slideManager = slideManager;

		if (slideManager != null) {
			Document doc = slideManager.getSlideXml();

			if (SlideXmlUtility.isValidSlideXml(doc)) {
				Element rootElement = doc.getRootElement(); 
				createActions(rootElement, slideManager);
				ComponentFactory.createTimers(rootElement, slideManager);

				createInputValue(rootElement);
				createOutputValue(rootElement);

				ComponentFactory.createWindows(rootElement,	slideManager);
				ComponentFactory.createViewportComponent(rootElement, slideManager);

				createVariables(rootElement, slideManager);
			}
		}
	}

	public Document createSlideData(SlideManager slideManager) {
		Document doc = SlideXmlFactory.createSlideDataXml();
		Element root = doc.getRootElement();
		// add fields data
		if (slideManager.getFields().size() > 0) {
			addFieldsToElement(slideManager.getFields(), root);
		}
		return doc;
	}

	public Document createSlideOutput(SlideManager slideManager) {
		Document doc = SlideXmlFactory.createSlideOutputXml();
		Element root = doc.getRootElement();
		// add output value
		Element valueElement = root.addElement(SlideXmlConstants.VALUE);
		if (slideManager.getOutputValue() != null) {
			writeOutputValue(valueElement, slideManager.getOutputValue());
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	private SwitchStatement createSwitchStatement(Element element) {
		if (element != null
				&& element.getName().equals(SlideXmlConstants.SWITCH)) {
			Element expressionElement = SlideXmlUtility
					.getExpressionElement(element);
			List<Element> caseElements = SlideXmlUtility
					.getCaseElements(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(slideManager,
						expression);

				for (Element caseElement : caseElements) {
					String caseValue = SlideXmlUtility.getValue(caseElement);
					List<Element> elements = caseElement.elements();
					if (elements != null) {
						for (Element evaluableElement : elements) {
							Evaluable evaluable = createEvaluable(evaluableElement);
							if (evaluable != null) {
								statement
										.addCaseEvaluable(caseValue, evaluable);
							}
						}
					}
				}

				return statement;
			}
		}

		return null;
	}

	private Variable<?> createVariable(Element element,
			SlideManager slideManager) {
		if (element.getName().equals(SlideXmlConstants.VARIABLE)) {
			String id = SlideXmlUtility.getId(element);
			String type = SlideXmlUtility.getType(element);
			String value = SlideXmlUtility.getValue(element);
			Variable<?> variable = null;

			if (SlideXmlConstants.OBJECT.equalsIgnoreCase(type)) {
				variable = new Variable<Object>(id);
				Element reference = SlideXmlUtility.getReferenceSubElement(element);
				if (reference != null) {
					String referenceId = SlideXmlUtility.getId(reference);
					if (!Strings.isNullOrEmpty(referenceId)) {
						SlideComponent component = null;
						if (reference.getName().equals(SlideXmlConstants.COMPONENT)) {
							component = slideManager.getComponent(referenceId);
						} else if (reference.getName().equals(SlideXmlConstants.TIMER)) {
							component = slideManager.getTimer(referenceId);
						} else if (reference.getName().equals(SlideXmlConstants.WINDOW)) {
							component = slideManager.getWindow(referenceId);
						}
						
						if (component != null) {
							variable.setRawValue(component);
						}
					}
				}
			} else if (SlideXmlConstants.INTEGER.equalsIgnoreCase(type))
				variable = new Variable<Integer>(id, Integer.parseInt(value));
			else if (SlideXmlConstants.BOOLEAN.equalsIgnoreCase(type))
				variable = new Variable<Boolean>(id,
						Boolean.parseBoolean(value));
			else if (SlideXmlConstants.FLOAT.equalsIgnoreCase(type))
				variable = new Variable<Double>(id, Double.parseDouble(value));

			return variable;
		} else
			return null;
	}

	private void createVariables(Element rootElement, SlideManager slideManager) {
		List<Element> variables = SlideXmlUtility
				.getVariablesElements(rootElement);
		for (Element variableElement : variables) {
			String id = SlideXmlUtility.getId(variableElement);
			if (!Strings.isNullOrEmpty(id)) {
				Variable<?> variable = createVariable(variableElement,
						slideManager);
				if (variable != null)
					slideManager.getVariables().put(variable);
			}
		}
		// create and add Navigator object variable
		slideManager.getVariables().put(createNavigatorObject(slideManager));

	}

	private Variable<Object> createNavigatorObject(SlideManager slideManager) {
		// TODO invent naming for system objects and mark navigator like a
		// system object
		Variable<Object> variable = new Variable<Object>("Navigator");
		Navigator navigator = new Navigator(slideManager);
		variable.setRawValue(navigator);
		return variable;
	}

	private void writeOutputValue(Element element, Object value) {
		if (value instanceof Double) {
			element.addAttribute(SlideXmlConstants.TYPE,
					SlideXmlConstants.FLOAT);
			// use Locale.ROOT for locale neutral formating of decimals
			element.addText(String.format(Locale.ROOT, "%g",
					((Double) value).doubleValue()));
		} else if (value instanceof Integer) {
			element.addAttribute(SlideXmlConstants.TYPE,
					SlideXmlConstants.INTEGER);
			element.addText(((Integer) value).toString());
		} else if (value instanceof Boolean) {
			element.addAttribute(SlideXmlConstants.TYPE,
					SlideXmlConstants.BOOLEAN);
			element.addText(((Boolean) value).toString());
		} else {
			element.addAttribute(SlideXmlConstants.TYPE,
					SlideXmlConstants.OBJECT);
			// TODO serialize object type values
		}

	}

}
