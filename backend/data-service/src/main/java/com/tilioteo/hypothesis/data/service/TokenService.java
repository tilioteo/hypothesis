/**
 * 
 */
package com.tilioteo.hypothesis.data.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.data.model.FieldConstants;
import com.tilioteo.hypothesis.data.model.Pack;
import com.tilioteo.hypothesis.data.model.Token;
import com.tilioteo.hypothesis.data.model.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class TokenService implements Serializable {

	private static Logger log = Logger.getLogger(TokenService.class);

	private static final int TOKEN_VALID_TIME = 120 * 1000; // 2 minutes

	private HibernateDao<Token, String> tokenDao;
	
	public static TokenService newInstance() {
		return new TokenService(new HibernateDao<Token, String>(Token.class));
	}

	public TokenService(HibernateDao<Token, String> tokenDao) {
		this.tokenDao = tokenDao;
	}

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

	public Token findTokenByUid(String uid) {
		log.debug("findTokenByUid");
		try {
			// first purge invalid tokens and then find token by uid
			Date date = new Date();
			date.setTime(date.getTime() - TOKEN_VALID_TIME);
			
			tokenDao.beginTransaction();
			List<Token> tokens = tokenDao.findByCriteria(Restrictions.lt(
					FieldConstants.DATETIME, date));
			for (Token invalidToken : tokens) {
				tokenDao.makeTransient(invalidToken);
			}
			tokenDao.commit();
		} catch (Throwable e) {
			log.error("purge of invalid tokens failed");
			log.error(e.getMessage());
			tokenDao.rollback();
		}
		
		try {
			tokenDao.beginTransaction();
			Token token = tokenDao.findById(uid, true);

			if (token != null) {
				// remove from database
				tokenDao.makeTransient(token);
			}

			tokenDao.commit();

			return token;
		} catch (Throwable e) {
			log.error(e.getMessage());
			tokenDao.rollback();
			return null;
		}
	}

	private boolean persistToken(Token token) {
		log.debug("persistToken");
		try {
			tokenDao.beginTransaction();
			tokenDao.makePersistent(token);
			tokenDao.commit();

			return true;
		} catch (Throwable e) {
			log.error(e.getMessage());
			tokenDao.rollback();
			return false;
		}
	}

}
