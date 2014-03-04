/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.dao.TokenDao;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TokenManager {

	private static final int TOKEN_VALID_TIME = 120 * 1000; // 2 minutes

	private TokenDao tokenDao;
	
	public static TokenManager newInstance() {
		return new TokenManager(new TokenDao());
	}

	public TokenManager(TokenDao tokenDao) {
		this.tokenDao = tokenDao;
	}

	public Token createToken(User user, Pack pack, boolean production) {
		Token token = new Token(user, pack);
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
		try {
			tokenDao.beginTransaction();
			// first purge invalid tokens and then find token by uid
			Date date = new Date();
			date.setTime(date.getTime() - TOKEN_VALID_TIME);
			List<Token> tokens = tokenDao.findByCriteria(Restrictions.lt(
					EntityFieldConstants.DATETIME, date));
			for (Token invalidToken : tokens) {
				tokenDao.makeTransient(invalidToken);
			}

			Token token = tokenDao.findById(uid, true);

			if (token != null) {
				// remove from database
				tokenDao.makeTransient(token);
			}

			tokenDao.commit();

			return token;
		} catch (Throwable e) {
			tokenDao.rollback();
			return null;
		}
	}

	private boolean persistToken(Token token) {
		try {
			tokenDao.beginTransaction();
			tokenDao.makePersistent(token);
			tokenDao.commit();

			return true;
		} catch (Throwable e) {
			tokenDao.rollback();
			return false;
		}
	}

}
