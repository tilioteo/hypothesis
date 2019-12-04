/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hypothesis.interfaces.Field;
import org.vaadin.special.ui.SelectButton;

import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SelectPanel extends org.vaadin.special.ui.SelectPanel implements Field, Validatable {

	/**
	 * Is automatic validation enabled.
	 */
	private boolean validationVisible = true;

	/**
	 * The list of validators.
	 */
	private LinkedList<Validator> validators = null;

	public SelectPanel() {
		super();
	}

	@Override
	public void addSelected(SelectButton button) {
		super.addSelected(button);
		if (childList.contains(button) && validators != null && !validators.isEmpty()) {
			markAsDirty();
		}
	}

	@Override
	public void removeSelected(SelectButton button) {
		boolean contained = selectedButtons.contains(button);
		super.removeSelected(button);
		if (contained && validators != null && !validators.isEmpty()) {
			markAsDirty();
		}
	}

	public boolean isSelected(int index) {
		if (index >= 0 && index < childList.size()) {
			return selectedButtons.contains(childList.get(index));
		}
		return false;
	}

	public void setSelected(int index, boolean value) {
		if (index >= 0 && index < childList.size()) {
			if (value) {
				addSelected(childList.get(index));
			} else {
				removeSelected(childList.get(index));
			}
		}
	}

	@Override
	public boolean isValid() {
		try {
			validate();
			return true;
		} catch (InvalidValueException e) {
			return false;
		}
	}

	@Override
	public void addValidator(Validator validator) {
		if (validators == null) {
			validators = new LinkedList<>();
		}
		validators.add(validator);
		markAsDirty();
	}

	@Override
	public void removeValidator(Validator validator) {
		if (validators != null) {
			validators.remove(validator);
		}
		markAsDirty();
	}

	@Override
	public void removeAllValidators() {
		if (validators != null) {
			validators.clear();
		}
		markAsDirty();
	}

	@Override
	public Collection<Validator> getValidators() {
		if (validators == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableCollection(validators);
		}
	}

	@Override
	public void validate() throws InvalidValueException {
		validate(getSelectedButtons());
	}

	protected void validate(Collection<SelectButton> values) throws InvalidValueException {

		List<InvalidValueException> validationExceptions = new ArrayList<>();
		if (validators != null) {
			// Gets all the validation errors
			for (Validator v : validators) {
				try {
					v.validate(values);
				} catch (final InvalidValueException e) {
					validationExceptions.add(e);
				}
			}
		}

		// If there were no errors
		if (validationExceptions.isEmpty()) {
			return;
		}

		// If only one error occurred, throw it forwards
		if (validationExceptions.size() == 1) {
			throw validationExceptions.get(0);
		}

		InvalidValueException[] exceptionArray = validationExceptions
				.toArray(new InvalidValueException[0]);

		// Create a composite validator and include all exceptions
		throw new InvalidValueException(null, exceptionArray);
	}

	@Override
	public ErrorMessage getErrorMessage() {

		/*
		 * Check validation errors only if automatic validation is enabled.
		 * Empty, required fields will generate a validation error containing
		 * the requiredError string. For these fields the exclamation mark will
		 * be hidden but the error must still be sent to the client.
		 */
		InvalidValueException validationError = null;
		if (isValidationVisible()) {
			try {
				validate();
			} catch (InvalidValueException e) {
				if (!e.isInvisible()) {
					validationError = e;
				}
			}
		}

		// Check if there are any systems errors
		final ErrorMessage superError = super.getErrorMessage();

		// Return if there are no errors at all
		if (superError == null && validationError == null) {
			return null;
		}

		// Throw combination of the error types
		return new CompositeErrorMessage(
				superError, AbstractErrorMessage.getErrorMessageForException(validationError));
	}

	public boolean isValidationVisible() {
		return validationVisible;
	}

	public void setValidationVisible(boolean validateAutomatically) {
		if (validationVisible != validateAutomatically) {
			markAsDirty();
			validationVisible = validateAutomatically;
		}
	}

	@Override
	public boolean isInvalidAllowed() {
		return false;
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException {
		// nop
	}

	public boolean hasClickListener() {
		return !clickListeners.isEmpty();
	}

}
