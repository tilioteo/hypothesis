/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import com.tilioteo.hypothesis.slide.shared.ui.mask.MaskClientRpc;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Mask extends AbstractExtension {
	
	private MaskClientRpc clientRpc;
	
	protected Mask(AbstractComponent target) {
		super.extend(target);
		
		clientRpc = getRpcProxy(MaskClientRpc.class);
	}
	
	public static Mask addToComponent(AbstractComponent target) {
		return new Mask(target);
	}
	
	public void show() {
		clientRpc.show();
	}
	
	public void hide() {
		clientRpc.hide();
	}

}
