/**
 * 
 */
package com.tilioteo.hypothesis.data;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractValidator implements Validator {

	private String message;
	
	public AbstractValidator(String message) {
		this.message = message;
	}
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(message);
		}
	}

}
