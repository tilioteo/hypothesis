/**
 * 
 */
package org.hypothesis.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         This class is specialized hash map to mask (and replace) external url
 *         path to right internal url
 * 
 */
public class PathRemapper {

	HashMap<String, String> pathremap = new HashMap<String, String>();

	public String get(String path) {
		if (pathremap.size() > 0) {
			// exact match
			if (pathremap.containsKey(path))
				return pathremap.get(path);

			// partial match
			Iterator<String> names = pathremap.keySet().iterator();
			ArrayList<String> potentialMatches = new ArrayList<String>();
			while (names.hasNext()) {
				String match = names.next();
				if (path.contains(match))
					potentialMatches.add(match);
			}

			if (potentialMatches.size() > 0) {
				Collections.sort(potentialMatches);
				String match = potentialMatches
						.get(potentialMatches.size() - 1);
				return path.replace(match, pathremap.get(match));
			}
		}

		return path;
	}

	public void put(String oldPath, String newPath) {
		pathremap.put(oldPath, newPath);
	}

}
