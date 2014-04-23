/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

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
	
	public static PersistenceManager newInstance() {
		return new PersistenceManager();
	}
	
	public Pack merge(Pack entity) {
		try {
			HibernateUtil.beginTransaction();
			Pack pack = (Pack)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(pack.getBranches());
			HibernateUtil.commitTransaction();
			
			return pack;
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}
	
	public Branch merge(Branch entity) {
		try {
			HibernateUtil.beginTransaction();
			Branch branch = (Branch)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(branch.getTasks());
			Hibernate.initialize(branch.getBranchMap());
			HibernateUtil.commitTransaction();
		
			return branch;
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

	public Task merge(Task entity) {
		try {
			HibernateUtil.beginTransaction();
			Task task = (Task)HibernateUtil.getSession().merge(entity);
			Hibernate.initialize(task.getSlides());
			HibernateUtil.commitTransaction();
		
			return task;
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

	public SimpleTest merge(SimpleTest entity) {
		try {
			HibernateUtil.beginTransaction();
			SimpleTest test = (SimpleTest)HibernateUtil.getSession().merge(entity);
			HibernateUtil.commitTransaction();
		
			return test;
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}

}
