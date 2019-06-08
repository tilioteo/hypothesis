/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import java.util.Set;

import org.hypothesis.data.api.Roles;
import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.UserServiceImpl;
import org.hypothesis.server.Messages;

import com.vaadin.data.Validator;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class RoleValidator implements Validator {

	private final Object source;
	private final SimpleUserDto loggedUser;
	private final UserService userService;

	public RoleValidator(Object source, SimpleUserDto loggedUser) {
		this.source = source;
		this.loggedUser = loggedUser;
		this.userService = new UserServiceImpl();
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		// validate only for superusers

		// updating one user, but not the logged one
		if (source instanceof SimpleUserDto && !((SimpleUserDto) source).getId().equals(loggedUser.getId())) {
			return;
		}

		// updating multiple users, but no one is the logged one
		if (source instanceof Set<?> && !((Set<SimpleUserDto>) source).stream().map(SimpleUserDto::getId)
				.anyMatch(id -> id.equals(loggedUser.getId()))) {
			return;
		}

		// superuser left in update
		if (((Set<RoleDto>) value).stream().map(RoleDto::getName).anyMatch(name -> name.equals(Roles.ROLE_MANAGER))) {
			return;
		}

		// no superuser left?
		if (!userService.anotherSuperuserExists(loggedUser.getId())) {
			throw new InvalidValueException(Messages.getString("Message.Error.SuperuserLeft"));
		}
	}

}
