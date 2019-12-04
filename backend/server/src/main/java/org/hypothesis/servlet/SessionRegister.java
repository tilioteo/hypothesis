package org.hypothesis.servlet;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.VaadinSession.State;

public class SessionRegister {

	private static final LinkedList<VaadinSession> sessions = new LinkedList<>();

	public static void register(VaadinSession session) {
		sessions.add(session);
	}

	public static void unregister(VaadinSession session) {
		sessions.remove(session);
	}

	public static List<VaadinSession> getActiveSessions() {
		return sessions.stream()//
				.filter(s -> s.getState() == State.OPEN)//
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

}
