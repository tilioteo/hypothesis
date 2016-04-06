/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.Message;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MessageService implements Serializable {

	private static Logger log = Logger.getLogger(MessageService.class);

	private HibernateDao<Message, String> messageDao;

	public static MessageService newInstance() {
		return new MessageService(new HibernateDao<Message, String>(Message.class));
	}

	protected MessageService(HibernateDao<Message, String> messageDao) {
		this.messageDao = messageDao;
	}

	public Message findMessageByUid(String uid) {
		log.debug(String.format("findMessageByUid: uid = %s", uid != null ? uid : "null"));
		try {
			messageDao.beginTransaction();
			Message message = messageDao.findById(uid, true);
			messageDao.commit();

			return message;
		} catch (Throwable e) {
			log.error(e.getMessage());
			messageDao.rollback();
			return null;
		}
	}

}
