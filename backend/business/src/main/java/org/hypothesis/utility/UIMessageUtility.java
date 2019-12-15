package org.hypothesis.utility;

import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.event.data.UIMessage;

import java.util.List;
import java.util.Set;

import static org.hypothesis.data.service.RoleService.ROLE_MANAGER;
import static org.hypothesis.data.service.RoleService.ROLE_SUPERUSER;
import static org.hypothesis.presenter.BroadcastMessages.*;

public class UIMessageUtility {

    public static boolean canHandle(UIMessage message, User loggedUser, String mainViewUid) {
        if (message != null) {
            String viewUid = message.getViewUid();
            Long groupId = message.getGroupId();
            Long userId = message.getUserId();
            List<String> roles = message.getRoles();

            if (null == viewUid && null == groupId && null == userId && roles.isEmpty()) {
                // non addressed broadcast message
                return true;

            } else {
                return (loggedUser != null && // addressed message, user must be logged
                        ((groupId != null && groupMatches(groupId, loggedUser.getGroups()))
                                || (userId != null && loggedUser.getId().equals(userId))
                                || (!roles.isEmpty() && rolesMatch(roles, loggedUser.getRoles()))))
                        ||
                        (mainViewUid != null && mainViewUid.equals(viewUid)); // addressed message for view with uid
            }
        }

        return false;
    }

    // TODO: replace by lambda
    private static boolean groupMatches(Long groupId, Set<Group> groups) {
        for (Group group : groups) {
            if (group.getId().equals(groupId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean rolesMatch(List<String> roles, Set<Role> roles2) {
        for (Role role : roles2) {
            if (roles.contains(role.getName())) {
                return true;
            }
        }
        // TODO Auto-generated method stub
        return false;
    }

    public static String createRefreshUserTestStateMessage(Long userId) {
        UIMessage message = new UIMessage(REFRESH_USER_TEST_STATE);
        message.setRoles(ROLE_MANAGER.getName(), ROLE_SUPERUSER.getName());
        message.setSenderId(userId);

        return message.toString();
    }

    public static String createRefreshUserPacksViewMessage(Long userId) {
        UIMessage message = new UIMessage(REFRESH_PACKS);
        message.setUserId(userId);

        return message.toString();
    }

    public static String createProcessViewClosedMessage(String mainViewUid, Long packId, Long userId) {
        UIMessage message = new UIMessage(PROCESS_VIEW_CLOSED);
        message.setViewUid(mainViewUid);
        message.setPackId(packId);
        message.setUserId(userId);

        return message.toString();
    }

    public static String createPreparedTestMessage(String mainViewUid, Long packId, Long userId) {
        UIMessage message = new UIMessage(PREPARED_TEST);
        message.setViewUid(mainViewUid);
        message.setPackId(packId);
        message.setUserId(userId);

        return message.toString();
    }

    public static String createFinishedTestMessage(String mainViewUid, Long packId, Long userId) {
        UIMessage message = new UIMessage(FINISHED_TEST);
        message.setViewUid(mainViewUid);
        message.setPackId(packId);
        message.setUserId(userId);

        return message.toString();
    }

}
