package org.hypothesis.servlet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BroadcastSingleton {

    private static BroadcastSingleton instance;

    private final ExecutorService executorService;
    private final LinkedList<Broadcaster.Listener> listeners;

    private BroadcastSingleton() {
        executorService = Executors.newSingleThreadExecutor();
        listeners = new LinkedList<>();
    }

    public static BroadcastSingleton instance() {
        if (instance == null) {
            instance = new BroadcastSingleton();
        }

        return instance;
    }

    public final void addListener(Broadcaster.Listener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public final void removeListener(Broadcaster.Listener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public final void broadcast(final String message, final Broadcaster.Listener... withoutListeners) {
        final List<Broadcaster.Listener> excepts = withoutListeners != null ? Stream.of(withoutListeners).collect(Collectors.toList()) : Collections.emptyList();

        listeners.stream()
                .filter(l -> !excepts.contains(l))
                .forEach(l -> executorService.execute(() -> l.receiveBroadcast(message)));
    }
}
