package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.MessageDto;
import org.hypothesis.data.model.Message;
import org.hypothesis.data.service.MessageService;

public class MessageServiceImpl implements MessageService {

	private static final Logger log = Logger.getLogger(MessageServiceImpl.class);

	private final HibernateDao<Message, String> dao = new HibernateDao<Message, String>(Message.class);

	@Override
	public MessageDto findById(String id) {
		log.debug(String.format("findById: id = %s", id != null ? id : "null"));

		try {
			begin();

			Message message = dao.findById(id, false);
			final MessageDto dto = toDto(message);

			commit();
			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	private MessageDto toDto(Message entity) {
		if (entity == null) {
			return null;
		}

		final MessageDto dto = new MessageDto();

		dto.setId(entity.getId());
		dto.setData(entity.getData());
		dto.setNote(entity.getNote());

		return dto;
	}

}
