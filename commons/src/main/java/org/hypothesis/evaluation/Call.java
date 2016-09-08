/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.Map;

import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.HasActions;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Call implements Evaluable {

	private final HasActions actions;
	private final String actionId;

	public Call(HasActions actions, String actionId) {
		this.actions = actions;
		this.actionId = actionId;
	}

	@Override
	public void evaluate() {
		if (actions != null) {
			org.hypothesis.interfaces.Action action = actions.getAction(actionId);
			if (action != null)
				action.execute();
		}
	}

	@Override
	public void setVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		// nop
	}

	@Override
	public void updateVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		// nop
	}

	@Override
	public String toString() {
		return "->" + actionId + "()";
	}

}
