/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hypothesis.evaluation.Call;
import org.hypothesis.evaluation.Expression;
import org.hypothesis.evaluation.IfStatement;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.evaluation.SwitchStatement;
import org.hypothesis.evaluation.WhileStatement;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.Evaluator;
import org.hypothesis.interfaces.ReferenceCallback;
import org.hypothesis.interfaces.Variable;

import com.tilioteo.common.Strings;
import com.tilioteo.expressions.ExpressionFactory;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class EvaluableUtility {

	public static void createActions(Element element, Evaluator evaluator) {
		List<Element> actions = DocumentUtility.getActionsElements(element);

		if (actions != null) {
			for (Element actionElement : actions) {
				String id = DocumentUtility.getId(actionElement);
				if (!Strings.isNullOrEmpty(id)) {
					Action action = createAction(actionElement, evaluator);
					if (action != null) {
						evaluator.setAction(id, action);
					}
				}
			}
		}
	}

	private static Action createAction(Element element, Evaluator evaluator) {
		if (element != null) {
			String id = DocumentUtility.getId(element);
			return createAction(element, id, evaluator);
		}

		return null;
	}

	public static Action createAnonymousAction(Element element, Evaluator evaluator) {
		if (element != null) {
			String id = UUID.randomUUID().toString();
			return createInnerAction(element, id, evaluator);
		}

		return null;
	}

	private static Action createAction(Element element, String id, Evaluator evaluator) {
		if (element.getName().equals(DocumentConstants.ACTION)) {
			return createInnerAction(element, id, evaluator);
		}

		return null;
	}

	private static Action createInnerAction(Element element, String id, Evaluator evaluator) {
		if (element != null && !Strings.isNullOrEmpty(id)) {
			org.hypothesis.evaluation.Action action = new org.hypothesis.evaluation.Action(evaluator, id);
			List<Element> elements = element.children();
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

			if (name.equals(DocumentConstants.EXPRESSION)) {
				return createExpression(element);
			} else if (name.equals(DocumentConstants.IF)) {
				return createIfStatement(element, evaluator);
			} else if (name.equals(DocumentConstants.WHILE)) {
				return createWhileStatement(element, evaluator);
			} else if (name.equals(DocumentConstants.SWITCH)) {
				return createSwitchStatement(element, evaluator);
			} else if (name.equals(DocumentConstants.CALL)) {
				return createCall(element, evaluator);
			}
		}

		return null;
	}

	public static Expression createExpression(Element element) {
		if (element != null && element.getName().equals(DocumentConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(DocumentUtility.getTrimmedText(element)));
		}

		return null;
	}

	private static IfStatement createIfStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.IF)) {
			Element expressionElement = DocumentUtility.getExpressionElement(element);
			Element trueElement = DocumentUtility.getTrueElement(element);
			Element falseElement = DocumentUtility.getFalseElement(element);
			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				IfStatement statement = new IfStatement(evaluator, expression);

				for (int i = 0; i < 2; ++i) {
					List<Element> elements = i == 0 ? trueElement != null ? trueElement.children() : null
							: falseElement != null ? falseElement.children() : null;
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

	private static WhileStatement createWhileStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.WHILE)) {
			Element expressionElement = DocumentUtility.getExpressionElement(element);
			Element loopElement = DocumentUtility.getLoopElement(element);
			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				WhileStatement statement = new WhileStatement(evaluator, expression);

				List<Element> elements = loopElement.children();
				if (elements != null) {
					for (Element evaluableElement : elements) {
						Evaluable evaluable = createEvaluable(evaluableElement, evaluator);
						if (evaluable != null) {
							statement.addEvaluable(evaluable);
						}
					}
				}

				return statement;
			}
		}

		return null;
	}

	private static SwitchStatement createSwitchStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.SWITCH)) {
			Element expressionElement = DocumentUtility.getExpressionElement(element);
			List<Element> caseElements = DocumentUtility.getCaseElements(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(evaluator, expression);

				for (Element caseElement : caseElements) {
					String caseValue = DocumentUtility.getValue(caseElement);
					List<Element> elements = caseElement.children();
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
		if (element != null && element.getName().equals(DocumentConstants.CALL)) {
			String actionId = DocumentUtility.getAction(element);
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Call(evaluator, actionId);
			}
		}

		return null;
	}

	private static void createActionOutputValues(Action action, Element element) {
		List<Element> outputElements = DocumentUtility.findElementsByNameStarting(element,
				DocumentConstants.OUTPUT_VALUE);

		if (outputElements != null) {
			for (Element outputElement : outputElements) {
				IndexedExpression outputValue = createValueExpression(outputElement, DocumentConstants.OUTPUT_VALUE);
				if (outputValue != null) {
					action.getOutputs().put(outputValue.getIndex(), outputValue);
				}
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
			Expression expression = createExpression(DocumentUtility.getExpressionElement(element));

			if (expression != null) {
				return new IndexedExpression(index, expression);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void createVariables(Element element, Evaluator evaluator, ReferenceCallback callback) {
		List<Element> variables = DocumentUtility.getVariablesElements(element);

		if (variables != null) {
			for (Element variableElement : variables) {
				String id = DocumentUtility.getId(variableElement);
				if (!Strings.isNullOrEmpty(id)) {
					Variable<?> variable = createVariable(variableElement, evaluator, callback);
					if (variable != null)
						evaluator.getVariables().put(variable.getName(), variable);
				}
			}
		}
	}

	private static Variable<?> createVariable(Element element, Evaluator evaluator, ReferenceCallback callback) {
		if (element.getName().equals(DocumentConstants.VARIABLE)) {
			String id = DocumentUtility.getId(element);
			String type = DocumentUtility.getType(element);
			String value = DocumentUtility.getValue(element);
			String values = DocumentUtility.getValues(element);
			Variable<?> variable = null;

			if (DocumentConstants.OBJECT.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<Object>(id);
				Element reference = DocumentUtility.getReferenceSubElement(element);
				if (callback != null && reference != null) {
					String referenceId = DocumentUtility.getId(reference);
					String referenceName = reference.getName();

					Object referencedObject = callback.getReference(referenceName, referenceId, evaluator);
					if (referencedObject != null) {
						variable.setRawValue(referencedObject);
					}
				} else {
					Element instance = DocumentUtility.getInstanceSubElement(element);
					if (instance != null) {
						if (instance.getName().equals(DocumentConstants.CLASS)) {
							String className = DocumentUtility.getName(instance);
							if (!Strings.isNullOrEmpty(className)) {
								try {
									Class<?> clazz = Class.forName(className);
									Constructor<?> ctor = clazz.getConstructor();
									Object object = ctor.newInstance();
									if (object != null) {
										variable.setRawValue(object);
									}
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else if (DocumentConstants.INTEGER.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<Integer>(id, Strings.toInteger(value));
			else if (DocumentConstants.BOOLEAN.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<Boolean>(id, Boolean.parseBoolean(value));
			else if (DocumentConstants.FLOAT.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<Double>(id, Strings.toDouble(value));
			else if (DocumentConstants.STRING.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<String>(id, value);

			else if (DocumentConstants.INTEGER_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<Integer> array = new ArrayList<>();
				Integer[] integers = Strings.toIntegerArray(values, DocumentConstants.STR_COMMA);
				if (integers != null) {
					for (Integer integer : integers) {
						if (integer != null) {
							array.add(integer);
						}
					}
				}
				variable.setRawValue(array);
			} else if (DocumentConstants.FLOAT_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<Double> array = new ArrayList<>();
				Double[] doubles = Strings.toDoubleArray(values, DocumentConstants.STR_COMMA);
				if (doubles != null) {
					for (Double dbl : doubles) {
						if (dbl != null) {
							array.add(dbl);
						}
					}
				}
				variable.setRawValue(array);
			} else if (DocumentConstants.STRING_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<String> array = new ArrayList<>();
				String[] strings = Strings.toStringArray(values, DocumentConstants.STR_COMMA,
						DocumentConstants.STR_QUOTED_STRING_SPLIT_PATTERN);
				if (strings != null) {
					for (String string : strings) {
						if (string != null) {
							array.add(string);
						}
					}
				}
				variable.setRawValue(array);
			} else if (DocumentConstants.OBJECT_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<Object>(id);
				ArrayList<Object> array = new ArrayList<>();
				variable.setRawValue(array);
			}

			return variable;
		} else
			return null;
	}

}
