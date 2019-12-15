package org.hypothesis.business.data;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlPanelData {
    private final Map<Long, UserSessionData> userSessionDataMap = new HashMap<>();
    private final Map<Long, List<Pack>> userPacksMap = new HashMap<>();
    private final List<User> users;

    public ControlPanelData(List<User> users) {
        this.users = users;
    }

    public Map<Long, UserSessionData> getUserSessionDataMap() {
        return userSessionDataMap;
    }

    public Map<Long, List<Pack>> getUserPacksMap() {
        return userPacksMap;
    }

    public List<User> getUsers() {
        return users;
    }
}
