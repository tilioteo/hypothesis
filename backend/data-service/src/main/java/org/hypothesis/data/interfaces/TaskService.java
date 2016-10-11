package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Task;

public interface TaskService extends Serializable {

	Task findById(Long id);

}