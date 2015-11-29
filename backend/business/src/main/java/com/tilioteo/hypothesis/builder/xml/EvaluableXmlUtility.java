/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dom4j.Element;

import com.tilioteo.common.Strings;
import com.tilioteo.expressions.ExpressionFactory;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.evaluation.AbstractBaseAction;
import com.tilioteo.hypothesis.evaluation.Action;
import com.tilioteo.hypothesis.evaluation.Call;
import com.tilioteo.hypothesis.evaluation.Expression;
import com.tilioteo.hypothesis.evaluation.IfStatement;
import com.tilioteo.hypothesis.evaluation.IndexedExpression;
import com.tilioteo.hypothesis.evaluation.SwitchStatement;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.Evaluator;
import com.tilioteo.hypothesis.interfaces.Variable;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
public class EvaluableXmlUtility {

	public static void createActions(Element element, Evaluator evaluator) {
		List<Element> actions = XmlDocumentUtility.getActionsElements(element);
		for (Element actionElement : actions) {
			String id = XmlDocumentUtility.getId(actionElement);
			if (!Strings.isNullOrEmpty(id)) {
				AbstractBaseAction action = createAction(actionElement, evaluator);
				if (action != null) {
					evaluator.setAction(id, action);
				}
			}
		}
	}

	private static AbstractBaseAction createAction(Element element, Evaluator evaluator) {
		if (element != null) {
			String id = XmlDocumentUtility.getId(element);
			return createAction(element, id, evaluator);
		}

		return null;
	}

	public static AbstractBaseAction createAnonymousAction(Element element, Evaluator evaluator) {
		if (element != null) {
			String id = UUID.randomUUID().toString();
			return createInnerAction(element, id, evaluator);
		}

		return null;
	}

	private static AbstractBaseAction createAction(Element element, String id, Evaluator evaluator) {
		if (element.getName().equals(BuilderConstants.ACTION)) {
			return createInnerAction(element, id, evaluator);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static AbstractBaseAction createInnerAction(Element element, String id, Evaluator evaluator) {
		if (element != null && !Strings.isNullOrEmpty(id)) {
			Action action = new Action(evaluator, id);
			List<Element> elements = element.elements();
			for (Element evaluableElement : elements) {
				Evaluable evaluable = createEvaluable(evaluableElement, evaluator);
				if (evaluable != null)
					action.add(evaluable);
			}
			createActionOutputValues(action, element);

			return action;
		}

		return null;
	}

	public static Evaluable createEvaluable(Element element, Evaluator evaluator) {
		if (element != null) {
			String name = element.getName();

			if (name.equals(BuilderConstants.EXPRESSION)) {
				return createExpression(element);
			} else if (name.equals(BuilderConstants.IF)) {
				return createIfStatement(element, evaluator);
			} else if (name.equals(BuilderConstants.SWITCH)) {
				return createSwitchStatement(element, evaluator);
			} else if (name.equals(BuilderConstants.CALL)) {
				return createCall(element, evaluator);
			}
		}

		return null;
	}

	private static Expression createExpression(Element element) {
		if (element != null && element.getName().equals(BuilderConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(element.getTextTrim()));
		}

		return null;
	}

	private static IfStatement createIfStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(BuilderConstants.IF)) {
			Element expressionElement = XmlDocumentUtility.getExpressionElement(element);
			Element trueElement = XmlDocumentUtility.getTrueElement(element);
			Element falseElement = XmlDocumentUtility.getFalseElement(element);
			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				IfStatement statement = new IfStatement(evaluator, expression);

				for (int i = 0; i < 2; ++i) {
					@SuppressWarnings("unchecked")
					List<Element> elements = i == 0 ? trueElement != null ? trueElement.elements() : null
							: falseElement != null ? falseElement.elements() : null;
					if (elements != null) {
						for (Element evaluableElement : elements) {
							Evaluable evaluable = createEvaluable(evaluableElement, evaluator);
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
	private static SwitchStatement createSwitchStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(BuilderConstants.SWITCH)) {
			Element expressionElement = XmlDocumentUtility.getExpressionElement(element);
			List<Element> caseElements = XmlDocumentUtility.getCaseElements(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(evaluator, expression);

				for (Element caseElement : caseElements) {
					String caseValue = XmlDocumentUtility.getValue(caseElement);
					List<Element> elements = caseElement.elements();
					if (elements != null) {
						for (Element evaluableElement : elements) {
							Evaluable evaluable = createEvaluable(evaluableElement, evaluator);
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

	private static Call createCall(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(BuilderConstants.CALL)) {
			String actionId = XmlDocumentUtility.getAction(element);
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Call(evaluator, actionId);
			}
		}

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void createActionOutputValues(Action action, Element element) {
		List<Element> outputElements = (List<Element>) (List) XmlUtility.findNodesByNameStarting(element,
				BuilderConstants.OUTPUT_VALUE);
		for (Element outputElement : outputElements) {
			IndexedExpression outputValue = createValueExpression(outputElement, BuilderConstants.OUTPUT_VALUE);
			if (outputValue != null) {
				action.getOutputs().put(outputValue.getIndex(), outputValue);
			}
		}
	}

	public static IndexedExpression createValueExpression(Element element, String prefix) {
		String indexString = element.getName().replace(prefix, "");

		if (indexString.isEmpty()) {
			indexString = "1";
		}

		try {
			int index = Integer.parseInt(indexString);
			Expression expression = createExpression(XmlDocumentUtility.getExpressionElement(element));

			return new IndexedExpression(index, expression);
		} catch (NumberFormatException e) {
		}

		return null;
	}

	public static void createVariables(Element element, Evaluator evaluator, ReferenceCallback callback) {
		List<Element> variables = XmlDocumentUtility.getVariablesElements(element);
		for (Element variableElement : variables) {
			String id = XmlDocumentUtility.getId(variableElement);
			if (!Strings.isNullOrEmpty(id)) {
				Variable<?> variable = createVariable(variableElement, evaluator, callback);
				if (variable != null)
					evaluator.getVariables().put(variable.getName(), variable);
			}
		}
	}

	private static Variable<?> createVariable(Element element, Evaluator evaluator, ReferenceCallback callback) {
		if (element.getName().equals(BuilderConstants.VARIABLE)) {
			String id = XmlDocumentUtility.getId(element);
			String type = XmlDocumentUtility.getType(element);
			String value = XmlDocumentUtility.getValue(element);
			String values = XmlDocumentUtility.getValues(element);
			Variable<?> variable = null;

			if (BuilderConstants.OBJECT.equalsIgnoreCase(type)) {
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Object>(id);
				Element reference = XmlDocumentUtility.getReferenceSubElement(element);
				if (callback != null && reference != null) {
					String referenceId = XmlDocumentUtility.getId(reference);
					String referenceName = reference.getName();

					Object referencedObject = callback.getReference(referenceName, referenceId, evaluator);
					if (referencedObject != null) {
						variable.setRawValue(referencedObject);
					}
				} else {
					Element instance = XmlDocumentUtility.getInstanceSubElement(element);
					if (instance != null) {
						if (instance.getName().equals(BuilderConstants.CLASS)) {
							String className = XmlDocumentUtility.getName(instance);
							if (!Strings.isNullOrEmpty(className)) {
								try {
									Class<?> clazz = Class.forName(className);
									Constructor<?> ctor = clazz.getConstructor(new Class[] {});
									Object object = ctor.newInstance(new Object[] {});
									if (object != null) {
										variable.setRawValue(object);
									}
								} catch (Throwable e) {

								}
							}
						}
					}
				}
			} else if (BuilderConstants.INTEGER.equalsIgnoreCase(type))
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Integer>(id, Strings.toInteger(value));
			else if (BuilderConstants.BOOLEAN.equalsIgnoreCase(type))
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Boolean>(id, Boolean.parseBoolean(value));
			else if (BuilderConstants.FLOAT.equalsIgnoreCase(type))
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Double>(id, Strings.toDouble(value));
			else if (BuilderConstants.STRING.equalsIgnoreCase(type))
				variable = new com.tilioteo.hypothesis.evaluation.Variable<String>(id, value);

			else if (BuilderConstants.INTEGER_ARRAY.equalsIgnoreCase(type)) {
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<Integer> array = new ArrayList<Integer>();
				Integer[] integers = Strings.toIntegerArray(values, BuilderConstants.STR_COMMA);
				if (integers != null) {
					for (Integer integer : integers) {
						if (integer != null) {
							array.add(integer);
						}
					}
				}
				variable.setRawValue(array);
			} else if (BuilderConstants.FLOAT_ARRAY.equalsIgnoreCase(type)) {
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<Double> array = new ArrayList<Double>();
				Double[] doubles = Strings.toDoubleArray(values, BuilderConstants.STR_COMMA);
				if (doubles != null) {
					for (Double dbl : doubles) {
						if (dbl != null) {
							array.add(dbl);
						}
					}
				}
				variable.setRawValue(array);
			} else if (BuilderConstants.STRING_ARRAY.equalsIgnoreCase(type)) {
				variable = new com.tilioteo.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<String> array = new ArrayList<String>();
				String[] strings = Strings.toStringArray(values, BuilderConstants.STR_COMMA,
						BuilderConstants.STR_QUOTED_STRING_SPLIT_PATTERN);
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

}
