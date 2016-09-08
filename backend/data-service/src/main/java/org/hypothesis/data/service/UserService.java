/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UserService implements Serializable {

	private static final Logger log = Logger.getLogger(UserService.class);

	private final HibernateDao<User, Long> userDao;

	public static UserService newInstance() {
		return new UserService(new HibernateDao<User, Long>(User.class));
	}

	protected UserService(HibernateDao<User, Long> userDao) {
		this.userDao = userDao;
	}

	public User merge(User user) {
		try {
			userDao.beginTransaction();
			user = mergeInit(user);
			userDao.commit();
			return user;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	private User mergeInit(User user) {
		userDao.clear();
		user = userDao.merge(user);
		Hibernate.initialize(user.getGroups());
		Hibernate.initialize(user.getRoles());
		return user;
	}

	public User add(User user) {
		log.debug("addUser");
		try {
			userDao.beginTransaction();
			//user = mergeInit(user);
			user = userDao.merge(user);
			user = userDao.makePersistent(user);
			userDao.commit();
			return user;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	// TODO: nejak rozumneji vyhledat primo v databazi
	public boolean anotherSuperuserExists(Long id) {
		log.debug("anotherSuperuserExists");
		for (User user : findAll()) {
			if (user.hasRole(RoleService.ROLE_SUPERUSER) && !id.equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	public void deleteAll() {
		log.debug("deleteAllUsers");
		try {
			List<User> allUsers = this.findAll();
			for (User user : allUsers) {
				this.delete(user);
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
	}

	public void delete(User user) {
		log.debug("deleteUser");
		try {
			userDao.beginTransaction();
			user = mergeInit(user);
			userDao.makeTransient(user);
			userDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
	}

	public List<User> findAll() {
		log.debug("findAllUsers");
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao.findAll();
			userDao.commit();
			return allUsers;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	public List<User> findOwnerUsers(User owner) {
		log.debug("findOwnerUsers");
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao
					.findByCriteria(Restrictions.eq(FieldConstants.PROPERTY_OWNER_ID, owner.getId()));
			userDao.commit();
			return allUsers;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	public User find(long id) {
		log.debug("findUser");
		try {
			userDao.beginTransaction();
			User usr = userDao.findById(Long.valueOf(id), true);
			userDao.commit();
			return usr;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	public User findByUsername(String username) {
		log.debug("findUserByUsername");
		try {
			userDao.beginTransaction();
			List<User> users = userDao.findByCriteria(Restrictions.eq(FieldConstants.USERNAME, username));
			userDao.commit();
			return users.get(0);
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	public User findByUsernamePassword(String username, String password) {
		log.debug("findUserByUsernamePassword");
		try {
			userDao.beginTransaction();
			List<User> users = userDao
					.findByCriteria(Restrictions.and(Restrictions.eq(FieldConstants.USERNAME, username),
							Restrictions.eq(FieldConstants.PASSWORD, password)));
			userDao.commit();

			if (users.isEmpty() || users.size() > 1) {
				return null;
			} else {
				return users.get(0);
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	public boolean usernameExists(Long id, String username) {
		log.debug("usernameExists");
		try {
			userDao.beginTransaction();
			Criterion crit = (id == null) ? Restrictions.eq(FieldConstants.USERNAME, username)
					: Restrictions.and(Restrictions.eq(FieldConstants.USERNAME, username),
							Restrictions.ne(FieldConstants.ID, id));
			List<User> users = userDao.findByCriteria(crit);
			userDao.commit();
			return !users.isEmpty();
		} catch (Throwable e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return false;
	}

}
