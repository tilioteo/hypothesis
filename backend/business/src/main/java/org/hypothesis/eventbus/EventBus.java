/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import net.engio.mbassy.bus.MBassador;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 * <p>
 * A simple wrapper for MBassador event bus. Defines methods for
 * relevant actions.
 */
@SuppressWarnings("serial")
public abstract class EventBus<T> implements Serializable {

    private final MBassador<T> eventBus = new MBassador<>(/*new BusConfiguration()
			.addFeature(Feature.SyncPubSub.Default()).addFeature(Feature.AsynchronousHandlerInvocation.Default())
			.addFeature(Feature.AsynchronousMessageDispatch.Default())
			.setProperty(Properties.Handler.PublicationError, this)*/);

    public void post(final T event) {
        eventBus.post(event).now();
    }

    public synchronized void register(final Object object) {
        eventBus.subscribe(object);
    }

    public synchronized void unregister(final Object object) {
        eventBus.unsubscribe(object);
    }

}
