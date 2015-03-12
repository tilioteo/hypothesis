/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.util.Date;

import com.vaadin.shared.ui.datefield.Resolution;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DateRangeValidator extends com.vaadin.data.validator.DateRangeValidator implements Validator {

	public DateRangeValidator(String message, Date minValue,
			Date maxValue) {
		super(message, minValue, maxValue, Resolution.DAY);
	}

}
