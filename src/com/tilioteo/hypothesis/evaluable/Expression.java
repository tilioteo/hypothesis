/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class Expression extends Primitive {

	protected Expression parent;
	protected Operator operator;
	protected VariableMap variables;
	
	protected Expression(Expression parent) {
		super();
		this.parent = parent;
		operator = null;
		if (parent == null) {
			variables = new VariableMap();
		} else {
			variables = parent.variables;
		}
	}
	
	protected Expression() {
		this(null);
	}
	
	@Override
	public void clear() {
		super.clear();
		
		operator = null;
		if (parent == null) {
			
		}
	}
	
	public void setVariableValue(String name, Object value) {
		Variable variable = variables.get(name);
		if (variable != null) {
			variable.setValue(value);
			variable.setType(variable.getType());
		}
	}
	
	public Object getVariableValue(String name) {
		Variable variable = variables.get(name);
		if (variable != null) {
			return variable.getValue();
		}
		return null;
	}

	public void mergeVariables(VariableMap variables) {
		for (String key : this.variables.keySet()) {
			if (!variables.containsKey(key)) {
				variables.put(key, this.variables.get(key));
			}
		}
		for (String key : variables.keySet()) {
			if (!this.variables.containsKey(key) || (this.variables.get(key) != variables.get(key))) {
				this.variables.put(key, variables.get(key));
			}
		}
	}
	
}
