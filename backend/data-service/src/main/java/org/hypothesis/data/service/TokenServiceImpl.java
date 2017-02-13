/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.TokenService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;

import javax.enterprise.inject.Default;
import java.util.Date;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class TokenServiceImpl implements TokenService {

	private static final Logger log = Logger.getLogger(TokenServiceImpl.class);

	private static final int TOKEN_VALID_TIME = 120 * 1000; // 2 minutes

	private final HibernateDao<Token, String> tokenDao;

	public TokenServiceImpl() {
		tokenDao = new HibernateDao<>(Token.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TokenService#createToken(org.hypothesis.data.
	 * model.User, org.hypothesis.data.model.Pack, java.lang.String, boolean)
	 */
	@Override
	public Token createToken(User user, Pack pack, String viewUid, boolean production) {
		Token token = new Token(user, pack, viewUid);
		// TODO test if user can run non-production test (only MANAGER and
		// SUPERUSER can)
		token.setProduction(production);

		if (persistToken(token)) {
			return token;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TokenService#findTokenByUid(java.lang.String)
	 */
	@Override
	public Token findById(String uid) {
		log.debug("findTokenByUid");
		Token token = null;
		try {
			// first purge invalid tokens and then find token by uid
			Date date = new Date();
			date.setTime(date.getTime() - TOKEN_VALID_TIME);

			tokenDao.beginTransaction();
			tokenDao.findByCriteria(Restrictions.lt(FieldConstants.DATETIME, date)).forEach(tokenDao::makeTransient);
			tokenDao.commit();
		} catch (Exception e) {
			log.error("purge of invalid tokens failed");
			log.error(e.getMessage());
			tokenDao.rollback();
		}

		try {
			tokenDao.beginTransaction();
			token = tokenDao.findById(uid, true);
			tokenDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			tokenDao.rollback();
		}

		if (token != null) {
			// remove from database
			try {
				tokenDao.beginTransaction();
				tokenDao.makeTransient(token);
				tokenDao.commit();
			} catch (Exception e) {
				log.error(e.getMessage());
				tokenDao.rollback();
			}
		}
		
		return token;
	}

	private boolean persistToken(Token token) {
		log.debug("persistToken");
		try {
			tokenDao.beginTransaction();
			tokenDao.makePersistent(token);
			tokenDao.commit();
			tokenDao.flush();

			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			tokenDao.rollback();
			return false;
		}
	}

}
