package org.hypothesis.servlet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BroadcastSingleton {

    private static BroadcastSingleton instance;

    //private final MBassador<BroadcastEvent> bus = new MBassador<>();
    private final ExecutorService executorService;
    private final LinkedList<Broadcaster.Listener> listeners;

    private BroadcastSingleton() {
        executorService = Executors.newSingleThreadExecutor();
        listeners = new LinkedList<>();
        //bus.subscribe(this);
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
        //bus.post(new BroadcastEvent(message, withoutListeners)).asynchronously();

        final List<Broadcaster.Listener> excepts = withoutListeners != null ? Stream.of(withoutListeners).collect(Collectors.toList()) : Collections.emptyList();

        CompletableFuture.runAsync(() -> {
            listeners.stream()
                    .filter(l -> !excepts.contains(l))
                    .forEach(l -> executorService.execute(() -> l.receiveBroadcast(message)));
        });
    }

    //@Handler
    //private void processBroadcast(BroadcastEvent event) {
    //   listeners.stream()
    //            .filter(l -> !event.withoutListeners.contains(l))
    //            .forEach(l -> executorService.execute(() -> l.receiveBroadcast(event.message)));
    //}

    //static class BroadcastEvent {
    //    private final String message;
    //    private final List<Broadcaster.Listener> withoutListeners;
    //
    //    BroadcastEvent(final String message, final Broadcaster.Listener... withoutListeners) {
    //        this.message = message;
    //        this.withoutListeners = withoutListeners != null ? Stream.of(withoutListeners).collect(Collectors.toList()) : Collections.emptyList();
    //    }
    //}

}
