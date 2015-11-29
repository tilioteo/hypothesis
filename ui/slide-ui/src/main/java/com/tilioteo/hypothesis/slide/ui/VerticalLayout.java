/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class VerticalLayout extends com.vaadin.ui.VerticalLayout implements Maskable {

	private Mask mask = null;

	public VerticalLayout() {
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
