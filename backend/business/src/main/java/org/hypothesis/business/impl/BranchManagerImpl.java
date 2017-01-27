/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hypothesis.builder.BranchControllerFactory;
import org.hypothesis.business.BranchController;
import org.hypothesis.business.BranchManager;
import org.hypothesis.business.KeySetManager;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Slide;
import org.hypothesis.interfaces.ExchangeVariable;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class BranchManagerImpl extends KeySetManager<Pack, Branch, Long> implements BranchManager {

	private static Logger log = Logger.getLogger(BranchManagerImpl.class);

	@Inject
	private BranchControllerFactory factory;

	private DocumentReader reader = new XmlDocumentReader();

	private Branch current = null;
	private BranchController controller = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.BanchManager#current()
	 */
	@Override
	public Branch current() {
		Branch branch = super.current();

		if (current != branch) {
			current = branch;

			if (current != null) {
				createBranchController();
			} else {
				controller = null;
			}
		}

		return current;
	}

	private void createBranchController() {
		log.debug("Building branch controller.");

		controller = factory.createController(current, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.BanchManager#addSlideOutputs(org.hypothesis.data.
	 * model.Slide, java.util.Map)
	 */
	@Override
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		Optional.ofNullable(controller).ifPresent(e -> e.addSlideOutputs(slide, outputValues));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.BanchManager#getNextBranch(java.util.Map)
	 */
	@Override
	public Branch getNextBranch(Map<String, Branch> branchMap) {
		return Optional.ofNullable(branchMap).filter(f -> controller != null).map(m -> controller.getNextBranchKey())
				.map(branchMap::get).map(m -> {
					find(m);
					return current();
				}).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.BanchManager#getSerializedData()
	 */
	@Override
	public String getSerializedData() {
		return Optional.ofNullable(controller).map(m -> m.getNextKey()).orElse(null);
	}

}
