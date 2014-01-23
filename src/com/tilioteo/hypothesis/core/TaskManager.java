/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Task;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TaskManager extends ListManager<Branch, Task> {

	private PairList<Slide, Object> slideOutputValues = new PairList<Slide, Object>();

	public void addSlideOutputValue(Slide slide, Object outputValue) {
		slideOutputValues.addObjectPair(slide, outputValue);

	}

}
