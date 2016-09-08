/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class BranchController implements Serializable {

	private final List<Path> paths = new ArrayList<>();
	private DefaultPath defaultPath = null;

	private String nextKey = null;

	private final HashMap<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<>();

	public void addPath(AbstractBasePath path) {
		if (path instanceof Path) {
			this.paths.add((Path) path);
		} else if (path instanceof DefaultPath) {
			this.defaultPath = (DefaultPath) path;
		}
	}

	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased at the slide
			// finish
			HashMap<Integer, ExchangeVariable> map = new HashMap<>();
			for (Integer index : outputValues.keySet()) {
				map.put(index, outputValues.get(index));
			}

			slideOutputs.put(slide.getId(), map);
		}
	}

	public String getNextBranchKey() {
		nextKey = null;
		boolean pathFound = false;

		for (Path path : paths) {
			if (path.isValid(slideOutputs)) {
				nextKey = path.getBranchKey();
				pathFound = true;
				break;
			}
		}
		if (!pathFound && defaultPath != null) {
			nextKey = defaultPath.getBranchKey();
		}
		return nextKey;
	}

	public String getNextKey() {
		return nextKey;
	}

}
