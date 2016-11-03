package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Task;

import java.io.Serializable;

public interface TaskService extends Serializable {

	Task findById(Long id);

}