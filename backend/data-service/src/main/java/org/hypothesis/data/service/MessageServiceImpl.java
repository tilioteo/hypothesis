/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import javax.enterprise.inject.Default;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.MessageService;
import org.hypothesis.data.model.Message;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class MessageServiceImpl implements MessageService {

	private static final Logger log = Logger.getLogger(MessageServiceImpl.class);

	private final HibernateDao<Message, String> messageDao;

	public MessageServiceImpl() {
		messageDao = new HibernateDao<>(Message.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.MessageService#findMessageByUid(java.lang.
	 * String)
	 */
	@Override
	public Message findMessageByUid(String uid) {
		log.debug(String.format("findMessageByUid: uid = %s", uid != null ? uid : "null"));
		try {
			messageDao.beginTransaction();
			Message message = messageDao.findById(uid, true);
			messageDao.commit();

			return message;
		} catch (Exception e) {
			log.error(e.getMessage());
			messageDao.rollback();
			return null;
		}
	}

}
