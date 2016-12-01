/**
 * 
 */
package org.hypothesis.utility;

import org.hypothesis.data.model.Role;
import org.hypothesis.interfaces.RoleType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author kamil
 *
 */
public final class RoleUtility {

	private RoleUtility() {
	}

	public static boolean isAnyRoleAllowed(List<RoleType> roleTypes, Set<Role> roles) {
		return roles.stream()
				.anyMatch(e -> roleTypes.stream().map(Enum::name).collect(Collectors.toSet()).contains(e.getName()));
	}
}
