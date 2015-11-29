/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import com.vaadin.shared.ui.label.ContentMode;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class Label extends com.vaadin.ui.Label {

	public Label() {
		super();
		setContentMode(ContentMode.HTML);
	}

	@Override
	public String getCaption() {
		return getValue();
	}
	
	@Override
	public void setCaption(String caption) {
		setValue(caption);
	}

}
