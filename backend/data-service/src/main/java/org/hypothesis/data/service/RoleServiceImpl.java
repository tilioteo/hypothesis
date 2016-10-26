/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.Default;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.RoleService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Role;
import org.hypothesis.interfaces.RoleType;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class RoleServiceImpl implements RoleService {

	private static final Logger log = Logger.getLogger(RoleServiceImpl.class);

	private final HibernateDao<Role, Long> roleDao;

	public static final Role ROLE_SUPERUSER = initRoleByName(RoleType.SUPERUSER.name());
	public static final Role ROLE_MANAGER = initRoleByName(RoleType.MANAGER.name());
	public static final Role ROLE_USER = initRoleByName(RoleType.USER.name());

	public RoleServiceImpl() {
		roleDao = new HibernateDao<>(Role.class);
	}

	private static Role initRoleByName(String name) {
		log.debug(String.format("initRoleByName: name = %s", name != null ? name : "NULL"));
		HibernateDao<Role, Long> roleDao = new HibernateDao<Role, Long>(Role.class);
		try {
			roleDao.beginTransaction();
			List<Role> roles = roleDao.findByCriteria(Restrictions.eq(FieldConstants.NAME, name).ignoreCase());
			Role role = null;
			if (!roles.isEmpty()) {
				role = roles.get(0);
			} else if (name != null) {
				role = new Role(name.toUpperCase());
				roleDao.makePersistent(role);
			}
			roleDao.commit();
			return role;
		} catch (Exception e) {
			log.error(e.getMessage());
			roleDao.rollback();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.RoleService#add(org.hypothesis.data.model.
	 * Role)
	 */
	@Override
	public Role add(Role role) {
		log.debug("addRole");
		try {
			roleDao.beginTransaction();
			role = roleDao.makePersistent(role);
			roleDao.commit();
			return role;
		} catch (Exception e) {
			log.error(e.getMessage());
			roleDao.rollback();
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.RoleService#delete(org.hypothesis.data.model.
	 * Role)
	 */
	@Override
	public void delete(Role role) {
		log.debug("deleteRole");
		try {
			roleDao.beginTransaction();
			roleDao.makeTransient(role);
			roleDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			roleDao.rollback();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.RoleService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		log.debug("deleteAllRoles");
		try {
			this.findAll().forEach(this::delete);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.RoleService#findAll()
	 */
	@Override
	public List<Role> findAll() {
		log.debug("findAllRoles");
		try {
			roleDao.beginTransaction();
			List<Role> allRoles = roleDao.findAll();
			roleDao.commit();
			return allRoles;
		} catch (Exception e) {
			log.error(e.getMessage());
			roleDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.RoleService#findAllRoleNames()
	 */
	@Override
	public List<String> findAllRoleNames() {
		log.debug("findAllRoleNames");
		try {
			return findAll().stream().map(Role::getName).collect(Collectors.toList());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.RoleService#findRoleByName(java.lang.String)
	 */
	@Override
	public Role findRoleByName(String roleName) {
		log.debug("findRoleByName");
		try {
			roleDao.beginTransaction();
			List<Role> roles = roleDao.findByCriteria(Restrictions.eq(FieldConstants.NAME, roleName));
			roleDao.commit();
			return (roles.isEmpty() || roles.size() > 1) ? null : roles.get(0);
		} catch (Exception e) {
			log.error(e.getMessage());
			roleDao.rollback();
		}
		return null;
	}

}
