/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.GenericDao;
import org.hypothesis.data.interfaces.TaskService;
import org.hypothesis.data.model.Task;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class TaskServiceImpl implements TaskService {

	private static final Logger log = Logger.getLogger(TaskServiceImpl.class);

	@Inject
	private GenericDao<Task, Long> taskDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.TaskService#findById(java.lang.Long)
	 */
	@Override
	public Task findById(Long id) {
		log.debug("TaskService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			taskDao.beginTransaction();

			Task task = taskDao.findById(id, false);
			taskDao.commit();
			return task;
		} catch (Exception e) {
			log.error(e.getMessage());
			taskDao.rollback();
		}
		return null;
	}
}
