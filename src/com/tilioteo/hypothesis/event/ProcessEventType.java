/**
 * 
 */
package com.tilioteo.hypothesis.event;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEventType {
	private long id;
	private String name;

	public ProcessEventType(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
