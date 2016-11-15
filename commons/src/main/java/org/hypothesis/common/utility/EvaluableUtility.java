/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
public final class EvaluableUtility {

	private EvaluableUtility() {
	}

	public static void createActions(Element element, Evaluator evaluator) {
		DocumentUtility.getActionsElements(element).stream()
				.filter(f -> StringUtils.isNotEmpty(DocumentUtility.getId(f))).map(m -> createAction(m, evaluator))
				.filter(Objects::nonNull).forEach(e -> evaluator.setAction(e.getId(), e));
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
		if (element != null && StringUtils.isNotEmpty(id)) {
			org.hypothesis.evaluation.Action action = new org.hypothesis.evaluation.Action(evaluator, id);
			element.children().stream().map(m -> createEvaluable(m, evaluator)).filter(Objects::nonNull)
					.forEach(action::add);
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

	public static Optional<Expression> createExpression(Optional<Element> element) {
		return element.filter(f -> DocumentConstants.EXPRESSION.equals(f.getName()))
				.map(m -> new Expression(ExpressionFactory.parseString(DocumentUtility.getTrimmedText(m))));
	}

	private static IfStatement createIfStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.IF)) {
			Element trueElement = DocumentUtility.getTrueElement(element);
			Element falseElement = DocumentUtility.getFalseElement(element);
			Expression expression = createExpression(DocumentUtility.getExpressionElement(element));

			if (expression != null) {
				IfStatement statement = new IfStatement(evaluator, expression);

				for (int i = 0; i < 2; ++i) {
					List<Element> elements = i == 0 ? trueElement != null ? trueElement.children() : null
							: falseElement != null ? falseElement.children() : null;

					if (elements != null) {
						final int index = i;
						elements.stream().map(m -> createEvaluable(m, evaluator)).filter(Objects::nonNull)
								.forEach(e -> {
									if (index == 0)
										statement.addTrueEvaluable(e);
									else
										statement.addFalseEvaluable(e);
								});
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
				loopElement.children().stream().map(m -> createEvaluable(m, evaluator)).filter(Objects::nonNull)
						.forEach(statement::addEvaluable);

				return statement;
			}
		}

		return null;
	}

	private static SwitchStatement createSwitchStatement(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.SWITCH)) {
			Element expressionElement = DocumentUtility.getExpressionElement(element);

			Expression expression = createExpression(expressionElement);

			if (expression != null) {
				SwitchStatement statement = new SwitchStatement(evaluator, expression);
				DocumentUtility.getCaseElements(element)
						.forEach(e -> e.children().stream().map(m -> createEvaluable(m, evaluator))
								.filter(Objects::nonNull)
								.forEach(i -> statement.addCaseEvaluable(DocumentUtility.getValue(e), i)));

				return statement;
			}
		}

		return null;
	}

	private static Call createCall(Element element, Evaluator evaluator) {
		if (element != null && element.getName().equals(DocumentConstants.CALL)) {
			String actionId = DocumentUtility.getAction(element);
			if (StringUtils.isNotEmpty(actionId)) {
				return new Call(evaluator, actionId);
			}
		}

		return null;
	}

	private static void createActionOutputValues(Action action, Element element) {
		List<Element> outputElements = DocumentUtility.findElementsByNameStarting(element,
				DocumentConstants.OUTPUT_VALUE);

		outputElements.stream().map(m -> createValueExpression(m, DocumentConstants.OUTPUT_VALUE))
				.filter(Objects::nonNull).forEach(e -> action.getOutputs().put(e.getIndex(), e));
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
		DocumentUtility.getVariablesElements(element).stream()
				.filter(f -> StringUtils.isNotBlank(DocumentUtility.getId(f)))
				.map(m -> createVariable(m, evaluator, callback)).filter(Objects::nonNull)
				.forEach(e -> evaluator.getVariables().put(e.getName(), e));
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
							if (StringUtils.isNotEmpty(className)) {
								try {
									Class<?> clazz = Class.forName(className);
									Constructor<?> ctor = clazz.getConstructor();
									Object object = ctor.newInstance();
									if (object != null) {
										variable.setRawValue(object);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else if (DocumentConstants.INTEGER.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<>(id, Strings.toInteger(value));
			else if (DocumentConstants.BOOLEAN.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<>(id, Boolean.parseBoolean(value));
			else if (DocumentConstants.FLOAT.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<>(id, Strings.toDouble(value));
			else if (DocumentConstants.STRING.equalsIgnoreCase(type))
				variable = new org.hypothesis.evaluation.Variable<>(id, value);

			else if (DocumentConstants.INTEGER_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<>(id);
				variable.setRawValue(Arrays.stream(StringUtils.split(values, DocumentConstants.STR_COMMA))
						.map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new)));
			} else if (DocumentConstants.FLOAT_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<>(id);
				variable.setRawValue(Arrays.stream(StringUtils.split(values, DocumentConstants.STR_COMMA))
						.map(Double::parseDouble).collect(Collectors.toCollection(ArrayList::new)));
			} else if (DocumentConstants.STRING_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<>(id);
				variable.setRawValue(Arrays.stream(values.split(DocumentConstants.STR_QUOTED_STRING_SPLIT_PATTERN))
						.map(m -> StringUtils.strip(m, DocumentConstants.STR_QUOTE))
						.collect(Collectors.toCollection(ArrayList::new)));
			} else if (DocumentConstants.OBJECT_ARRAY.equalsIgnoreCase(type)) {
				variable = new org.hypothesis.evaluation.Variable<>(id);
				variable.setRawValue(new ArrayList<>());
			}

			return variable;
		} else
			return null;
	}

	// private

}
