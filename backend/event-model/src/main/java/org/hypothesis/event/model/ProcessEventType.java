/**
 * 
 */
package org.hypothesis.event.model;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ProcessEventType implements Serializable {
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
