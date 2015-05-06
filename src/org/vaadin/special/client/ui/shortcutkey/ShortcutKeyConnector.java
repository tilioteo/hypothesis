/**
 * 
 */
package org.vaadin.special.client.ui.shortcutkey;

import org.vaadin.special.client.ui.AbstractNonVisualComponentConnector;
import org.vaadin.special.client.ui.VShortcutKey;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.ShortcutKey.class)
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
