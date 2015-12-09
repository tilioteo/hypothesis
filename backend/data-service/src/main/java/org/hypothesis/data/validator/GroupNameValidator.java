package org.hypothesis.data.validator;

import org.hypothesis.data.service.GroupService;

import org.hypothesis.server.Messages;
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
