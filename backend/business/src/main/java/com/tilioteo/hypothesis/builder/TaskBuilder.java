/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.TaskController;
import com.tilioteo.hypothesis.data.model.Task;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TaskBuilder implements Serializable {

	public static TaskController buildTaskController(Task entity, TaskControllerFactory factory) {
		if (entity != null && factory != null) {
			TaskController controller = factory.buildTaskController(entity.getData());

			return controller;
		}
		return null;
	}

}
