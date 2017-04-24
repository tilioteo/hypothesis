/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.HasVariables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Node extends AbstractVariableContainer implements Serializable {

	private final long slideId;

	private final List<Evaluable> evaluables = new ArrayList<>();

	private int nextIndex = -1;
	private boolean breakExecution = false;

	public Node(HasVariables variables, long slideId) {
		super(variables);
		this.slideId = slideId;
	}

	public long getSlideId() {
		return slideId;
	}

	public void add(Evaluable evaluable) {
		evaluables.add(evaluable);
	}

	public int getNextIndex() {
		return nextIndex;
	}

	public void setNextIndex(int nextIndex) {
		this.nextIndex = nextIndex;

		if (nextIndex > 0) {
			breakExecution = true;
		}
	}

	public void execute() {
		breakExecution = false;
		for (Evaluable evaluable : evaluables) {
			Map<String, org.hypothesis.interfaces.Variable<?>> variables = getVariables();

			if (variables != null) {
				evaluable.setVariables(variables);
			}
			evaluable.evaluate();
			if (variables != null) {
				evaluable.updateVariables(variables);
			}

			if (breakExecution) {
				break;
			}
		}
	}

}
