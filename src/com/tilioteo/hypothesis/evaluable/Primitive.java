/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class Primitive implements Serializable {
	
	private Object value;
	
	public Object getValue() {
		return value;
	}
	
	public Double getDouble() {
		Object val = getValue();
		if (val instanceof Variable)
			val = ((Variable)val).getValue();
		if (val instanceof Double) {
			return (Double)val;
		} else if (val instanceof Integer) {
			return new Double((Integer)val);
		} else if (val instanceof Boolean) {
			return ((Boolean)val).booleanValue() ? new Double(1) : new Double(0);
		} else
			return null;
	}
	
	public Integer getInteger() {
		Object val = getValue();
		if (val instanceof Variable)
			val = ((Variable)val).getValue();
		if (val instanceof Integer) {
			return (Integer)val;
		} else if (val instanceof Double) {
			return new Integer(((Double)val).intValue());
		} else if (val instanceof Boolean) {
			return ((Boolean)val).booleanValue() ? new Integer(1) : new Integer(0);
		} else
			return null;
	}
	
	public Boolean getBoolean() {
		Object val = getValue();
		if (val instanceof Variable)
			val = ((Variable)val).getValue();
		if (val instanceof Boolean) {
			return (Boolean)val;
		} else if (val instanceof Integer) {
			return ((Integer)val).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (val instanceof Double) {
			return ((Double)val).doubleValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else
			return null;
	}
	
	protected void setValue(Object value) {
		this.value = value;
	}
	
	public void clear() {
		value = null;
	}

	@Override
	public String toString() {
		return value != null ? value.toString() : "⦰";
	}
}
