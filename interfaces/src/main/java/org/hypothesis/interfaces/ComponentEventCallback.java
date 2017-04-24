/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@FunctionalInterface
public interface ComponentEventCallback {

	void initEvent(ComponentEvent componentEvent);

	static void empty(ComponentEvent componentEvent) {
	}
}
