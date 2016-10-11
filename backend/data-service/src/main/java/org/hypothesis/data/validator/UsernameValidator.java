/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import javax.inject.Inject;

import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.server.Messages;

import com.vaadin.data.Validator;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UsernameValidator implements Validator {

	@Inject
	private UserService userService;
	private final Long id;

	public UsernameValidator(Long id) {
		this.id = id;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (userService.usernameExists(id, (String) value)) {
			throw new InvalidValueException(Messages.getString("Message.Error.UsernameExists"));
		}
	}

}
