/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
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
		}
	}
	
	public Object getVariableValue(String name) {
		Variable variable = variables.get(name);
		if (variable != null) {
			return variable.getValue();
		}
		return null;
	}
	
}
