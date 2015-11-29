/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.util.Map;

import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.HasActions;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Call implements Evaluable {

	private HasActions actions;
	private String actionId;

	public Call(HasActions actions, String actionId) {
		this.actions = actions;
		this.actionId = actionId;
	}

	@Override
	public void evaluate() {
		if (actions != null) {
			Action action = actions.getAction(actionId);
			if (action != null)
				action.execute();
		}
	}

	@Override
	public void setVariables(Map<String, Variable<?>> variables) {
		// nop
	}

	@Override
	public void updateVariables(Map<String, Variable<?>> variables) {
		// nop
	}

	@Override
	public String toString() {
		return "->" + actionId + "()";
	}

}
