/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.dao;

import java.io.Serializable;

import org.hypothesis.data.interfaces.GenericDao;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractDao<T, ID extends Serializable> implements GenericDao<T, ID> {

	protected final Class<T> persistentClass;

	/**
	 * Class constructor
	 */
	protected AbstractDao(Class<T> clazz) {
		this.persistentClass = clazz;
		// this.persistentClass = (Class<T>) ((ParameterizedType)
		// getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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
