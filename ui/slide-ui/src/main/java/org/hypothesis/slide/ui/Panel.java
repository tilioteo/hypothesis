/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import org.hypothesis.interfaces.Maskable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Panel extends com.vaadin.ui.Panel implements Maskable {

	private Mask mask = null;

	public Panel() {
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
