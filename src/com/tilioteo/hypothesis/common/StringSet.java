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
	
	public StringSet(String[] strings) {
		this();
		
		if (strings != null)
			for (String str : strings)
				add(str);
	}
}
