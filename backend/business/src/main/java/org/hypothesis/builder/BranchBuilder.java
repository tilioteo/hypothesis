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
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchBuilder implements Serializable {

	private BranchBuilder() {
	}

	/**
	 * Reads entity definition using proper reader implementation and returns
	 * entity controller
	 * 
	 * @param entity
	 * @param reader
	 * @return new BranchController instance associated with entity or null when
	 *         some parameter is null or entity data is empty or data cannot be
	 *         parsed by reader.
	 */
	public static BranchController buildBranchController(Branch entity, DocumentReader reader) {

		BranchControllerFactory factory = new BranchControllerFactoryImpl();

		if (entity != null && reader != null) {
			return factory.buildBranchController(entity.getData(), reader);
		}

		return null;
	}
}
