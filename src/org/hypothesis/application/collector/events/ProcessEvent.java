/**
 * 
 */
package org.hypothesis.application.collector.events;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEvent {
	private int id;
	private String name;

	public ProcessEvent(int id, String name) {
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
