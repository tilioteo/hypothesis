package org.hypothesis.event.interfaces;

public interface EventBus {

	/**
	 * post message object to bus
	 * 
	 * @param event
	 */
	void post(Object event);

	/**
	 * register object to receive messages
	 * 
	 * @param object
	 */
	void register(Object object);

	/**
	 * unregister object to stop receiving messages
	 * 
	 * @param object
	 */
	void unregister(Object object);

}