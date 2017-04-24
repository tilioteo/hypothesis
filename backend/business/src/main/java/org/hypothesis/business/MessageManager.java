/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.hypothesis.event.data.Message;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
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