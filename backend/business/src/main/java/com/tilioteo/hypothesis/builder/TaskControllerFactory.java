/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.TaskController;

/**
 * @author kamil
 *
 */
public interface TaskControllerFactory extends Serializable {

	public TaskController buildTaskController(String data);

}
