package org.hypothesis.business;

import org.hypothesis.business.data.ControlPanelData;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.UserService;

import java.util.Date;
import java.util.List;

public class ControlPanelDataManager {

    private final UserService userService = UserService.newInstance();
    private final PermissionService permissionService = PermissionService.newInstance();

    public ControlPanelData getControlPanelData(Date date) {
        final List<User> users = userService.findPlannedUsers(date);

        return getControlPanelData(users);
    }

    public ControlPanelData getControlPanelData(List<User> users) {
        final ControlPanelData data = new ControlPanelData(users);
        users.forEach(u -> {
            data.getUserPacksMap().put(u, permissionService.getUserPacksVN(u));
            data.getUserSessionDataMap().put(u, UserSessionManager.ensureUserSessionData(u));
        });

        return data;
    }
}
