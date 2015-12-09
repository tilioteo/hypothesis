/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.BranchController;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Branch;

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
