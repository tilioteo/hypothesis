/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VShortcutKey extends Widget {

    public static final String CLASSNAME = "v-shortcutkey";

	public VShortcutKey() {
		setElement(DOM.createDiv());
		setStyleName(CLASSNAME);
		setVisible(false);
	}

}
