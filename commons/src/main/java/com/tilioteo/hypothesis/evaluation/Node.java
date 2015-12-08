/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.HasVariables;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Node extends AbstractVariableContainer implements Serializable {

	private long slideId;

	private List<Evaluable> evaluables = new ArrayList<Evaluable>();

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
			Map<String, com.tilioteo.hypothesis.interfaces.Variable<?>> variables = null;
			if (getVariables() != null)
				variables = getVariables();

			if (variables != null)
				evaluable.setVariables(variables);
			evaluable.evaluate();
			if (variables != null)
				evaluable.updateVariables(variables);

			if (breakExecution) {
				break;
			}
		}
	}

}
