package com.tilioteo.hypothesis.event;

import java.io.Serializable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

/**
 * A simple wrapper for MBassador event bus. Defines methods for relevant actions.
 */
@SuppressWarnings("serial")
public class HypothesisEventBus<T> implements Serializable, IPublicationErrorHandler {

    private final MBassador<T> eventBus = new MBassador<T>(new BusConfiguration().
    		addFeature(Feature.SyncPubSub.Default()).
			addFeature(Feature.AsynchronousHandlerInvocation.Default()).
			addFeature(Feature.AsynchronousMessageDispatch.Default()).
			setProperty(Properties.Handler.PublicationError, this));    
    
    public void post(final T event) {
        eventBus.publish(event);
    }

    public void register(final Object object) {
    	eventBus.subscribe(object);
    }

    public void unregister(final Object object) {
    	eventBus.unsubscribe(object);
    }

	@Override
	public void handleError(PublicationError error) {
		// TODO Auto-generated method stub
		
	}
    
}
