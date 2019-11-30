/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.hypothesis.data.model.User;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.server.SessionUtils;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class SessionManager implements Serializable {

    private static final String MAIN_UID = "MainUID";

    private static <T> void setOrClear(Class<T> type, T value) {
        if (value != null) {
            SessionUtils.setAttribute(type, value);
        } else {
            SessionUtils.clearAttribute(type);
        }
    }

    public static User getLoggedUser() {
        return SessionUtils.getAttribute(User.class);
    }

    public static void setLoggedUser(User user) {
        setOrClear(User.class, user);
    }

    public static String getMainUID() {
        return SessionUtils.getStringAttribute(MAIN_UID);
    }

    public static void setMainUID(String uid) {
        SessionUtils.setAttribute(MAIN_UID, uid);
    }

    public static MainEventBus getMainEventBus() {
        return SessionUtils.getAttribute(MainEventBus.class);
    }

    public static void setMainEventBus(MainEventBus mainEventBus) {
        setOrClear(MainEventBus.class, mainEventBus);
    }

    public static ProcessEventBus getProcessEventBus() {
        return SessionUtils.getAttribute(ProcessEventBus.class);
    }

    public static void setProcessEventBus(ProcessEventBus processEventBus) {
        setOrClear(ProcessEventBus.class, processEventBus);
    }

}
