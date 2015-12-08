/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.business.BranchController;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.data.model.Branch;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class BranchBuilder implements Serializable {

	public static BranchController buildBranchController(Branch entity, DocumentReader reader) {

		BranchControllerFactory factory = new BranchControllerFactoryImpl();

		if (entity != null && reader != null) {
			BranchController controller = factory.buildBranchController(entity.getData(), reader);

			return controller;
		}

		return null;
	}
}
