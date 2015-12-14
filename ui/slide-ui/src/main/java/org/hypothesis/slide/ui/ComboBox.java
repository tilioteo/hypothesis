/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import org.hypothesis.interfaces.Field;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ComboBox extends com.vaadin.ui.ComboBox implements Field {

	public ComboBox() {
		super();
	}

	@Override
	public void setValue(Object newValue) throws com.vaadin.data.Property.ReadOnlyException {
		boolean readOnly = false;
		if (isReadOnly()) {
			readOnly = true;
			setReadOnly(false);
		}
		super.setValue(newValue);

		if (readOnly) {
			setReadOnly(true);
		}
	}

}
