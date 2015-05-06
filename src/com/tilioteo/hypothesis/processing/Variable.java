/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.Locale;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Variable<T> implements com.tilioteo.hypothesis.interfaces.Variable<T> {

	private String name;
	private T value;

	public Variable(String name) {
		this.name = name;
	}

	public Variable(String name, T value) {
		this(name);
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public void setRawValue(Object value) {
		try {
			this.value = (T) value;
		} catch (Throwable t) {
		}
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public Class<?> getType() {
		if (value != null) {
			Class<?> type = value.getClass();
			
			if (type == int.class || type == short.class || type == long.class || Integer.class.isAssignableFrom(type)) {
				return Integer.class;
			} else if (type == double.class || type == float.class || Double.class.isAssignableFrom(type)) {
				return Double.class;
			} else if (type == boolean.class || Boolean.class.isAssignableFrom(type)) {
				return Boolean.class;
			} else if (type == String.class || String.class.isAssignableFrom(type)) {
				return String.class;
			} else {
				return Object.class;
			}
		} else {
			return Object.class;
		}
	}

	/*
	 * public void setName(String name) { this.name = name; }
	 */
	
	@Override
	public String getStringValue() {
		if (value != null) {
			Class<?> type = getType();
			if (type.equals(Integer.class)) {
				return ((Integer)value).toString();
			} else if (type.equals(Double.class)) {
				return String.format(Locale.ROOT, "%g", ((Double)value).doubleValue());
			} else if (type.equals(Boolean.class)) {
				return ((Boolean) value).toString();
			} else if (type.equals(String.class)) {
				return (String) value;
			} else {
				// object variables are not supported yet
				return "";
			}
		} else {
			return "";
		}
	}
}
