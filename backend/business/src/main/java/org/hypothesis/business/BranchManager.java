package org.hypothesis.business;

import java.util.Map;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Slide;
import org.hypothesis.interfaces.ExchangeVariable;

public interface BranchManager {

	Branch current();

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues);

	/**
	 * Look into provided map of branches and return the next one according to
	 * internal state
	 * 
	 * @param branchMap
	 * @return the next branch or null if map is empty or nothing found.
	 */
	Branch getNextBranch(Map<String, Branch> branchMap);

	String getSerializedData();

	void setListFromParent(Pack currentPack);

	Branch find(Branch lastBranch);

}