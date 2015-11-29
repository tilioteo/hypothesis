package com.tilioteo.hypothesis.data.validator;

import com.tilioteo.hypothesis.data.service.GroupService;
import com.tilioteo.hypothesis.server.Messages;
import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class GroupNameValidator implements Validator {

	private GroupService groupService;
	private Long id;

	public GroupNameValidator(Long id) {
		groupService = GroupService.newInstance();
		this.id = id;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (groupService.groupNameExists(id, (String) value)) {
			throw new InvalidValueException(Messages.getString("Message.Error.GroupExists"));
		}
	}

}
