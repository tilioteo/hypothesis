/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import org.hypothesis.data.service.GroupService;
import org.hypothesis.data.service.impl.GroupServiceImpl;
import org.hypothesis.server.Messages;

import com.vaadin.data.Validator;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class GroupNameValidator implements Validator {

	private final GroupService groupService;
	private final Long id;

	public GroupNameValidator(Long id) {
		groupService = new GroupServiceImpl();
		this.id = id;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (groupService.groupNameExists(id, (String) value)) {
			throw new InvalidValueException(Messages.getString("Message.Error.GroupExists"));
		}
	}

}
