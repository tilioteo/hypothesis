/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.servlet.HibernateUtil;

/**
 * @author kamil
 *
 */
public class PersistenceManager {
	
	private static Logger log = Logger.getLogger(PersistenceManager.class);

	public static PersistenceManager newInstance() {
		return new PersistenceManager();
	}
	
	public Pack merge(Pack entity) {
		log.debug(String.format("merge(pack id = %s)", entity != null ? entity.getId() : "NULL"));
		try {
			HibernateUtil.beginTransaction();
			Pack pack = (Pack)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(pack.getBranches());
			HibernateUtil.commitTransaction();
			
			return pack;
		} catch (Throwable e) {
			log.error(e.getMessage());
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}
	
	public Branch merge(Branch entity) {
		log.debug(String.format("merge(branch id = %s)", entity != null ? entity.getId() : "NULL"));
		try {
			HibernateUtil.beginTransaction();
			Branch branch = (Branch)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(branch.getTasks());
			HibernateUtil.commitTransaction();
		
			return branch;
		} catch (Throwable e) {
			log.error(e.getMessage());
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

	public Task merge(Task entity) {
		log.debug(String.format("merge(task id = %s)", entity != null ? entity.getId() : "NULL"));
		try {
			HibernateUtil.beginTransaction();
			Task task = (Task)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(task.getSlides());
			HibernateUtil.commitTransaction();
		
			return task;
		} catch (Throwable e) {
			log.error(e.getMessage());
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

	public SimpleTest merge(SimpleTest entity) {
		log.debug(String.format("merge(test id = %s)", entity != null ? entity.getId() : "NULL"));
		try {
			HibernateUtil.beginTransaction();
			SimpleTest test = (SimpleTest)HibernateUtil.getSession().merge(entity);
			HibernateUtil.commitTransaction();
		
			return test;
		} catch (Throwable e) {
			log.error(e.getMessage());
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

}
