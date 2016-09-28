/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import java.io.Serializable;

import org.hypothesis.event.interfaces.EventBus;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         A simple wrapper for MBassador event bus. Defines methods for
 *         relevant actions.
 *
 * @param <T>
 *            type of message
 */
@SuppressWarnings("serial")
public abstract class HypothesisEventBus<T> implements Serializable, IPublicationErrorHandler, EventBus {

	private final MBassador<T> eventBus = new MBassador<>(new BusConfiguration()
			.addFeature(Feature.SyncPubSub.Default()).addFeature(Feature.AsynchronousHandlerInvocation.Default())
			.addFeature(Feature.AsynchronousMessageDispatch.Default())
			.setProperty(Properties.Handler.PublicationError, this));

	/* (non-Javadoc)
	 * @see org.hypothesis.eventbus.EventBus#post(T)
	 */
	public synchronized void post(final Object event) {
		eventBus.publish((T)event);
	}

	/* (non-Javadoc)
	 * @see org.hypothesis.eventbus.EventBus#register(java.lang.Object)
	 */
	public synchronized void register(final Object object) {
		eventBus.subscribe(object);
	}

	/* (non-Javadoc)
	 * @see org.hypothesis.eventbus.EventBus#unregister(java.lang.Object)
	 */
	public synchronized void unregister(final Object object) {
		eventBus.unsubscribe(object);
	}

	/* (non-Javadoc)
	 * @see org.hypothesis.eventbus.EventBus#handleError(net.engio.mbassy.bus.error.PublicationError)
	 */
	@Override
	public synchronized void handleError(PublicationError error) {
		// TODO Auto-generated method stub

	}

}
