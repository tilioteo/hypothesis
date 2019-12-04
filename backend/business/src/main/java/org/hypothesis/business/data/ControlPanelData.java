package org.hypothesis.business.data;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlPanelData {
    private final Map<User, UserSessionData> userSessionDataMap = new HashMap<>();
    private final Map<User, List<Pack>> userPacksMap = new HashMap<>();
    private final List<User> users;

    public ControlPanelData(List<User> users) {
        this.users = users;
    }

    public Map<User, UserSessionData> getUserSessionDataMap() {
        return userSessionDataMap;
    }

    public Map<User, List<Pack>> getUserPacksMap() {
        return userPacksMap;
    }

    public List<User> getUsers() {
        return users;
    }
}
