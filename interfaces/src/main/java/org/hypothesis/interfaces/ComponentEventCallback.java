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
public interface ComponentEventCallback {

	void initEvent(ComponentEvent componentEvent);

	ComponentEventCallback DEFAULT = new ComponentEventCallback() {
		@Override
		public void initEvent(ComponentEvent componentEvent) {
		}
	};

}
