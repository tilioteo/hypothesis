/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Role;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class RoleService implements Serializable {

	private static final Logger log = Logger.getLogger(RoleService.class);

	private final HibernateDao<Role, Long> roleDao;

	public static final Role ROLE_SUPERUSER = initRoleByName("SUPERUSER");
	public static final Role ROLE_MANAGER = initRoleByName("MANAGER");
	public static final Role ROLE_USER = initRoleByName("USER");

	protected RoleService(HibernateDao<Role, Long> roleDao) {
		this.roleDao = new HibernateDao<Role, Long>(Role.class);
	}

	public static RoleService newInstance() {
		return new RoleService(new HibernateDao<Role, Long>(Role.class));
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

	public void deleteAll() {
		log.debug("deleteAllRoles");
		try {
			this.findAll().forEach(this::delete);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

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

	public List<String> findAllRoleNames() {
		log.debug("findAllRoleNames");
		try {
			return findAll().stream().map(Role::getName).collect(Collectors.toList());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

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