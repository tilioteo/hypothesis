/**
 * 
 */
package com.tilioteo.hypothesis.data.service;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.data.model.Message;

/**
 * @author kamil
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
