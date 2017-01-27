package org.hypothesis.business;

import org.hypothesis.event.data.Message;

public interface MessageManager {

	/**
	 * Create new message object by provided uid
	 * 
	 * @param uid
	 *            message identifier to look for definition
	 * @param userId
	 *            user identifier passed into message
	 * @return new message object or null when message definition not found
	 */
	Message createMessage(String uid, Long userId);

}