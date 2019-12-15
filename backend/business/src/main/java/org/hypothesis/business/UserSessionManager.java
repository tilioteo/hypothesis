package org.hypothesis.business;

import org.hypothesis.business.data.SessionData;
import org.hypothesis.business.data.UserSessionData;
import org.hypothesis.data.model.User;

public class UserSessionManager {

    public static synchronized UserSessionData ensureUserSessionData(final User user, final String sessionId) {
        final UserSessionData userSessionData = ensureUserSessionData(user);
        ensureSessionData(userSessionData, sessionId);

        return userSessionData;
    }

    public static synchronized UserSessionData ensureUserSessionData(final User user) {
        UserSessionData userSessionData = UserSessionRegisterSingleton.instance().getUserSessionData(user.getId());
        if (null == userSessionData) {
            userSessionData = new UserSessionData(user);
        } else {
            userSessionData = userSessionData.withUser(user);
        }
        UserSessionRegisterSingleton.instance().addUserSessionData(userSessionData);
        return userSessionData;
    }

    private static SessionData ensureSessionData(final UserSessionData userSessionData, final String sessionId) {
        SessionData data = userSessionData.getSessionData(sessionId);
        if (null == data) {
            data = new SessionData(sessionId);
            userSessionData.setSessionData(data);
        }
        return data;
    }

    public static synchronized void purgeUserSessionData(final long userId, final String sessionId) {
        UserSessionData userSessionData = UserSessionRegisterSingleton.instance().getUserSessionData(userId);
        if (userSessionData != null) {
            userSessionData.removeSessionData(sessionId);
            if (userSessionData.isEmpty()) {
                UserSessionRegisterSingleton.instance().removeUserSessionData(userId);
            }
        }
    }
}
