/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.business.TaskController;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Task;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public final class TaskBuilder implements Serializable {

	private TaskBuilder() {
	}

	/**
	 * Reads entity definition using proper reader implementation and returns
	 * entity controller
	 * 
	 * @param entity
	 * @param reader
	 * @return new TaskController instance associated with entity or null when
	 *         some parameter is null or entity data is empty or data cannot be
	 *         parsed by reader.
	 */
	public static TaskController buildTaskController(Task entity, DocumentReader reader) {

		TaskControllerFactory factory = new TaskControllerFactoryImpl();

		if (entity != null && reader != null) {
			return factory.buildTaskController(entity.getData(), reader);
		}

		return null;
	}

}
