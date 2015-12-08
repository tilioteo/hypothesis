/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.BranchController;
import com.tilioteo.hypothesis.data.DocumentReader;

/**
 * @author kamil
 *
 */
public interface BranchControllerFactory extends Serializable {

	public BranchController buildBranchController(String data, DocumentReader reader);

}
