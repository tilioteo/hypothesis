/**
 * 
 */
package com.tilioteo.hypothesis.common;

import java.util.HashSet;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class StringSet extends HashSet<String> {

	public StringSet() {
		super();
	}

	public StringSet(StringSet stringSet, String[] strings) {
		this();

		if (stringSet != null)
			addAll(stringSet);

		if (strings != null)
			for (String str : strings)
				add(str);
	}

	public StringSet(String[] strings) {
		this(null, strings);
	}

	public StringSet(StringSet stringSet) {
		this(stringSet, null);
	}

}
