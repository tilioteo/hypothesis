/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import java.util.Date;

import org.hypothesis.interfaces.Field;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField implements Field {

	public DateField() {
		super();
	}

	@Override
	public void setValue(Date newValue) throws com.vaadin.data.Property.ReadOnlyException {
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
