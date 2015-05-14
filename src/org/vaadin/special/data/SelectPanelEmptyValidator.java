/**
 * 
 */
package org.vaadin.special.data;

import java.util.Collection;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SelectPanelEmptyValidator extends EmptyValidator {

	public SelectPanelEmptyValidator(String message) {
		super(message);
	}

	@Override
	public boolean isValid(Object value) {
		if (null == value || !(value instanceof Collection<?>) || ((Collection<?>)value).size() == 0) {
			return false;
		}

		return true;
	}
	
}
