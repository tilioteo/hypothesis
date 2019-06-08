/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.business.BranchController;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.dto.BranchDto;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchBuilder implements Serializable {

	public static BranchController buildBranchController(BranchDto dto, DocumentReader reader) {

		BranchControllerFactory factory = new BranchControllerFactoryImpl();

		if (dto != null && reader != null) {

			return factory.buildBranchController(dto.getData(), reader);
		}

		return null;
	}
}
