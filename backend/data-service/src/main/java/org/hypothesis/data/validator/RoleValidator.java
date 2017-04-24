/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.validator;

import com.vaadin.data.Validator;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleServiceImpl;
import org.hypothesis.server.Messages;

import javax.inject.Inject;
import java.util.Set;

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

	@Inject
	private UserService userService;

	public RoleValidator(Object source, User loggedUser) {
		this.source = source;
		this.loggedUser = loggedUser;
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
		if (((Set<Role>) value).contains(RoleServiceImpl.ROLE_SUPERUSER)) {
			return;
		}

		// no superuser left?
		if (!userService.anotherSuperuserExists(loggedUser.getId())) {
			throw new InvalidValueException(Messages.getString("Message.Error.SuperuserLeft"));
		}
	}

}
