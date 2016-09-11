/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.Task;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class TaskService {

	private static Logger log = Logger.getLogger(TaskService.class);

	private HibernateDao<Task, Long> taskDao;

	protected TaskService(HibernateDao<Task, Long> taskDao) {
		this.taskDao = taskDao;
	}

	public static TaskService newInstance() {
		return new TaskService(new HibernateDao<Task, Long>(Task.class));
	}

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
