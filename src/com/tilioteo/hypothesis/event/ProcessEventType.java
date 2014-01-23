/**
 * 
 */
package com.tilioteo.hypothesis.event;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEventType {
	private int id;
	private String name;

	public ProcessEventType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
