package org.hypothesis.utility;

import static org.hypothesis.data.api.Roles.ROLE_MANAGER;
import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_PACKS;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_USER_TEST_STATE;

import java.util.List;
import java.util.Set;

import org.hypothesis.data.dto.GroupDto;
import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.event.data.UIMessage;

public class UIMessageUtility {

	public static boolean canHandle(UIMessage message, SimpleUserDto loggedUser) {
		if (message != null) {
			Long groupId = message.getGroupId();
			Long userId = message.getUserId();
			List<String> roles = message.getRoles();

			if (null == groupId && null == userId && roles.isEmpty()) {
				// non addressed broadcast message
				return true;

			} else if (loggedUser != null // addressed message, user must be
											// logged
					&& ((groupId != null && groupMatches(groupId, loggedUser.getGroups()))
							|| (userId != null && loggedUser.getId().equals(userId))
							|| (!roles.isEmpty() && rolesMatch(roles, loggedUser.getRoles())))) {
				return true;
			}
		}

		return false;
	}

	private static boolean groupMatches(Long groupId, Set<GroupDto> groups) {
		return groups.stream().map(GroupDto::getId).anyMatch(id -> id.equals(groupId));
	}

	private static boolean rolesMatch(List<String> roles, Set<RoleDto> roleDtos) {
		return roleDtos.stream().map(RoleDto::getName).anyMatch(name -> roles.contains(name));
	}

	public static String createRefreshUserTestStateMessage(Long userId) {
		UIMessage message = new UIMessage(REFRESH_USER_TEST_STATE);
		message.setRoles(ROLE_MANAGER, ROLE_SUPERUSER);
		message.setSenderId(userId);

		return message.toString();
	}

	public static String createRefreshUserPacksViewMessage(Long userId) {
		UIMessage message = new UIMessage(REFRESH_PACKS);
		message.setUserId(userId);

		return message.toString();
	}

}
