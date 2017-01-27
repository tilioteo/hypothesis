/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hypothesis.builder.TaskControllerFactory;
import org.hypothesis.business.ListManager;
import org.hypothesis.business.TaskController;
import org.hypothesis.business.TaskManager;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.interfaces.ExchangeVariable;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class TaskManagerImpl extends ListManager<Branch, Task> implements TaskManager {

	private static Logger log = Logger.getLogger(TaskManagerImpl.class);

	@Inject
	private TaskControllerFactory factory;

	private DocumentReader reader = new XmlDocumentReader();

	private Task current = null;
	private TaskController controller = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.TaskManager#current()
	 */
	@Override
	public Task current() {
		Task task = super.current();

		if (current != task) {
			current = task;

			if (current != null) {
				createTaskController();
			} else {
				controller = null;
			}
		}

		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.TaskManager#next()
	 */
	@Override
	public Task next() {
		super.next();

		return current();
	}

	private void createTaskController() {
		log.debug("Creating task controller.");

		controller = factory.createController(current, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.impl.TaskManager#addSlideOutputs(org.hypothesis.
	 * data.model.Slide, java.util.Map)
	 */
	@Override
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (controller != null) {
			controller.addSlideOutputs(slide, outputValues);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.impl.TaskManager#getNextSlideIndex(org.hypothesis
	 * .data.model.Slide)
	 */
	@Override
	public int getNextSlideIndex(Slide slide) {
		if (controller != null) {
			controller.getNextSlideIndex(current, slide);
		}

		return 0;
	}

}
