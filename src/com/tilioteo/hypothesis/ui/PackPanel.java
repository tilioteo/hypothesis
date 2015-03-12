/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.entity.Pack;
import com.vaadin.ui.Button.ClickEvent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PackPanel extends SimplePackPanel {

	public PackPanel(Pack pack) {
		super(pack);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		super.buttonClick(event);
		
		collapse();
	}
	
}
