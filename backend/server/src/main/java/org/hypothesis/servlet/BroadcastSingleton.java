package org.hypothesis.servlet;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;
import org.mpilone.vaadin.uitask.UIAccessor;
import org.mpilone.vaadin.uitask.UITask;

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
        /*final List<Broadcaster.Listener> excepts = withoutListeners != null ? Stream.of(withoutListeners).collect(Collectors.toList()) : Collections.emptyList();

        listeners.stream()
                .filter(l -> !excepts.contains(l))
                .forEach(l -> executorService.execute(() -> l.receiveBroadcast(message)));
        */

        BroadcastTask task = new BroadcastTask(new UIAccessor.Current(), message, withoutListeners);
        executorService.execute(task);
    }

    class BroadcastTask extends UITask<Void> {
        final String message;
        final List<Broadcaster.Listener> withoutListeners;

        public BroadcastTask(final UIAccessor uiAccessor, final String message, final Broadcaster.Listener... withoutListeners) {
            super(uiAccessor);
            this.message = message;
            this.withoutListeners = withoutListeners != null ? Stream.of(withoutListeners).collect(Collectors.toList()) : Collections.emptyList();
        }

        @Override
        protected Void runInBackground() {
            listeners.stream()
                    .filter(l -> !withoutListeners.contains(l))
                    .forEach(l -> {
                        l.receiveBroadcast(message);
                        forceManualPush();
                    });

            return null;
        }

        void forceManualPush() {
            final UI ui = UI.getCurrent();
            if (ui != null && ui.getPushConfiguration().getPushMode() == PushMode.MANUAL) {
                try {
                    ui.push();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
