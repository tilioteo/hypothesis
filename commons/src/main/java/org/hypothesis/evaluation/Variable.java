/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.Locale;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Variable<T> implements org.hypothesis.interfaces.Variable<T> {

	private final String name;
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
	@Override
	public void setRawValue(Object value) {
		try {
			this.value = (T) value;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public Class<?> getType() {
		if (value != null) {
			Class<?> type = value.getClass();

			if (type == byte.class || type == int.class || type == short.class
					|| Integer.class.isAssignableFrom(type)) {
				return Integer.class;
			} else if (type == long.class || type == double.class || type == float.class
					|| Double.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
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
				return value.toString();
			} else if (type.equals(Double.class)) {
				return String.format(Locale.ROOT, "%g", ((Double) value).doubleValue());
			} else if (type.equals(Boolean.class)) {
				return value.toString();
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

	public static Variable<?> createVariable(String name, Object value) {
		if (value != null) {
			Class<?> type = value.getClass();

			if (type == byte.class || type == int.class || type == short.class
					|| Integer.class.isAssignableFrom(type)) {
				return new Variable<Integer>(name, (Integer) value);
			} else if (type == long.class || Long.class.isAssignableFrom(type)) {
				return new Variable<Double>(name, ((Long) value).doubleValue());
			} else if (type == double.class || type == float.class || Double.class.isAssignableFrom(type)) {
				return new Variable<Double>(name, (Double) value);
			} else if (type == boolean.class || Boolean.class.isAssignableFrom(type)) {
				return new Variable<Boolean>(name, (Boolean) value);
			} else if (type == String.class || String.class.isAssignableFrom(type)) {
				return new Variable<String>(name, (String) value);
			}
		}

		return new Variable<Object>(name);
	}
}
