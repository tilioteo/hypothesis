package org.hypothesis.business;

import org.hypothesis.business.data.UserSessionData;
import org.hypothesis.data.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserSessionRegisterSingleton {

    private static UserSessionRegisterSingleton instance;

    private final Map<Long, UserSessionData> userSessionDataMap;

    private UserSessionRegisterSingleton() {
        userSessionDataMap = new HashMap<>();
    }

    public static UserSessionRegisterSingleton instance() {
        if (instance == null) {
            instance = new UserSessionRegisterSingleton();
        }

        return instance;
    }

    public void addUserSessionData(UserSessionData data) {
        userSessionDataMap.put(data.getUser().getId(), data);
    }

    public UserSessionData getUserSessionData(long userId) {
        return userSessionDataMap.get(userId);
    }

    public void removeUserSessionData(long userId) {
        userSessionDataMap.remove(userId);
    }
}
