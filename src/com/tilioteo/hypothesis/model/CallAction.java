/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class CallAction implements Evaluable {

	private HasActions actions;
	private String actionId;

	public CallAction(HasActions actions, String actionId) {
		this.actions = actions;
		this.actionId = actionId;
	}

	public void evaluate() {
		if (actions != null) {
			AbstractBaseAction action = actions.getActions().get(actionId);
			if (action != null)
				action.execute();
		}
	}

	public void setVariables(VariableMap variables) {
		// nothing
	}

	public void updateVariables(VariableMap variables) {
		// nothing
	}

}
