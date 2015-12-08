/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.TaskController;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.data.model.Task;

/**
 * @author kamil
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
