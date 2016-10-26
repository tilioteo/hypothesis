/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import javax.inject.Inject;

import org.hypothesis.data.interfaces.GroupService;
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

	@Inject
	private GroupService groupService;
	private final Long id;

	public GroupNameValidator(Long id) {
		this.id = id;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (groupService.groupNameExists(id, (String) value)) {
			throw new InvalidValueException(Messages.getString("Message.Error.GroupExists"));
		}
	}

}
