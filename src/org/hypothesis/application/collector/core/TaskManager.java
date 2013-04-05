/**
 * 
 */
package org.hypothesis.application.collector.core;

import org.hypothesis.application.collector.QueueManager;
import org.hypothesis.common.PairList;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.Task;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TaskManager extends QueueManager<Branch, Task> {

	private PairList<Slide, Object> slideOutputValues = new PairList<Slide, Object>();

	public void addSlideOutputValue(Slide slide, Object outputValue) {
		slideOutputValues.addObjectPair(slide, outputValue);

	}

}
