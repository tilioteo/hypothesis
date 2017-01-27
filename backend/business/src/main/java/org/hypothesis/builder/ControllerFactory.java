/**
 * 
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.interfaces.HasData;

/**
 * @author kamil
 *
 */
public interface ControllerFactory<E extends HasData<?>, C extends Controller> extends Serializable {

	/**
	 * Create new entity controller from entity data parsed by proper
	 * reader implementation
	 * 
	 * @param entity
	 *            entity with data definition of controller
	 * @param reader
	 *            implementation of reader knowing data structure
	 * @return new instance or null when inconsistent parameters provided
	 */
	C createController(E entity, DocumentReader reader);
}
