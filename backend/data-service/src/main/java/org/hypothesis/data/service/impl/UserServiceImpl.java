package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.api.Roles;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;

public class UserServiceImpl implements UserService {

	private static final Logger log = Logger.getLogger(UserServiceImpl.class);

	private final HibernateDao<User, Long> dao = new HibernateDao<User, Long>(User.class);

	private final UserConverter userConverter = new UserConverter();

	@Override
	public synchronized List<UserDto> findAll() {
		log.debug("findAll");

		try {
			begin();

			List<User> users = dao.findAll();

			final List<UserDto> dtos = users.stream()//
					.filter(Objects::nonNull)//
					.map(userConverter::toDto)//
					.collect(toList());
			commit();

			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized List<UserDto> findOwnerUsers(long userId) {
		log.debug("findOwnerUsers");
		try {
			begin();

			List<User> users = dao.findByCriteria(Restrictions.eq(EntityConstants.OWNER_ID, userId));

			final List<UserDto> dtos = users.stream()//
					.filter(Objects::nonNull)//
					.map(userConverter::toDto)//
					.collect(toList());
			commit();

			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized SimpleUserDto getSimpleById(long userId) {
		log.debug("getSimpleById");

		try {
			begin();

			User user = getByIdInternal(userId);

			final SimpleUserDto dto = userConverter.toSimpleDto(user);
			commit();

			return dto;

		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return null;
	}

	@Override
	public synchronized UserDto getById(long userId) {
		log.debug("getById");

		try {
			begin();

			User user = getByIdInternal(userId);

			final UserDto dto = userConverter.toDto(user);
			commit();

			return dto;

		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return null;
	}

	@Override
	public synchronized SimpleUserDto findByUsernameAndPassword(String username, String password) {
		log.debug("findByUsernameAndPassword");
		try {
			begin();

			List<User> users = dao
					.findByCriteria(Restrictions.and(Restrictions.eq(FieldConstants.USERNAME, username),
							Restrictions.eq(FieldConstants.PASSWORD, password)))
					.stream()//
					.filter(Objects::nonNull)//
					.collect(toList());

			final SimpleUserDto dto = users.isEmpty() ? null : userConverter.toSimpleDto(users.get(0));
			commit();

			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized UserDto findFullByUsernamePassword(String username, String password) {
		log.debug("findFullByUsernamePassword");
		try {
			begin();

			List<User> users = dao
					.findByCriteria(Restrictions.and(Restrictions.eq(FieldConstants.USERNAME, username),
							Restrictions.eq(FieldConstants.PASSWORD, password)))
					.stream()//
					.filter(Objects::nonNull)//
					.collect(toList());

			final UserDto dto = users.isEmpty() || users.size() > 1 ? null : userConverter.toDto(users.get(0));
			commit();

			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized UserDto findByUsername(String username) {
		log.debug("findByUsername");

		try {
			begin();

			List<User> users = dao.findByCriteria(Restrictions.eq(FieldConstants.USERNAME, username)).stream()//
					.filter(Objects::nonNull)//
					.collect(toList());
			final UserDto dto = !users.isEmpty() ? userConverter.toDto(users.get(0)) : null;

			commit();
			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	};

	@Override
	public synchronized boolean anotherSuperuserExists(long userid) {
		log.debug("anotherSuperuserExists");

		try {
			begin();

			Criteria criteria = dao.createCriteria();
			criteria.add(Restrictions.ne(EntityConstants.USER_ID, userid));
			criteria.createAlias("roles", "rolesAlias");
			criteria.add(Restrictions.eq("rolesAlias.name", Roles.ROLE_SUPERUSER));
			boolean exists = !criteria.list().isEmpty();

			commit();
			return exists;

		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return false;
	}

	@Override
	public synchronized void updateUsersTestingSuspendedVN(List<Long> ids, boolean suspend) {
		log.debug("updateUsersTestingSuspendedVN");
		if (ids != null && !ids.isEmpty()) {
			try {
				begin();

				List<User> users = dao.findByCriteria(Restrictions.in(FieldConstants.ID, ids)).stream()//
						.filter(Objects::nonNull)//
						.collect(toList());
				users.forEach(u -> {
					u.setTestingSuspended(suspend);
					dao.makePersistent(u);
				});

				commit();
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<SimpleUserDto> findPlannedUsersVN(Date date) {
		log.debug("findPlannedUsersVN");

		try {
			begin();

			Criteria criteria = dao.createCriteria().add(Restrictions.eq(EntityConstants.TESTING_DATE, date))
					.addOrder(Order.asc(FieldConstants.USERNAME));
			List<User> users = criteria.list();

			final List<SimpleUserDto> dtos = users.stream()//
					.filter(Objects::nonNull)//
					.map(userConverter::toSimpleDto)//
					.collect(toList());
			commit();

			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return emptyList();
	}

	@Override
	public synchronized List<UserDto> findByPasswordAkaBirthNumberVN(String password) {
		log.debug("findByPasswordAkaBirthNumberVN");

		try {
			begin();

			List<User> users = dao.findByCriteria(Restrictions.eq(FieldConstants.PASSWORD, password));

			final List<UserDto> dtos = users.stream()//
					.filter(Objects::nonNull)//
					.map(userConverter::toDto)//
					.collect(toList());
			commit();

			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return emptyList();
	}

	@Override
	public synchronized UserDto save(UserDto user) {
		log.debug("save");

		if (user != null) {
			try {
				begin();

				User entity = new User();
				userConverter.fillEntity(user, entity, true);

				dao.makePersistent(entity);
				final UserDto dto = userConverter.toDto(entity);

				commit();
				return dto;
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}

		return null;
	}

	@Override
	public synchronized boolean delete(SimpleUserDto user) {
		log.debug("delete");

		if (user != null && user.getId() != null) {

			try {
				begin();

				User usr = dao.findById(user.getId(), true);
				dao.makeTransient(usr);

				commit();
				return true;
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}

		return false;
	}

	@Override
	public synchronized boolean usernameExists(Long id, String username) {
		log.debug("usernameExists");

		try {
			begin();

			Criterion crit = (id == null) ? Restrictions.eq(FieldConstants.USERNAME, username)
					: Restrictions.and(Restrictions.eq(FieldConstants.USERNAME, username),
							Restrictions.ne(FieldConstants.ID, id));

			List<User> users = dao.findByCriteria(crit).stream()//
					.filter(Objects::nonNull)//
					.collect(toList());

			commit();
			return !users.isEmpty();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return false;
	}

	private User getByIdInternal(long userId) {
		User user = dao.findById(Long.valueOf(userId), false);

		if (user == null) {
			throw new EntityNotFoundException("user id=" + userId);
		}

		return user;
	};

	UserDto getDtoByIdInternal(long userId) {
		return userConverter.toDto(getByIdInternal(userId));
	}

}
