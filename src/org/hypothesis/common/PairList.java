/**
 * 
 */
package org.hypothesis.common;

import java.util.LinkedList;


/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PairList<T, U> extends LinkedList<Pair<T, U>> {
	
	public void addObjectPair(T first, U second) {
		add(new Pair<T, U>(first, second));
	}

}
