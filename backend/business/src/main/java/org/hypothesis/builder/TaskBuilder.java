/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.TaskController;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Task;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskBuilder implements Serializable {

	public static TaskController buildTaskController(Task entity, DocumentReader reader) {

		TaskControllerFactory factory = new TaskControllerFactoryImpl();

		if (entity != null && reader != null) {
			TaskController controller = factory.buildTaskController(entity.getData(), reader);

			return controller;
		}

		return null;
	}

}
