/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.util.Map;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.builder.BranchBuilder;
import com.tilioteo.hypothesis.builder.BranchControllerFactory;
import com.tilioteo.hypothesis.builder.xml.BranchControllerXmlFactory;
import com.tilioteo.hypothesis.data.model.Branch;
import com.tilioteo.hypothesis.data.model.Pack;
import com.tilioteo.hypothesis.data.model.Slide;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class BranchManager extends KeySetManager<Pack, Branch, Long> {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private BranchControllerFactory factory = new BranchControllerXmlFactory();

	private Branch current = null;
	private BranchController controller = null;

	public BranchManager() {
		super();
	}

	@Override
	public Branch current() {
		Branch branch = super.current();

		if (current != branch) {
			current = branch;

			if (current != null) {
				buildBranchController();
			}
		}

		return current;
	}

	private void buildBranchController() {
		log.debug("Building branch controller.");

		controller = BranchBuilder.buildBranchController(current, factory);
	}

	/*@Override
	public Branch find(Branch item) {
		if (item != current) {
			super.find(item);
		}
		return current();
	}*/

	/*@Override
	public Branch get(Long key) {
		clearBranchRelatives();
		super.get(key);
		return current();
	}*/

	/*public String getSerializedData() {
		return nextKey;
	}*/

	/*@Override
	public void setCurrent(Branch item) {
		if (item != super.current()) {
			clearBranchRelatives();
			super.setCurrent(item);
			buildBranch();
		}
	}*/

	public BranchController getController() {
		return controller;
	}

	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (controller != null) {
			controller.addSlideOutputs(slide, outputValues);
		}
	}

	public String getNextBranchKey() {
		if (controller != null) {
			return controller.getNextBranchKey();
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
