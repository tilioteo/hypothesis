/**
 * 
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.TaskController;
import org.hypothesis.data.DocumentReader;

/**
 * @author kamil
 *
 */
public interface TaskControllerFactory extends Serializable {

	public TaskController buildTaskController(String data, DocumentReader reader);

}
