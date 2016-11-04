/**
 * 
 */
package org.hypothesis.data.service.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hypothesis.data.interfaces.GenericDao;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class GenericDaoJpaImpl<T, ID extends Serializable> implements GenericDao<T, ID> {

	protected Class<T> entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public GenericDaoJpaImpl() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	@Override
	public void beginTransaction() {
		
	}

	@Override
	public void commit() {
		// nop
	}

	@Override
	public void rollback() {
		// nop
	}

	@Override
	public List<T> findAll() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);
		Root<T> root = query.from(entityClass);
		CriteriaQuery<T> all = query.select(root);
		TypedQuery<T> allQuery = entityManager.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public T findById(ID id, boolean lock) {
		return entityManager.find(entityClass, id, lock ? LockModeType.OPTIMISTIC : LockModeType.NONE);
	}

	@Override
	public T makePersistent(T entity) {
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

	@Override
	public void makeTransient(T entity) {
		entityManager.remove(entity);
	}

}
