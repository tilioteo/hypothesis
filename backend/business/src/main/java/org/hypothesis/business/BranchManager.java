/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.apache.log4j.Logger;
import org.hypothesis.builder.BranchBuilder;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Slide;
import org.hypothesis.interfaces.ExchangeVariable;

import java.util.Map;
import java.util.Optional;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchManager extends KeySetManager<Pack, Branch, Long> {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private DocumentReader reader = new XmlDocumentReader();

	private Branch current = null;
	private BranchController controller = null;

	@Override
	public Branch current() {
		Branch branch = super.current();

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

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		Optional.ofNullable(controller).ifPresent(e -> e.addSlideOutputs(slide, outputValues));
	}

	/**
	 * Look into provided map of branches and return the next one according to
	 * internal state
	 * 
	 * @param branchMap
	 * @return the next branch or null if map is empty or nothing found.
	 */
	public Branch getNextBranch(Map<String, Branch> branchMap) {
		return Optional.ofNullable(branchMap).filter(f -> controller != null).map(m -> controller.getNextBranchKey())
				.map(branchMap::get).map(m -> {
					find(m);
					return current();
				}).orElse(null);
	}

	public String getSerializedData() {
		return Optional.ofNullable(controller).map(m -> m.getNextKey()).orElse(null);
	}

}
