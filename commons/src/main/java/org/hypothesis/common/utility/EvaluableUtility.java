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
				.filter(f -> StringUtils.isNotEmpty(DocumentUtility.getId(f).orElse(null)))
				.map(m -> createAction(m, evaluator).orElse(null)).filter(Objects::nonNull)
				.forEach(e -> evaluator.setAction(e.getId(), e));
	}

	private static Optional<Action> createAction(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).flatMap(DocumentUtility::getId)
				.flatMap(m -> createAction(element, m, evaluator));
	}

	public static Optional<Action> createAnonymousAction(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).flatMap(m -> createInnerAction(m, UUID.randomUUID().toString(), evaluator));
	}

	private static Optional<Action> createAction(Element element, String id, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.ACTION.equals(f.getName()))
				.flatMap(m -> createInnerAction(m, id, evaluator));
	}

	private static Optional<Action> createInnerAction(Element element, String id, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> StringUtils.isNotEmpty(id)).map(fm -> {
			org.hypothesis.evaluation.Action action = new org.hypothesis.evaluation.Action(evaluator, id);
			fm.children().stream().map(m -> createEvaluable(m, evaluator).orElse(null)).filter(Objects::nonNull)
					.forEach(action::add);
			return createActionOutputValues(action, fm);
		});
	}

	public static Optional<Evaluable> createEvaluable(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).map(m -> m.getName()).map(m -> {
			if (m.equals(DocumentConstants.EXPRESSION)) {
				return createExpression(element).orElse(null);
			} else if (m.equals(DocumentConstants.IF)) {
				return createIfStatement(element, evaluator).orElse(null);
			} else if (m.equals(DocumentConstants.WHILE)) {
				return createWhileStatement(element, evaluator).orElse(null);
			} else if (m.equals(DocumentConstants.SWITCH)) {
				return createSwitchStatement(element, evaluator).orElse(null);
			} else if (m.equals(DocumentConstants.CALL)) {
				return createCall(element, evaluator).orElse(null);
			}
			return null;
		});
	}

	public static Optional<Expression> createExpression(Element element) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.EXPRESSION.equals(f.getName())).flatMap(
				m -> DocumentUtility.getTrimmedText(m).map(ExpressionFactory::parseString).map(Expression::new));
	}

	private static Optional<IfStatement> createIfStatement(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.IF.equals(f.getName()))
				.flatMap(m -> createExpression(DocumentUtility.getExpressionElement(m).orElse(null))).map(m -> {
					IfStatement statement = new IfStatement(evaluator, m);
					for (int i = 0; i < 2; ++i) {
						List<Element> elements = i == 0
								? DocumentUtility.getTrueElement(element).map(mm -> mm.children()).orElse(null)
								: DocumentUtility.getFalseElement(element).map(mm -> mm.children()).orElse(null);

						if (elements != null) {
							final int index = i;
							elements.stream().map(mm -> createEvaluable(mm, evaluator).orElse(null))
									.filter(Objects::nonNull).forEach(e -> {
										if (index == 0)
											statement.addTrueEvaluable(e);
										else
											statement.addFalseEvaluable(e);
									});
						}
					}
					return statement;
				});
	}

	private static Optional<WhileStatement> createWhileStatement(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.WHILE.equals(f.getName()))
				.flatMap(m -> createExpression(DocumentUtility.getExpressionElement(m).orElse(null))).map(m -> {
					WhileStatement statement = new WhileStatement(evaluator, m);
					DocumentUtility.getLoopElement(element).ifPresent(
							el -> el.children().stream().map(mm -> createEvaluable(mm, evaluator).orElse(null))
									.filter(Objects::nonNull).forEach(statement::addEvaluable));
					return statement;
				});
	}

	private static Optional<SwitchStatement> createSwitchStatement(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.SWITCH.equals(f.getName()))
				.flatMap(m -> createExpression(DocumentUtility.getExpressionElement(m).orElse(null))).map(m -> {
					SwitchStatement statement = new SwitchStatement(evaluator, m);
					DocumentUtility.getCaseElements(element).forEach(e -> e.children().stream()
							.map(mm -> createEvaluable(mm, evaluator).orElse(null)).filter(Objects::nonNull)
							.forEach(i -> statement.addCaseEvaluable(DocumentUtility.getValue(e).orElse(null), i)));
					return statement;
				});
	}

	private static Optional<Call> createCall(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.CALL.equals(f.getName()))
				.flatMap(DocumentUtility::getAction).map(m -> new Call(evaluator, m));
	}

	private static Action createActionOutputValues(Action action, Element element) {
		DocumentUtility.findElementsByNameStarting(element, DocumentConstants.OUTPUT_VALUE).stream()
				.map(m -> createValueExpression(m, DocumentConstants.OUTPUT_VALUE).orElse(null))
				.filter(Objects::nonNull).forEach(e -> action.getOutputs().put(e.getIndex(), e));
		return action;
	}

	public static Optional<IndexedExpression> createValueExpression(Element element, String prefix) {
		return Optional.ofNullable(element).flatMap(m -> {
			String indexString = m.getName().replace(prefix, "");
			if (indexString.isEmpty()) {
				indexString = "1";
			}

			try {
				int index = Integer.parseInt(indexString);
				return DocumentUtility.getExpressionElement(m).flatMap(EvaluableUtility::createExpression)
						.map(mm -> new IndexedExpression(index, mm));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public static void createVariables(Element element, Evaluator evaluator, ReferenceCallback callback) {
		DocumentUtility.getVariablesElements(element).stream()
				.filter(f -> StringUtils.isNotBlank(DocumentUtility.getId(f).orElse(null)))
				.map(m -> createVariable(m, evaluator, callback)).filter(Objects::nonNull)
				.forEach(e -> evaluator.getVariables().put(e.getName(), e));
	}

	private static Variable<?> createVariable(Element element, Evaluator evaluator, ReferenceCallback callback) {
		return Optional.ofNullable(element).filter(f -> DocumentConstants.VARIABLE.equals(f.getName()))
				.map(m -> DocumentUtility.getId(m).orElse(null))
				.map(id -> DocumentUtility.getType(element).map(type -> {
					String value = DocumentUtility.getValue(element).orElse(null);
					String values = DocumentUtility.getValues(element).orElse(null);
					Variable<?> variable = null;

					if (DocumentConstants.OBJECT.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<Object>(id);
						final Variable<?> finalVariable = variable;
						Element reference = DocumentUtility.getReferenceSubElement(element).orElse(null);
						if (callback != null && reference != null) {
							DocumentUtility.getId(reference)
									.flatMap(m -> callback.getReference(reference.getName(), m, evaluator))
									.ifPresent(variable::setRawValue);
						} else {
							DocumentUtility.getInstanceSubElement(element)
									.filter(f -> DocumentConstants.CLASS.equals(f.getName()))
									.flatMap(DocumentUtility::getName).filter(StringUtils::isNotEmpty).ifPresent(e -> {
										try {
											Class<?> clazz = Class.forName(e);
											Constructor<?> ctor = clazz.getConstructor();
											Object object = ctor.newInstance();
											if (object != null) {
												finalVariable.setRawValue(object);
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									});
						}
					} else if (DocumentConstants.INTEGER.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id, ConversionUtility.getInteger(value));
					} else if (DocumentConstants.BOOLEAN.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id, ConversionUtility.getBoolean(value));
					} else if (DocumentConstants.FLOAT.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id, ConversionUtility.getDouble(value));
					} else if (DocumentConstants.STRING.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id, value);
					} else if (DocumentConstants.INTEGER_ARRAY.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id);
						variable.setRawValue(Arrays.stream(StringUtils.split(values, DocumentConstants.STR_COMMA))
								.map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new)));
					} else if (DocumentConstants.FLOAT_ARRAY.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id);
						variable.setRawValue(Arrays.stream(StringUtils.split(values, DocumentConstants.STR_COMMA))
								.map(Double::parseDouble).collect(Collectors.toCollection(ArrayList::new)));
					} else if (DocumentConstants.STRING_ARRAY.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id);
						variable.setRawValue(
								Arrays.stream(values.split(DocumentConstants.STR_QUOTED_STRING_SPLIT_PATTERN))
										.map(m -> StringUtils.strip(m, DocumentConstants.STR_QUOTE))
										.collect(Collectors.toCollection(ArrayList::new)));
					} else if (DocumentConstants.OBJECT_ARRAY.equalsIgnoreCase(type)) {
						variable = new org.hypothesis.evaluation.Variable<>(id);
						variable.setRawValue(new ArrayList<>());
					}
					return variable;
				}).orElse(null)).orElse(null);
	}

}
