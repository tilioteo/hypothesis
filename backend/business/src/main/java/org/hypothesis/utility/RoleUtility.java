/**
 * 
 */
package org.hypothesis.utility;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hypothesis.data.model.Role;
import org.hypothesis.interfaces.RoleType;

/**
 * @author kamil
 *
 */
public class RoleUtility {

	private RoleUtility() {
	}

	public static boolean isAnyRoleAllowed(List<RoleType> roleTypes, Set<Role> roles) {
		return roles.stream()
				.anyMatch(e -> roleTypes.stream().map(r -> r.name()).collect(Collectors.toSet()).contains(e.getName()));
	}
}
