/**
 * 
 */
package com.tilioteo.hypothesis.core;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Pair<T, U> {
	T first;
	U second;
	
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	public T getFirst() {
		return first;
	}
	
	public U getSecond() {
		return second;
	}
}
