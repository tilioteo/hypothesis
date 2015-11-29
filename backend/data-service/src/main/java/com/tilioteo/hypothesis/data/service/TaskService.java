/**
 * 
 */
package com.tilioteo.hypothesis.data.service;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.data.model.Task;

/**
 * @author kamil
 *
 */
public class TaskService {

	private static Logger log = Logger.getLogger(TaskService.class);

	private HibernateDao<Task, Long> taskDao; 

	public static TaskService newInstance() {
		return new TaskService(new HibernateDao<Task, Long>(Task.class));
	}
	
	protected TaskService(HibernateDao<Task, Long> taskDao) {
		this.taskDao = taskDao;
	}

	public Task findById(Long id) {
		log.debug("TaskService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			taskDao.beginTransaction();
			
			Task task = taskDao.findById(id, false);
			taskDao.commit();
			return task;
		} catch (Throwable e) {
			log.error(e.getMessage());
			taskDao.rollback();
		}
		return null;
	}
}
