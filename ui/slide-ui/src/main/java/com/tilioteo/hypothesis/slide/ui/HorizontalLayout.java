/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class HorizontalLayout extends com.vaadin.ui.HorizontalLayout implements Maskable {

	private Mask mask = null;

	public HorizontalLayout() {
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
