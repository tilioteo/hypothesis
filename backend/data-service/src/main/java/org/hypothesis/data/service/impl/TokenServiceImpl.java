package org.hypothesis.data.service.impl;

import static java.util.UUID.randomUUID;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.TokenDto;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.data.service.UserService;

public class TokenServiceImpl implements TokenService {

	private static final Logger log = Logger.getLogger(TokenServiceImpl.class);

	private static final int TOKEN_VALID_TIME = 120 * 1000; // 2 minutes

	private final HibernateDao<Token, String> dao = new HibernateDao<Token, String>(Token.class);

	private final UserService userService = new UserServiceImpl();

	@Override
	public synchronized TokenDto create(long packId, Long userId, String viewUid, boolean production) {
		log.debug("create");

		Token token = new Token();
		token.setPackId(packId);
		token.setUserId(userId);
		token.setViewUid(viewUid);

		// TODO test if user can run non-production test (only MANAGER and
		// SUPERUSER can)
		token.setProduction(production);

		token.setId(randomUUID().toString().replaceAll("-", ""));
		token.setDatetime(new Date());

		try {
			begin();
			dao.makePersistent(token);

			final TokenDto dto = toDto(token);

			commit();
			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized TokenDto findById(String id) {
		log.debug("findById");
		try {
			// first purge invalid tokens and then find token by uid
			Date date = new Date();
			date.setTime(date.getTime() - TOKEN_VALID_TIME);

			begin();
			dao.findByCriteria(Restrictions.lt(FieldConstants.DATETIME, date)).forEach(dao::makeTransient);
			commit();
		} catch (Throwable e) {
			log.error("purge of invalid tokens failed");
			log.error(e.getMessage());
			rollback();
		}

		try {
			begin();
			Token token = dao.findById(id, false);

			if (token != null) {
				// remove from database
				dao.makeTransient(token);
			}

			final TokenDto dto = toDto(token);

			commit();
			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	private TokenDto toDto(Token entity) {
		final TokenDto dto = new TokenDto();
		dto.setId(entity.getId());
		dto.setPackId(entity.getPackId());
		dto.setViewUid(entity.getViewUid());
		dto.setProduction(entity.isProduction());

		dto.setUser(entity.getUserId() != null ? userService.getSimpleById(entity.getUserId()) : null);

		return dto;
	}

}
