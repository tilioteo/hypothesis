/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.util.Map;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface BranchService extends EntityService<Branch, Long> {

	Map<String, Branch> getBranches(Pack pack, Branch branch);

}