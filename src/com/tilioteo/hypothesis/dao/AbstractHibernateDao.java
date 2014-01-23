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
	private Session session = null;

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

	public void beginTransaction() {
		if (session != null && session.isOpen())
			throw new IllegalStateException(
					"Session allready opened and transaction started.");

		session = HibernateUtil.getSession();
		session.beginTransaction();
	}

	/**
	 * Clears completely the session
	 */
	public void clear() {
		getSession().clear();
	}

	public void commit() {
		flush();
		getSession().getTransaction().commit();
		session = null;
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	// public List<T> findByExample(T exampleInstance, String[] excludeProperty)
	// {
	// Criteria crit = getSession().createCriteria(getPersistentClass());
	// Example example = Example.create(exampleInstance);
	// for (String exclude : excludeProperty) {
	// example.excludeProperty(exclude);
	// }
	// crit.add(example);
	// return crit.list();
	// }

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
	protected Session getSession() {
		if (session == null)
			throw new IllegalStateException(
					"Session has not been set on DAO before usage, try use beginTransaction() first.");
		return session;
	}

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

	public void makeTransient(T entity) {
		getSession().delete(entity);
	}

	public void rollback() {
		getSession().getTransaction().rollback();
		session = null;
	}
}
