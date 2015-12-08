/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import com.tilioteo.hypothesis.interfaces.Maskable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Image extends org.vaadin.special.ui.Image implements Maskable {

	private Mask mask = null;
	
	public Image() {
		super();
	}
	
	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor("#808080");
		mask.show();
	}

	@Override
	public void mask(String color) {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor(color);
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

}
