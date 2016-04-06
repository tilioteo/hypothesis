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

	public BranchController buildBranchController(String data, DocumentReader reader);

}
