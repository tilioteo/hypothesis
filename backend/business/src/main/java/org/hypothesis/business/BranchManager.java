/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hypothesis.builder.BranchBuilder;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.dto.BranchDto;
import org.hypothesis.data.dto.BranchKeyMap;
import org.hypothesis.data.dto.SlideDto;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchManager extends KeySetManager<BranchDto, Long> {

	private static final Logger log = Logger.getLogger(BranchManager.class);

	private final DocumentReader reader = new XmlDocumentReader();

	private BranchDto current = null;
	private BranchController controller = null;

	public BranchManager() {
		super();
	}

	@Override
	public BranchDto current() {
		BranchDto branch = super.current();

		if (current != branch) {
			current = branch;

			if (current != null) {
				buildBranchController();
			} else {
				controller = null;
			}
		}

		return current;
	}

	private void buildBranchController() {
		log.debug("Building branch controller.");

		controller = BranchBuilder.buildBranchController(current, reader);
	}

	public BranchController getController() {
		return controller;
	}

	public void addSlideOutputs(SlideDto slide, Map<Integer, ExchangeVariable> outputValues) {
		if (controller != null) {
			controller.addSlideOutputs(slide, outputValues);
		}
	}

	public BranchDto getNextBranch(BranchKeyMap branchMap) {
		if (branchMap != null && controller != null) {
			String key = controller.getNextBranchKey();

			Long branchId = branchMap.get(key);

			if (branchId != null) {
				findById(branchId);
				return current();
			}
		}

		return null;
	}

	public String getSerializedData() {
		if (controller != null) {
			return controller.getNextKey();
		}

		return null;
	}

}
