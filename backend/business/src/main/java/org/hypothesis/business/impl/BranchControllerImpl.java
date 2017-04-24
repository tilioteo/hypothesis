/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.hypothesis.business.BranchController;
import org.hypothesis.data.model.Slide;
import org.hypothesis.evaluation.AbstractBasePath;
import org.hypothesis.evaluation.DefaultPath;
import org.hypothesis.evaluation.Path;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchControllerImpl implements BranchController {

	private List<Path> paths = new ArrayList<>();
	private DefaultPath defaultPath = null;

	private String nextKey = null;

	private Map<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<>();

	/**
	 * Add branch path previously created from it's definition
	 * 
	 * @param path
	 */
	@Override
	public void addPath(AbstractBasePath path) {
		if (path instanceof Path) {
			this.paths.add((Path) path);
		} else if (path instanceof DefaultPath) {
			this.defaultPath = (DefaultPath) path;
		}
	}

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	@Override
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased on slide finish
			slideOutputs.put(slide.getId(),
					outputValues.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}
	}

	@Override
	public String getNextBranchKey() {
		nextKey = paths.stream().filter(f -> f.isValid(slideOutputs)).map(Path::getBranchKey).findFirst()
				.orElse(defaultPath != null ? defaultPath.getBranchKey() : null);
		return nextKey;
	}

	@Override
	public String getNextKey() {
		return nextKey;
	}

}
