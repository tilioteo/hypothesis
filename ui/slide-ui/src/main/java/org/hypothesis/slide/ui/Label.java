/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import com.vaadin.shared.ui.label.ContentMode;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
