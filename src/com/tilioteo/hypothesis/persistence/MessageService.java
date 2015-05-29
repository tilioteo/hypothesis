/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.dao.MessageDao;
import com.tilioteo.hypothesis.entity.Message;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MessageService implements Serializable {

	private static Logger log = Logger.getLogger(MessageService.class);

	private MessageDao messageDao;
	
	public static MessageService newInstance() {
		return new MessageService(new MessageDao());
	}
	
	protected MessageService(MessageDao messageDao) {
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
