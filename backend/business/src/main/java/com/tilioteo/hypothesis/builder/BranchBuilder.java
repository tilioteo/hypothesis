/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.BranchController;
import com.tilioteo.hypothesis.data.model.Branch;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class BranchBuilder implements Serializable {

	public static BranchController buildBranchController(Branch entity, BranchControllerFactory factory) {
		if (entity != null && factory != null) {
			BranchController controller = factory.buildBranchController(entity.getData());
			return controller;
		}
		return null;
	}
}
