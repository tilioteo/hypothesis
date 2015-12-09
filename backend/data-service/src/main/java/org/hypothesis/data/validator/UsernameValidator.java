package org.hypothesis.data.validator;

import org.hypothesis.data.service.UserService;

import org.hypothesis.server.Messages;
import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class UsernameValidator implements Validator {

	private UserService userService;
	private Long id;

	public UsernameValidator(Long id) {
		this.id = id;
		userService = UserService.newInstance();
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (userService.usernameExists(id, (String) value)) {
			throw new InvalidValueException(Messages.getString("Message.Error.UsernameExists"));
		}
	}

}
