package com.tilioteo.hypothesis.ui.form.validator;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.persistence.UserManager;
import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class UsernameValidator implements Validator {

	UserManager userManager;
	Long id;
	
	public UsernameValidator(Long id) {
		this.id = id;
		userManager = UserManager.newInstance();
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (userManager.usernameExists(id, (String) value)) {
			throw new InvalidValueException(
					Messages.getString("Message.Error.UsernameExists"));
		}
	}

}
