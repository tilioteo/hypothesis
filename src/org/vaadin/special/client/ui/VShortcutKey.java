/**
 * 
 */
package org.vaadin.special.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VShortcutKey extends Widget {

    public static final String CLASSNAME = "v-shortcutkey";

	public VShortcutKey() {
		setElement(Document.get().createDivElement());
		setStyleName(CLASSNAME);
		setVisible(false);
	}

}
