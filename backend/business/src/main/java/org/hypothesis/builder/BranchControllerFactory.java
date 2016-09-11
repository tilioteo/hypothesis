/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.BranchController;
import org.hypothesis.data.DocumentReader;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface BranchControllerFactory extends Serializable {

	/**
	 * Create new entity controller from string definition parsed by proper
	 * reader implementation
	 * 
	 * @param data
	 *            string definition of entity controller
	 * @param reader
	 *            implementation of reader knowing data structure
	 * @return new instance or null when inconsistent parameters provided
	 */
	BranchController buildBranchController(String data, DocumentReader reader);

}
