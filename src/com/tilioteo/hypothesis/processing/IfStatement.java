/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.HasVariables;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class IfStatement implements Evaluable {

	private HasVariables variables;
	private Expression expression;
	private List<Evaluable> trueBlock = new ArrayList<Evaluable>();
	private List<Evaluable> falseBlock = new ArrayList<Evaluable>();

	public IfStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addFalseEvaluable(Evaluable evaluable) {
		falseBlock.add(evaluable);
	}

	public void addTrueEvaluable(Evaluable evaluable) {
		trueBlock.add(evaluable);
	}

	public void evaluate() {
		if (expression != null && variables != null) {
			Boolean result = expression.getBoolean();
			expression.updateVariables(variables.getVariables());
			
			if (result != null) {
				List<Evaluable> evaluables = result ? trueBlock : falseBlock;

				for (Evaluable evaluable : evaluables) {
					evaluable.setVariables(variables.getVariables());
					evaluable.evaluate();
					evaluable.updateVariables(variables.getVariables());
				}
			}
		}
	}

	@Override
	public void setVariables(Map<String, Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

	@Override
	public void updateVariables(Map<String, Variable<?>> variables) {
		// NOTE If statement cannot update variables after block evaluation
		/*if (expression != null) {
			expression.updateVariables(variables);
		}*/
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("if (" + expression.toString() + ") {\n");
		for (Evaluable evaluable : trueBlock) {
			builder.append("\t" + evaluable.toString() + ";\n");
		}
		builder.append("}");
		if (!falseBlock.isEmpty()) {
			builder.append(" else {\n");
			for (Evaluable evaluable : falseBlock) {
				builder.append("\t" + evaluable.toString() + ";\n");
			}
			builder.append("}");
		}
		return builder.toString();
	}

}
