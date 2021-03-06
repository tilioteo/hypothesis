/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.BranchOutput;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface OutputService extends Serializable {

	void saveBranchOutput(BranchOutput branchOutput);

}