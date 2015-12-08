/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.TaskController;
import com.tilioteo.hypothesis.data.DocumentReader;

/**
 * @author kamil
 *
 */
public interface TaskControllerFactory extends Serializable {

	public TaskController buildTaskController(String data, DocumentReader reader);

}
