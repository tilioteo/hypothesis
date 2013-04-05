/**
 * 
 */
package org.hypothesis.application.collector.slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Variable<T> {

	private String name;
	private T value;

	public Variable(String name) {
		this.name = name;
	}

	public Variable(String name, T value) {
		this(name);
		this.value = value;
	}

	public String getName() {
		return name;
	}

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

	/*
	 * public void setName(String name) { this.name = name; }
	 */

}
