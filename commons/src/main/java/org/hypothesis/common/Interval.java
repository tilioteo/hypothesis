/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class Interval<T extends Comparable<T>> {
	T min;
	T max;

	public Interval(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean contains(T value) {
		if (value != null) {
			return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
		}

		return false;
	}

	public boolean containsExclusive(T value) {
		if (value != null) {
			return value.compareTo(min) > 0 && value.compareTo(max) < 0;
		}

		return false;
	}

	public boolean containsExclusiveRight(T value) {
		if (value != null) {
			return value.compareTo(min) >= 0 && value.compareTo(max) < 0;
		}

		return false;
	}

	public boolean containsExclusiveLeft(T value) {
		if (value != null) {
			return value.compareTo(min) > 0 && value.compareTo(max) <= 0;
		}

		return false;
	}

}
