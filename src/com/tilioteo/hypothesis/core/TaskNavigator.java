/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TaskNavigator implements Serializable {
	
	private Node node;
	
	public TaskNavigator(Node node) {
		this.node = node;
	}

	public void next(int index) {
		node.setNextIndex(index);
	}
	
}
