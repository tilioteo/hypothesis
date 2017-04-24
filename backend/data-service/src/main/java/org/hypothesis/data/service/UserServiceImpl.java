/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import com.vaadin.cdi.NormalUIScoped;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.User;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalUIScoped
public class UserServiceImpl implements UserService {

	private static final Logger log = Logger.getLogger(UserServiceImpl.class);

	private final HibernateDao<User, Long> userDao;

	public UserServiceImpl() {
		System.out.println("Construct " + getClass().getName());
		userDao = new HibernateDao<>(User.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#merge(org.hypothesis.data.model.
	 * User)
	 */
	@Override
	public User merge(User user) {
		try {
			userDao.beginTransaction();
			user = mergeInit(user);
			userDao.commit();
			return user;
		} catch (Exception e) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#add(org.hypothesis.data.model.
	 * User)
	 */
	@Override
	public User add(User user) {
		log.debug("addUser");
		try {
			userDao.beginTransaction();
			// user = mergeInit(user);
			user = userDao.merge(user);
			user = userDao.makePersistent(user);
			userDao.commit();
			return user;
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	// TODO: nejak rozumneji vyhledat primo v databazi
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#anotherSuperuserExists(java.lang.
	 * Long)
	 */
	@Override
	public boolean anotherSuperuserExists(Long id) {
		log.debug("anotherSuperuserExists");
		return findAll().stream().anyMatch(e -> e.hasRole(RoleServiceImpl.ROLE_SUPERUSER) && !id.equals(e.getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.UserService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		log.debug("deleteAllUsers");
		try {
			findAll().forEach(this::delete);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#delete(org.hypothesis.data.model.
	 * User)
	 */
	@Override
	public void delete(User user) {
		log.debug("deleteUser");
		try {
			userDao.beginTransaction();
			user = mergeInit(user);
			userDao.makeTransient(user);
			userDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.UserService#findAll()
	 */
	@Override
	public List<User> findAll() {
		log.debug("findAllUsers");
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao.findAll();
			userDao.commit();
			return allUsers;
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#findOwnerUsers(org.hypothesis.
	 * data.model.User)
	 */
	@Override
	public List<User> findOwnerUsers(User owner) {
		log.debug("findOwnerUsers");
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao
					.findByCriteria(Restrictions.eq(FieldConstants.PROPERTY_OWNER_ID, owner.getId()));
			userDao.commit();
			return allUsers;
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.UserService#find(long)
	 */
	@Override
	public User findById(Long id) {
		log.debug("findUser");
		try {
			userDao.beginTransaction();
			User usr = userDao.findById(id, true);
			userDao.commit();
			return usr;
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#findByUsername(java.lang.String)
	 */
	@Override
	public User findByUsername(String username) {
		log.debug("findUserByUsername");
		try {
			userDao.beginTransaction();
			List<User> users = userDao.findByCriteria(Restrictions.eq(FieldConstants.USERNAME, username));
			userDao.commit();
			return users.get(0);
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#findByUsernamePassword(java.lang.
	 * String, java.lang.String)
	 */
	@Override
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
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.UserService#usernameExists(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
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
		} catch (Exception e) {
			log.error(e.getMessage());
			userDao.rollback();
		}
		return false;
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
	}
}
