/**
 * 
 */
package org.hypothesis.persistence;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.GroupPermission;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Test;
import org.hypothesis.entity.User;
import org.hypothesis.entity.UserPermission;
import org.hypothesis.entity.Test.Status;
import org.hypothesis.persistence.hibernate.GroupPermissionDao;
import org.hypothesis.persistence.hibernate.PackDao;
import org.hypothesis.persistence.hibernate.TestDao;
import org.hypothesis.persistence.hibernate.UserPermissionDao;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class PermissionManager {

	// private static Logger logger = Logger.getLogger(PermitionManager.class);

	private UserPermissionDao userPermissionDao;
	private GroupPermissionDao groupPermissionDao;
	private PackDao packDao;
	private TestManager testManager;

	public static PermissionManager newInstance() {
		return new PermissionManager(new UserPermissionDao(), new GroupPermissionDao());
	}
	
	protected PermissionManager(UserPermissionDao userPermitionDao,
			GroupPermissionDao groupPermitionDao) {
		this(userPermitionDao, groupPermitionDao, new TestManager(new TestDao()));
	}

	protected PermissionManager(UserPermissionDao userPermitionDao,
			GroupPermissionDao groupPermitionDao, TestManager testManager) {
		this.userPermissionDao = userPermitionDao;
		this.groupPermissionDao = groupPermitionDao;
		this.packDao = new PackDao();
		this.testManager = testManager;
	}
	
	public TestManager getTestManager() {
		return testManager;
	}

	public GroupPermission addGroupPermission(GroupPermission groupPermission) {
		try {
			groupPermissionDao.beginTransaction();
			groupPermission = groupPermissionDao.makePersistent(groupPermission);
			groupPermissionDao.commit();
			return groupPermission;
		} catch (HibernateException e) {
			groupPermissionDao.rollback();
			throw e;
		}
	}

	public UserPermission addUserPermission(UserPermission userPermission) {
		try {
			userPermissionDao.beginTransaction();
			userPermission = userPermissionDao.makePersistent(userPermission);
			userPermissionDao.commit();
			return userPermission;
		} catch (HibernateException e) {
			userPermissionDao.rollback();
			throw e;
		}
	}

	public void deleteGroupPermissions(Group group) {
		try {
			Set<GroupPermission> groupPermissions = getGroupPermissions(group);
			groupPermissionDao.beginTransaction();
			for (GroupPermission groupPermission : groupPermissions) {
				groupPermissionDao.makeTransient(groupPermission);
			}
			groupPermissionDao.commit();
		} catch (HibernateException e) {
			groupPermissionDao.rollback();
			throw e;
		}
	}

	public void deleteUserPermissions(User user) {
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			for (UserPermission userPermission : userPermissions) {
				userPermissionDao.makeTransient(userPermission);
			}
			userPermissionDao.commit();
		} catch (HibernateException e) {
			userPermissionDao.rollback();
			throw e;
		}
	}

	public void deleteUserPermissions(User user, boolean enabled) {
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			for (UserPermission userPermission : userPermissions) {
				if (userPermission.getEnabled() == enabled) {
					userPermissionDao.makeTransient(userPermission);
				}
			}
			userPermissionDao.commit();
		} catch (HibernateException e) {
			userPermissionDao.rollback();
			throw e;
		}
	}

	public List<GroupPermission> findAllGroupPermissions() {
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> groupPermissions = groupPermissionDao.findAll();
			groupPermissionDao.commit();
			return groupPermissions;
		} catch (HibernateException e) {
			groupPermissionDao.rollback();
			return null;
		}
	}

	public List<Pack> findAllPacks() {
		try {
			packDao.beginTransaction();
			List<Pack> allPacks = packDao.findAll();
			packDao.commit();
			return allPacks;
		} catch (Throwable e) {
			packDao.rollback();
		}
		return null;
	}

	/*
	 * public Set<Pack> findUserPacks3(User user, boolean excludeFinished) {
	 * //List<Pack> allPacks = findAllPacks(); List<GroupPermition>
	 * groupPermitions = findAllGroupPermitions(); List<UserPermition>
	 * userpPermitions = findAllUserPermitions();
	 * 
	 * //for (Pack pack : allPacks) {
	 * 
	 * //} }
	 */

	public List<UserPermission> findAllUserPermissions() {
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> userPermissions = userPermissionDao.findAll();
			userPermissionDao.commit();
			return userPermissions;
		} catch (HibernateException e) {
			userPermissionDao.rollback();
			return null;
		}
	}

	public Set<Pack> findUserPacks(User user, boolean excludeFinished) {
		Set<Pack> packs = getUserPacks(user, true, excludeFinished);
		Set<Pack> disabledPacks = getUserPacks(user, false, null);

		if (!user.getGroups().isEmpty()) {
			try {
				groupPermissionDao.beginTransaction();
				List<GroupPermission> groupsPermissions = groupPermissionDao
						.findByCriteria(Restrictions.in(FieldConstants.GROUP,
								user.getGroups()));
				groupPermissionDao.commit();

				for (GroupPermission groupPermission : groupsPermissions) {
					if (!disabledPacks.contains(groupPermission.getPack())) {
						packs.add(groupPermission.getPack());
					}
				}
			} catch (Throwable e) {
				groupPermissionDao.rollback();
				return null;
			}
		}
		return packs;
	}

	/*
	 * @SuppressWarnings("unchecked") public Set<Pack> findUserPacks(Long
	 * userId, boolean excludeFinished) { try { Set<Pack> packs = new
	 * HashSet<Pack>(); Session session = Util.getSession();
	 * session.beginTransaction(); Query query =
	 * session.createSQLQuery("select * from tbl_pack where tbl_pack.id in " +
	 * "(select pack_id from tbl_user_permition where user_id = :id and enabled = true) or ("
	 * +
	 * "tbl_pack.id in (select pack_id from tbl_group_permition left join tbl_group_user using (group_id) where user_id = :id) "
	 * +
	 * "and tbl_pack.id not in (select pack_id from tbl_user_permition where user_id = :id and enabled = false))"
	 * ); query.setParameter("id", userId, Hibernate.LONG); List<Object[]> list
	 * = query.list(); session.flush(); session.getTransaction().commit(); for
	 * (Object[] item : list) { Pack pack = new Pack();
	 * pack.setName((String)item[2]); packs.add(pack); } return packs; } catch
	 * (HibernateException e) { throw e; } }
	 */

	public Set<Pack> findUserPacks2(User user, boolean excludeFinished) {
		try {
			// Set<Pack> packs = new HashSet<Pack>();
			Hashtable<Long, Pack> packs = new Hashtable<Long, Pack>();

			if (!user.getGroups().isEmpty()) {
				Set<GroupPermission> groupsPermissions = getGroupsPermissions(user
						.getGroups());
				for (GroupPermission groupPermission : groupsPermissions) {
					packs.put(groupPermission.getPack().getId(),
							groupPermission.getPack());
				}
			}

			Set<UserPermission> userPermissions = getUserPermissions(user);
			for (UserPermission userPermission : userPermissions) {
				Long packId = userPermission.getPack().getId();
				if (userPermission.getEnabled() && !packs.containsKey(packId)) {
					packs.put(packId, userPermission.getPack());
				} else if (!userPermission.getEnabled()) {
					packs.remove(packId);
				}
			}

			return new HashSet<Pack>(packs.values());
		} catch (HibernateException e) {
			throw e;
		}
	}

	public Set<Pack> getGroupPacks(Group group) {
		try {
			Set<GroupPermission> groupPermissions = getGroupPermissions(group);
			Set<Pack> packs = new HashSet<Pack>();
			for (GroupPermission groupPermission : groupPermissions) {
				packs.add(groupPermission.getPack());
			}
			return packs;
		} catch (HibernateException e) {
			return null;
		}
	}

	public Set<GroupPermission> getGroupPermissions(Group group) {
		Set<GroupPermission> groupPermissions = new HashSet<GroupPermission>();
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao
					.findByCriteria(Restrictions
							.eq(FieldConstants.GROUP, group));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);
		} catch (Throwable e) {
			groupPermissionDao.rollback();
		}
		return groupPermissions;
	}

	public Set<GroupPermission> getGroupsPermissions(Set<Group> groups) {
		try {
			Set<GroupPermission> groupsPermissions = new HashSet<GroupPermission>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpsPerms = groupPermissionDao
					.findByCriteria(Restrictions.in(FieldConstants.GROUP,
							groups));
			groupPermissionDao.commit();
			groupsPermissions.addAll(grpsPerms);

			return groupsPermissions;
		} catch (HibernateException e) {
			groupPermissionDao.rollback();
			throw e;
		}
	}

	public Set<GroupPermission> getPackGroupPermissions(Pack pack) {
		try {
			Set<GroupPermission> groupPermissions = new HashSet<GroupPermission>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao
					.findByCriteria(Restrictions.eq(FieldConstants.PACK, pack));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);

			return groupPermissions;
		} catch (Throwable e) {
			groupPermissionDao.rollback();
		}
		return null;
	}

	public Set<UserPermission> getPackUserPermissions(Pack pack, boolean enabled) {
		try {
			Set<UserPermission> userPermissions = new HashSet<UserPermission>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao
					.findByCriteria(Restrictions.and(
							Restrictions.eq(FieldConstants.PACK, pack),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);

			return userPermissions;
		} catch (Throwable e) {
			userPermissionDao.rollback();
		}
		return null;
	}

	public Set<Pack> getUserPacks(User user, Boolean enabled,
			Boolean excludeFinished) {
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			Set<Pack> packs = new HashSet<Pack>();
			for (UserPermission userPermission : userPermissions) {
				if (enabled == null
						|| userPermission.getEnabled().equals(enabled)) {
					Pack pack = userPermission.getPack();
					if (userPermission.getPass() == null
							|| excludeFinished == null || !excludeFinished) {
						packs.add(pack);
					} else {
						List<Test> finishedTests = testManager.findTestsBy(
								user, pack, Status.FINISHED);
						if (userPermission.getPass() < finishedTests.size()) {
							packs.add(pack);
						}
					}
				}
			}
			return packs;
		} catch (HibernateException e) {
			return null;
		}
	}

	public Set<UserPermission> getUserPermissions(User user) {
		Set<UserPermission> userPermissions = new HashSet<UserPermission>();
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao
					.findByCriteria(Restrictions.eq(FieldConstants.USER, user));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);
		} catch (HibernateException e) {
			userPermissionDao.rollback();
		}
		return userPermissions;
	}

	public Set<UserPermission> getUsersPermissions(Set<User> users,
			boolean enabled) {
		try {
			Set<UserPermission> usersPermissions = new HashSet<UserPermission>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrsPerms = userPermissionDao
					.findByCriteria(Restrictions.and(
							Restrictions.in(FieldConstants.USER, users),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			usersPermissions.addAll(usrsPerms);

			return usersPermissions;
		} catch (Throwable e) {
			userPermissionDao.rollback();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pack> getPublishedPacks() {
		try {
			packDao.beginTransaction();
			Criteria criteria = packDao.createCriteria();
			criteria.add(Restrictions.eq(FieldConstants.PUBLISHED, true));
			criteria.addOrder(Order.asc(FieldConstants.ID));
			List<Pack> packs = criteria.list();
			packDao.commit();
			return packs;
		} catch (Throwable e) {
			packDao.rollback();
		}
		return null;
	}

}
