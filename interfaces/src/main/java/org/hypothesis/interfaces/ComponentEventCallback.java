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

	public void initEvent(ComponentEvent componentEvent);

	public static final ComponentEventCallback DEFAULT = new ComponentEventCallback() {
		@Override
		public void initEvent(ComponentEvent componentEvent) {
		}
	};

}
