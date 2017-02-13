/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface TaskManager {

	Task current();

	Task next();

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues);

	/**
	 * Look for node associated with slide and evaluate conditions to get next
	 * slide index.
	 * 
	 * @param slide
	 *            processed slide which is a child of current task
	 * @return index of next slide - value >= 1 means direct index of slide in
	 *         task, 0 means next slide after currently processed, -1 means go
	 *         to next task
	 */
	int getNextSlideIndex(Slide slide);

	void setListFromParent(Branch currentBranch);

	Task find(Task lastTask);

}