/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import com.tilioteo.hypothesis.common.StringConstants;
import com.tilioteo.hypothesis.common.Strings;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Variable extends Primitive {

	private int refCount;
	private String name;
	private Class<?> type;
	
	protected int decRefCount() {
		return (refCount > 1 ? --refCount : 0);
	}
	
	protected void incRefCount() {
		++refCount;
	}

	public Variable(String name, Class<?> type) {
		assert (!Strings.isNullOrEmpty(name));
		
		this.refCount = 0;
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	@Override
	public void setValue(Object value) {
		boolean assigned = false;
		if (value != null) {
			if (type.isAssignableFrom(value.getClass())) {
				super.setValue(value);
				assigned = true;
			} else if (value.getClass() == String.class) {
				String strValue = (String)value;
				if (type == Integer.class) {
					try {
						Integer val = Integer.parseInt(strValue);
						super.setValue(val);
						assigned = true;
					} catch (NumberFormatException e) {}
				} else if (type == Double.class) {
					try {
						Double val = Double.parseDouble(strValue);
						super.setValue(val);
						assigned = true;
					} catch (NumberFormatException e) {}
				} else if (type == Boolean.class) {
					if (strValue.equalsIgnoreCase(StringConstants.STR_BOOL_TRUE)) {
						super.setValue(Boolean.TRUE);
						assigned = true;
					} else if (strValue.equalsIgnoreCase(StringConstants.STR_BOOL_FALSE)) {
						super.setValue(Boolean.FALSE);
						assigned = true;
					}
				}
			}
		}
		
		if (!assigned) {
			super.setValue(null);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
