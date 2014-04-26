/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * @author kamil
 *
 */
public class VCheckBox extends SelectButton {

	public static final String CLASSNAME = "v-checkbox";

	@Override
	protected Element createInputElement() {
		return DOM.createInputCheck();
	}

}
