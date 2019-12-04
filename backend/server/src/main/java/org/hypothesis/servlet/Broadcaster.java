package org.hypothesis.servlet;

public interface Broadcaster {

    default void registerBroadcasterListener(Listener listener) {
        BroadcastSingleton.instance().addListener(listener);
    }

    default void listenBroadcasting() {
        if (this instanceof Listener) {
            BroadcastSingleton.instance().addListener((Listener) this);
        } else {
            throw new IllegalStateException("Class " + this.getClass().getName() + " is not descendant of " + Listener.class.getName());
        }
    }

    default void unregisterBroadcasterListener(Listener listener) {
        BroadcastSingleton.instance().removeListener(listener);
    }

    default void unlistenBroadcasting() {
        if (this instanceof Listener) {
            BroadcastSingleton.instance().removeListener((Listener) this);
        } else {
            throw new IllegalStateException("Class " + this.getClass().getName() + " is not descendant of " + Listener.class.getName());
        }
    }

    default void broadcast(final String message) {
        BroadcastSingleton.instance().broadcast(message, (Listener[]) null);
    }

    default void broadcastOthers(final String message) {
        BroadcastSingleton.instance().broadcast(message, (this instanceof Listener) ? (Listener) this : null);
    }

    interface Listener {
        void receiveBroadcast(final String message);
    }

}
