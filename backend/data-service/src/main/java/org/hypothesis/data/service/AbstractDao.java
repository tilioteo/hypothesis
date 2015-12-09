/**
 * 
 */
package org.hypothesis.data.service;

import java.io.Serializable;

import org.hypothesis.data.interfaces.GenericDao;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractDao<T, ID extends Serializable> implements GenericDao<T, ID> {

	protected Class<T> persistentClass;

	/**
	 * Class constructor
	 */
	protected AbstractDao(Class<T> clazz) {
		this.persistentClass = clazz;
		// this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Returns the class of the entity
	 * 
	 * @return the entity class
	 */
	protected Class<T> getPersistentClass() {
		return persistentClass;
	}

}
