/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.lang.reflect.Method;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
	
	/*protected Object mergeInternal(Object entity) {
		try {
			HibernateUtil.beginTransaction();
			Object merged = HibernateUtil.getSession().merge(entity);
			//Hibernate.initialize(merged);
			//initializeLazyCollections(merged);
			HibernateUtil.commitTransaction();
			
			return merged;
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			return null;
		}
	}*/
	
	private void initializeLazyCollections(Object entity) {
		Method[] methods = entity.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAccessible()) {
				OneToMany oneToMany = null;
				if (method.isAnnotationPresent(OneToMany.class)) {
					oneToMany = method.getAnnotation(OneToMany.class);
				}
				ManyToOne manyToOne = null;
				if (method.isAnnotationPresent(ManyToOne.class)) {
					manyToOne = method.getAnnotation(ManyToOne.class);
				}
				ManyToMany manyToMany = null;
				if (method.isAnnotationPresent(ManyToMany.class)) {
					manyToMany = method.getAnnotation(ManyToMany.class);
				}

				LazyCollection lazyCollection = null;
				if (method.isAnnotationPresent(LazyCollection.class)) {
					lazyCollection = method.getAnnotation(LazyCollection.class);
				}

				if (oneToMany != null && FetchType.LAZY.equals(oneToMany.fetch())
						|| manyToOne != null && FetchType.LAZY.equals(manyToOne.fetch())
						|| manyToMany != null && FetchType.LAZY.equals(manyToMany.fetch())
						|| lazyCollection != null && LazyCollectionOption.TRUE.equals(lazyCollection.value())) {
					try {
						method.invoke(entity, new Object[] {});
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*public Pack merge(Pack entity) {
		Pack pack = (Pack) mergeInternal(entity);
		Hibernate.initialize(pack.getBranches());
		
		return pack;
	}*/
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
	
	/*public Branch merge(Branch entity) {
		Branch branch = (Branch) mergeInternal(entity);
		Hibernate.initialize(branch.getTasks());
		Hibernate.initialize(branch.getBranchMap());
		
		return branch;
	}*/
	
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

	/*public Task merge(Task entity) {
		Task task = (Task) mergeInternal(entity);
		Hibernate.initialize(task.getSlides());
		
		return task;
	}*/
	
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

	/*public SimpleTest merge(SimpleTest entity) {
		return (SimpleTest) mergeInternal(entity);
	}*/
	
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
