/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.interfaces.HasData;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
