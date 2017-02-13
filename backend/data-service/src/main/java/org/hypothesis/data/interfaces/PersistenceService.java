/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.*;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface PersistenceService extends Serializable {

	User merge(User entity);
	
	Pack merge(Pack entity);

	Branch merge(Branch entity);

	Task merge(Task entity);

	Slide merge(Slide entity);

	SimpleTest merge(SimpleTest entity);

}