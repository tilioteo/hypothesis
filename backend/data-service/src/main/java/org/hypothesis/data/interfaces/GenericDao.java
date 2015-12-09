/**
 * 
 */
package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface GenericDao<T, ID extends Serializable> extends Serializable {
	
	void beginTransaction();

	void commit();

	void rollback();

	/**
	 * Retrieves the entities that are equal to a given entity instance
	 * 
	 * @param exampleInstance
	 *            the instance to which the entities are compared
	 * @return the list of entities that are equal to the example instance
	 */
	// List<T> findByExample(T exampleInstance);

	/**
	 * Retrieves all entities of type T from the database
	 * 
	 * @return the retrieved entities
	 */
	List<T> findAll();

	/**
	 * Retrieves an entity of type T from the database using the entity id
	 * 
	 * @param id
	 *            the id of the entity
	 * @param lock
	 * @return the retrieved entity
	 */
	T findById(ID id, boolean lock);

	/**
	 * Makes the storage of the entity permanent at the db
	 * 
	 * @param entity
	 *            the entity to store in the db
	 * @return the stored entity
	 */
	T makePersistent(T entity);

	/**
	 * Deletes the given entity from the db
	 * 
	 * @param entity
	 */
	void makeTransient(T entity);

}
