package com.tilioteo.hypothesis.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.dao.GroupDao;
import com.tilioteo.hypothesis.dao.PackDao;
import com.tilioteo.hypothesis.dao.RoleDao;
import com.tilioteo.hypothesis.dao.UserDao;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Role;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class UserGroupManager {
	public static final Role ROLE_SUPERUSER = initRoleByName("SUPERUSER");
	public static final Role ROLE_MANAGER = initRoleByName("MANAGER");
	public static final Role ROLE_USER = initRoleByName("USER");

	private static Role initRoleByName(String name) {
		RoleDao dao = new RoleDao();
		try {
			dao.beginTransaction();
			Role role = dao.findByNameIgnoreCase(name);
			if (role == null) {
				role = new Role(name.toUpperCase());
				dao.makePersistent(role);
			}
			dao.commit();
			return role;
		} catch (Throwable e) {
			dao.rollback();
		}

		return null;
	}

	private UserDao userDao;
	private GroupDao groupDao;
	private RoleDao roleDao;

	private PackDao packDao;

	public UserGroupManager(UserDao userDao, GroupDao groupDao) {
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.roleDao = new RoleDao();
		this.packDao = new PackDao();
	}

	public Group addGroup(Group group) {
		try {
			groupDao.beginTransaction();
			group = groupDao.makePersistent(group);
			groupDao.commit();
			return group;
		} catch (HibernateException e) {
			groupDao.rollback();
			throw e;
		}
	}

	public void addPack(Pack pack) throws HibernateException {
		try {
			packDao.beginTransaction();
			packDao.makePersistent(pack);
			packDao.commit();
		} catch (HibernateException e) {
			packDao.rollback();
			throw e;
		}
	}

	public Role addRole(Role role) {
		try {
			roleDao.beginTransaction();
			role = roleDao.makePersistent(role);
			roleDao.commit();
			return role;
		} catch (HibernateException e) {
			roleDao.rollback();
			throw e;
		}
	}

	public User addUser(User user) {
		try {
			userDao.beginTransaction();
			user = userDao.makePersistent(user);
			userDao.commit();
			return user;
		} catch (HibernateException e) {
			userDao.rollback();
			throw e;
		}
	}

	// TODO: nejak rozumneji vyhledat primo v databazi
	public boolean anotherSuperuserExists(Long id) {
		for (User user : findAllUsers()) {
			if (user.getRoles().contains(ROLE_SUPERUSER)
					&& !id.equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	public void deleteAllGroups() {
		try {
			List<Group> allGroups = this.findAllGroups();
			for (Group group : allGroups) {
				this.deleteGroup(group);
			}
		} catch (Throwable e) {
		}
	}

	public void deleteAllRoles() {
		try {
			List<Role> allRoles = this.findAllRoles();
			for (Role roles : allRoles) {
				this.deleteRole(roles);
			}
		} catch (Throwable e) {
		}
	}

	public void deleteAllUsers() {
		try {
			List<User> allUsers = this.findAllUsers();
			for (User user : allUsers) {
				this.deleteUser(user);
			}
		} catch (Throwable e) {
		}
	}

	public void deleteGroup(Group group) {
		try {
			groupDao.beginTransaction();
			groupDao.makeTransient(group);
			groupDao.commit();
		} catch (Throwable e) {
			groupDao.rollback();
		}
	}

	public void deleteRole(Role role) {
		try {
			roleDao.beginTransaction();
			roleDao.makeTransient(role);
			roleDao.commit();
		} catch (Throwable e) {
			roleDao.rollback();
		}
	}

	public void deleteUser(User user) {
		try {
			userDao.beginTransaction();
			userDao.makeTransient(user);
			userDao.commit();
		} catch (HibernateException e) {
			userDao.rollback();
			throw e;
		}
	}

	public List<Group> findAllGroups() {
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao.findAll();
			groupDao.commit();
			return allGroups;
		} catch (Throwable e) {
			groupDao.rollback();
		}
		return null;
	}

	public List<String> findAllRoleNames() {
		try {
			List<Role> allRoles = findAllRoles();
			List<String> roleNames = new ArrayList<String>();
			for (Role role : allRoles) {
				roleNames.add(role.getName());
			}
			return roleNames;
		} catch (Throwable e) {
		}
		return null;
	}

	public List<Role> findAllRoles() {
		try {
			roleDao.beginTransaction();
			List<Role> allRoles = roleDao.findAll();
			roleDao.commit();
			return allRoles;
		} catch (Throwable e) {
			roleDao.rollback();
		}
		return null;
	}

	public List<User> findAllUsers() {
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao.findAll();
			userDao.commit();
			return allUsers;
		} catch (Throwable e) {
			userDao.rollback();
		}
		return null;
	}

	public Group findGroup(long id) {
		try {
			groupDao.beginTransaction();
			Group grp = groupDao.findById(Long.valueOf(id), true);
			groupDao.commit();
			return grp;
		} catch (Throwable e) {
			groupDao.rollback();
		}
		return null;
	}

	/*
	 * public void deleteUsers(Collection<Long> ids) { try { Session session =
	 * Util.getSession(); session.beginTransaction(); Query query =
	 * session.createSQLQuery("DELETE FROM tbl_user WHERE id IN (" +
	 * StringUtils.join(ids, ',') + ")"); query.executeUpdate();
	 * session.flush(); session.getTransaction().commit(); } catch
	 * (HibernateException e) { throw e; } }
	 */

	public List<Group> findOwnerGroups(User owner) {
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao.findByCriteria(Restrictions.eq(
					EntityFieldConstants.OWNER_ID, owner.getId()));
			groupDao.commit();
			return allGroups;
		} catch (HibernateException e) {
			groupDao.rollback();
			throw e;
		}
	}

	public List<User> findOwnerUsers(User owner) {
		try {
			userDao.beginTransaction();
			List<User> allUsers = userDao.findByCriteria(Restrictions.eq(
					EntityFieldConstants.OWNER_ID, owner.getId()));
			userDao.commit();
			return allUsers;
		} catch (HibernateException e) {
			userDao.rollback();
			throw e;
		}
	}

	public Role findRoleByName(String roleName) {
		try {
			roleDao.beginTransaction();
			List<Role> roles = roleDao.findByCriteria(Restrictions.eq(
					EntityFieldConstants.NAME, roleName));
			roleDao.commit();
			return (roles.isEmpty() || roles.size() > 1) ? null : roles.get(0);
		} catch (Throwable e) {
			roleDao.rollback();
		}
		return null;
	}

	public User findUser(long id) {
		try {
			userDao.beginTransaction();
			User usr = userDao.findById(Long.valueOf(id), true);
			userDao.commit();
			return usr;
		} catch (Throwable e) {
			userDao.rollback();
		}
		return null;
	}

	public User findUserByUsername(String username) {
		try {
			userDao.beginTransaction();
			List<User> users = userDao.findByCriteria(Restrictions.eq(
					EntityFieldConstants.USERNAME, username));
			userDao.commit();
			return users.get(0);
		} catch (HibernateException e) {
			userDao.rollback();
			throw e;
		}
	}

	public User findUserByUsernamePassword(String username, String password) {
		try {
			userDao.beginTransaction();
			List<User> usrs = userDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityFieldConstants.USERNAME, username),
					Restrictions.eq(EntityFieldConstants.PASSWORD, password)));
			userDao.commit();

			if (usrs.isEmpty() || usrs.size() > 1) {
				return null;
			} else {
				User user = usrs.get(0);
				return user;
			}
		} catch (Throwable e) {
			userDao.rollback();
		}
		return null;
	}

	public Set<User> findUsersByGroups() {
		try {
			Group group = findGroup(7);
			Set<User> users = group.getUsers();
			return users;
		} catch (Throwable e) {
		}
		return null;
	}

	public boolean groupNameExists(Long id, String name) {
		try {
			groupDao.beginTransaction();
			Criterion crit = (id == null) ? Restrictions.eq(
					EntityFieldConstants.NAME, name) : Restrictions.and(
					Restrictions.eq(EntityFieldConstants.NAME, name),
					Restrictions.ne(EntityFieldConstants.ID, id));
			List<Group> groups = groupDao.findByCriteria(crit);
			groupDao.commit();
			return !groups.isEmpty();
		} catch (HibernateException e) {
			groupDao.rollback();
			throw e;
		}
	}

	public boolean usernameExists(Long id, String username) {
		try {
			userDao.beginTransaction();
			Criterion crit = (id == null) ? Restrictions.eq(
					EntityFieldConstants.USERNAME, username) : Restrictions.and(
					Restrictions.eq(EntityFieldConstants.USERNAME, username),
					Restrictions.ne(EntityFieldConstants.ID, id));
			List<User> users = userDao.findByCriteria(crit);
			userDao.commit();
			return !users.isEmpty();
		} catch (HibernateException e) {
			userDao.rollback();
			throw e;
		}
	}

	/*
	 * public UserPermition addUserPermition(UserPermition up) { try {
	 * upDao.beginTransaction(); up = upDao.makePersistent(up); upDao.commit();
	 * return up; } catch (HibernateException e) { upDao.rollback(); throw e; }
	 * }
	 * 
	 * public List<UserPermition> findAllUserPermitions() { try {
	 * upDao.beginTransaction(); List<UserPermition> allUserPermitions =
	 * upDao.findAll(); upDao.commit(); return allUserPermitions; } catch
	 * (Throwable e) {} return null; }
	 * 
	 * public void deleteUserPermition(UserPermition up) { try {
	 * upDao.beginTransaction(); upDao.makeTransient(up); upDao.commit(); }
	 * catch (Throwable e) {} }
	 * 
	 * public void deleteUserPermitions(User user) { try { List<UserPermition>
	 * ups = findAllUserPermitions(); for (UserPermition up : ups) { if
	 * (up.getUser().equals(user)) { deleteUserPermition(up); } } } catch
	 * (Throwable e) {} }
	 * 
	 * public Set<Test> findUsersTests(User user) { Set<Test> tests = new
	 * HashSet<Test>(); try { List<UserPermition> ups = findAllUserPermitions();
	 * for (UserPermition up : ups) { if (up.getUser().equals(user)) {
	 * tests.add(up.getTest()); } } return tests; } catch (Throwable e) {}
	 * return null; }
	 */

}
