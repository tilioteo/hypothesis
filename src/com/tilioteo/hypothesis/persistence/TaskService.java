/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.dao.TaskDao;
import com.tilioteo.hypothesis.entity.Task;

/**
 * @author kamil
 *
 */
public class TaskService {

	private static Logger log = Logger.getLogger(TaskService.class);

	private TaskDao taskDao; 

	public static TaskService newInstance() {
		return new TaskService(new TaskDao());
	}
	
	protected TaskService(TaskDao taskDao) {
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
