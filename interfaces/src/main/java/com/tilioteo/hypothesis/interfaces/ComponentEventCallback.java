/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

/**
 * @author kamil
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
