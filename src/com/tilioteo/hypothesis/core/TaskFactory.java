/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.common.Strings;
import com.tilioteo.expressions.ExpressionFactory;
import com.tilioteo.hypothesis.common.StringConstants;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.dom.TaskXmlUtility;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Action;
import com.tilioteo.hypothesis.processing.Call;
import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.IfStatement;
import com.tilioteo.hypothesis.processing.SwitchStatement;
import com.tilioteo.hypothesis.processing.Variable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TaskFactory implements Serializable {

	private static HashMap<TaskManager, TaskFactory> instances = new HashMap<TaskManager, TaskFactory>();
	
	public static TaskFactory getInstance(TaskManager taskManager) {
		TaskFactory taskFactory = instances.get(taskManager);
		
		if (null == taskFactory) {
			taskFactory = new TaskFactory(taskManager);
			instances.put(taskManager, taskFactory);
		}
		return taskFactory;
	}
	
	public static void remove(TaskManager taskManager) {
		instances.remove(taskManager);
	}

	private TaskManager taskManager = null;

	private TaskFactory(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void createTaskControls() {
		if (taskManager != null) {
			Document doc = taskManager.getTaskXml();
			if (TaskXmlUtility.isValidTaskXml(doc)) {
				Element rootElement = doc.getRootElement(); 
				createActions(rootElement);
				createVariables(rootElement);
				createNodes(rootElement);
			}
		}
	}

	private void createActions(Element rootElement) {
		List<Element> actions = TaskXmlUtility.getActionsElements(rootElement);
		for (Element actionElement : actions) {
			String id = SlideXmlUtility.getId(actionElement);
			if (!Strings.isNullOrEmpty(id)) {
				final AbstractBaseAction action = createAction(actionElement);
				if (action != null) {
					taskManager.setAction(id, action);
				}
			}
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

	@SuppressWarnings("unchecked")
	private AbstractBaseAction createInnerAction(Element element, String id) {
		if (element != null && !Strings.isNullOrEmpty(id)) {
			Action action = new Action(taskManager, id);
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

	private void createVariables(Element rootElement) {
		List<Element> variables = TaskXmlUtility.getVariablesElements(rootElement);
		for (Element variableElement : variables) {
			String id = SlideXmlUtility.getId(variableElement);
			if (!Strings.isNullOrEmpty(id)) {
				Variable<?> variable = createVariable(variableElement);
				if (variable != null)
					taskManager.getVariables().put(variable.getName(), variable);
			}
		}
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

	private void createNodes(Element rootElement) {
		List<Element> nodes = TaskXmlUtility.getNodesElements(rootElement);
		for (Element nodeElement : nodes) {
			Node node = createNode(nodeElement);
			if (node != null)
				taskManager.addNode(node.getSlideId(), node);
		}
	}

	@SuppressWarnings("unchecked")
	private Node createNode(Element element) {
		String idString = TaskXmlUtility.getSlideId(element);
		if (!idString.isEmpty()) {
			try {
				long slideId = Long.parseLong(idString);
			
				Node node = new Node(taskManager, slideId);
				Element evaluateElement = TaskXmlUtility.getEvaluateElement(element);
				if (evaluateElement != null) {
					List<Element> evaluables = evaluateElement.elements();
					for (Element evaluableElement : evaluables) {
						Evaluable evaluable = createEvaluable(evaluableElement);
						if (evaluable != null)
							node.add(evaluable);
					}
				}
				return node;
			} catch(Throwable e) {}
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
				IfStatement statement = new IfStatement(taskManager, expression);

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

	@SuppressWarnings("unchecked")
	private SwitchStatement createSwitchStatement(Element element) {
		if (element != null
				&& element.getName().equals(SlideXmlConstants.SWITCH)) {
			Element expressionElement = SlideXmlUtility.getExpressionElement(element);
			List<Element> caseElements = SlideXmlUtility.getCaseElements(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(taskManager, expression);

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

	private Call createCall(Element element) {
		if (element != null && element.getName().equals(SlideXmlConstants.CALL)) {
			String actionId = SlideXmlUtility.getAction(element);
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Call(taskManager, actionId);
			}
		}
		return null;
	}

	public com.tilioteo.hypothesis.interfaces.Variable<Object> createNavigatorObject(Node node) {
		// TODO invent naming for system objects and mark navigator like a
		// system object
		Variable<Object> variable = new Variable<Object>(SlideXmlConstants.NAVIGATOR,
				new TaskNavigator(node));
		return variable;
	}

}
