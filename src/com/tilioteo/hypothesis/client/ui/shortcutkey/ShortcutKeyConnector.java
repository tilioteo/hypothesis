/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.shortcutkey;

import com.tilioteo.hypothesis.client.ui.AbstractNonVisualComponentConnector;
import com.tilioteo.hypothesis.client.ui.VShortcutKey;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.ShortcutKey.class)
public class ShortcutKeyConnector extends AbstractNonVisualComponentConnector {

	@Override
	protected void init() {
		super.init();
	}
	
    @Override
    public VShortcutKey getWidget() {
    	return (VShortcutKey) super.getWidget(); 
    }

}
