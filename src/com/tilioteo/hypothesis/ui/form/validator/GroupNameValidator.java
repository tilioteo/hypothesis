package com.tilioteo.hypothesis.ui.form.validator;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.persistence.GroupManager;
import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class GroupNameValidator implements Validator {
	
	GroupManager groupManager;
	Long id;
	
	public GroupNameValidator(Long id) {
		groupManager = GroupManager.newInstance();
		this.id = id;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (groupManager.groupNameExists(id, (String) value)) {
			throw new InvalidValueException(
					Messages.getString("Message.Error.GroupExists"));
		}
	}

}
