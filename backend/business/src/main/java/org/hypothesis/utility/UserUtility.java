package org.hypothesis.utility;

import java.util.Arrays;
import java.util.List;

import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.dto.SimpleUserDto;

public class UserUtility {

	public static boolean userHasAnyRole(SimpleUserDto user, String... roles) {
		final List<String> roleList = Arrays.asList(roles);
		return user.getRoles().stream().map(RoleDto::getName).anyMatch(name -> roleList.contains(name));
	}

}
