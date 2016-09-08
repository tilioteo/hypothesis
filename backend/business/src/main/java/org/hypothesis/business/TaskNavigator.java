/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.evaluation.Node;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskNavigator implements Serializable {

	private final Node node;

	public TaskNavigator(Node node) {
		this.node = node;
	}

	public void nextIndex(int index) {
		node.setNextIndex(index);
	}

	public void next() {
		node.setNextIndex(0);
	}

	public void nextTask() {
		node.setNextIndex(-1);
	}

}
