package org.hypothesis.business.data;

import org.hypothesis.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class UserSessionData {

    private final User user;
    private final Map<String, SessionData> sessions = new HashMap<>();

    public UserSessionData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public SessionData getSessionData(String uid) {
        return sessions.get(uid);
    }

    public void removeSessionData(String uid) {
        sessions.remove(uid);
    }

    public List<SessionData> getSessionData() {
        return new ArrayList<>(sessions.values());
    }

    public void setSessionData(SessionData data) {
        sessions.put(data.getUid(), data);
    }

    public boolean isEmpty() {
        return sessions.isEmpty();
    }

    public UserSessionData withUser(User user) {
        final UserSessionData userSessionData = new UserSessionData(user);
        userSessionData.sessions.putAll(this.sessions);

        return userSessionData;
    }
}
