/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.TaskController;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.dto.TaskDto;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskBuilder implements Serializable {

	public static TaskController buildTaskController(TaskDto dto, DocumentReader reader) {

		TaskControllerFactory factory = new TaskControllerFactoryImpl();

		if (dto != null && reader != null) {

			return factory.buildTaskController(dto.getData(), reader);
		}

		return null;
	}

}
