/**
 * 
 */
package com.tilioteo.hypothesis.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import com.tilioteo.hypothesis.servlet.HibernateUtil;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractHibernateDao<T, ID extends Serializable> implements
		GenericDao<T, ID> {

	private Class<T> persistentClass;

	/**
	 * Class constructor
	 */
	@SuppressWarnings("unchecked")
	public AbstractHibernateDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Sets the hibernate session for making transactions with the db
	 * 
	 * @param s
	 *            the session to be set
	 */
	/*
	 * public void setSession(Session s) { this.session = s; }
	 */

	@Override
	public void beginTransaction() {
		HibernateUtil.beginTransaction();
	}

	/**
	 * Clears completely the session
	 */
	public void clear() {
		getSession().clear();
	}

	@Override
	public void commit() {
		flush();
		HibernateUtil.commitTransaction();
	}

	@Override
	public List<T> findAll() {
		return findByCriteria();
	}

	public Criteria createCriteria() {
		return getSession().createCriteria(getPersistentClass());
	}

	/**
	 * Not used. But, it can be used inside subclasses as a convenience method
	 * 
	 * @param criterion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock)
			entity = (T) getSession().load(getPersistentClass(), id,
					LockOptions.UPGRADE);
		else
			entity = (T) getSession().load(getPersistentClass(), id);

		return entity;
	}

	/**
	 * Forces the session to flush
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * Returns the class of the entity
	 * 
	 * @return the entity class
	 */
	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	/**
	 * Retrieves the session used for making transactions with the db
	 * 
	 * @return the session retrieved
	 */
	public Session getSession() {
		return HibernateUtil.getSession();
	}

	@Override
	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	/*
	 * public T makePersistent(T entity) { if ((entity instanceof
	 * SerializableIdObject && ((SerializableIdObject)entity).getId() == null) ||
	 * (entity instanceof SerializableUidObject &&
	 * ((SerializableUidObject)entity).getUid() == null)) {
	 * getSession().save(entity); } else { getSession().merge(entity); } return
	 * entity; }
	 */

	@Override
	public void makeTransient(T entity) {
		getSession().delete(entity);
	}
	
	@Override
	public void rollback() {
		HibernateUtil.rollbackTransaction();
	}
}
