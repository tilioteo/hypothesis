/**
 * 
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.BranchController;
import org.hypothesis.data.DocumentReader;

/**
 * @author kamil
 *
 */
public interface BranchControllerFactory extends Serializable {

	public BranchController buildBranchController(String data, DocumentReader reader);

}
