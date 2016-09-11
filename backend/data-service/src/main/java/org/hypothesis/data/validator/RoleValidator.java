/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import java.util.Set;

import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;

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
	private final User loggedUser;
	private final UserService userService;

	public RoleValidator(Object source, User loggedUser) {
		this.source = source;
		this.loggedUser = loggedUser;
		this.userService = UserService.newInstance();
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		// validate only for superusers

		// updating one user, but not the logged one
		if (source instanceof User && !loggedUser.equals((User) source)) {
			return;
		}

		// updating multiple users, but no one is the logged one
		if (source instanceof Set<?> && !((Set<User>) source).contains(loggedUser)) {
			return;
		}

		// superuser left in update
		if (((Set<Role>) value).contains(RoleService.ROLE_SUPERUSER)) {
			return;
		}

		// no superuser left?
		if (!userService.anotherSuperuserExists(loggedUser.getId())) {
			throw new InvalidValueException(Messages.getString("Message.Error.SuperuserLeft"));
		}
	}

}
