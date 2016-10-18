/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hypothesis.context.HibernateUtil;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class HibernateDao<T, ID extends Serializable> extends AbstractDao<T, ID> {

	public HibernateDao(Class<T> clazz) {
		super(clazz);
	}

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
		// flush();
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
		Arrays.stream(criterion).forEach(crit::add);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock)
			entity = (T) getSession().load(getPersistentClass(), id, LockOptions.UPGRADE);
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
	 * SerializableIdObject && ((SerializableIdObject)entity).getId() == null)
	 * || (entity instanceof SerializableUidObject &&
	 * ((SerializableUidObject)entity).getUid() == null)) {
	 * getSession().save(entity); } else { getSession().merge(entity); } return
	 * entity; }
	 */

	@Override
	public void makeTransient(T entity) {
		getSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public T merge(T entity) {
		// clear();
		return (T) getSession().merge(entity);
	}

	@Override
	public void rollback() {
		HibernateUtil.rollbackTransaction();
	}
}
