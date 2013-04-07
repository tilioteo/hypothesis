/**
 * 
 */
package org.hypothesis.persistence;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.GroupPermition;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Test;
import org.hypothesis.entity.User;
import org.hypothesis.entity.UserPermition;
import org.hypothesis.entity.Test.Status;
import org.hypothesis.persistence.hibernate.GroupPermitionDao;
import org.hypothesis.persistence.hibernate.PackDao;
import org.hypothesis.persistence.hibernate.TestDao;
import org.hypothesis.persistence.hibernate.UserPermitionDao;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class PermitionManager {

	// private static Logger logger = Logger.getLogger(PermitionManager.class);

	private UserPermitionDao userPermitionDao;
	private GroupPermitionDao groupPermitionDao;
	private PackDao packDao;
	private TestManager testManager;

	public PermitionManager(UserPermitionDao userPermitionDao,
			GroupPermitionDao groupPermitionDao) {
		this(userPermitionDao, groupPermitionDao, new TestManager(
				new TestDao()));
	}

	public PermitionManager(UserPermitionDao userPermitionDao,
			GroupPermitionDao groupPermitionDao, TestManager testManager) {
		this.userPermitionDao = userPermitionDao;
		this.groupPermitionDao = groupPermitionDao;
		this.packDao = new PackDao();
		this.testManager = testManager;
	}

	public GroupPermition addGroupPermition(GroupPermition groupPermition) {
		try {
			groupPermitionDao.beginTransaction();
			groupPermition = groupPermitionDao.makePersistent(groupPermition);
			groupPermitionDao.commit();
			return groupPermition;
		} catch (HibernateException e) {
			groupPermitionDao.rollback();
			throw e;
		}
	}

	public UserPermition addUserPermition(UserPermition userPermition) {
		try {
			userPermitionDao.beginTransaction();
			userPermition = userPermitionDao.makePersistent(userPermition);
			userPermitionDao.commit();
			return userPermition;
		} catch (HibernateException e) {
			userPermitionDao.rollback();
			throw e;
		}
	}

	public void deleteGroupPermitions(Group group) {
		try {
			Set<GroupPermition> groupPermitions = getGroupPermitions(group);
			groupPermitionDao.beginTransaction();
			for (GroupPermition groupPermition : groupPermitions) {
				groupPermitionDao.makeTransient(groupPermition);
			}
			groupPermitionDao.commit();
		} catch (HibernateException e) {
			groupPermitionDao.rollback();
			throw e;
		}
	}

	public void deleteUserPermitions(User user) {
		try {
			Set<UserPermition> userPermitions = getUserPermitions(user);
			userPermitionDao.beginTransaction();
			for (UserPermition userPermition : userPermitions) {
				userPermitionDao.makeTransient(userPermition);
			}
			userPermitionDao.commit();
		} catch (HibernateException e) {
			userPermitionDao.rollback();
			throw e;
		}
	}

	public void deleteUserPermitions(User user, boolean enabled) {
		try {
			Set<UserPermition> userPermitions = getUserPermitions(user);
			userPermitionDao.beginTransaction();
			for (UserPermition userPermition : userPermitions) {
				if (userPermition.getEnabled() == enabled) {
					userPermitionDao.makeTransient(userPermition);
				}
			}
			userPermitionDao.commit();
		} catch (HibernateException e) {
			userPermitionDao.rollback();
			throw e;
		}
	}

	public List<GroupPermition> findAllGroupPermitions() {
		try {
			groupPermitionDao.beginTransaction();
			List<GroupPermition> grpsPerms = groupPermitionDao.findAll();
			groupPermitionDao.commit();
			return grpsPerms;
		} catch (HibernateException e) {
			groupPermitionDao.rollback();
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

	public List<UserPermition> findAllUserPermitions() {
		try {
			userPermitionDao.beginTransaction();
			List<UserPermition> usersPerms = userPermitionDao.findAll();
			userPermitionDao.commit();
			return usersPerms;
		} catch (HibernateException e) {
			userPermitionDao.rollback();
			return null;
		}
	}

	public Set<Pack> findUserPacks(User user, boolean excludeFinished) {
		Set<Pack> packs = getUserPacks(user, true, excludeFinished);
		Set<Pack> disabledPacks = getUserPacks(user, false, null);

		if (!user.getGroups().isEmpty()) {
			try {
				groupPermitionDao.beginTransaction();
				List<GroupPermition> groupsPermitions = groupPermitionDao
						.findByCriteria(Restrictions.in(FieldConstants.GROUP,
								user.getGroups()));
				groupPermitionDao.commit();

				for (GroupPermition groupsPermition : groupsPermitions) {
					if (!disabledPacks.contains(groupsPermition.getPack())) {
						packs.add(groupsPermition.getPack());
					}
				}
			} catch (Throwable e) {
				groupPermitionDao.rollback();
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
				Set<GroupPermition> groupsPermitions = getGroupsPermitions(user
						.getGroups());
				for (GroupPermition groupPermition : groupsPermitions) {
					packs.put(groupPermition.getPack().getId(),
							groupPermition.getPack());
				}
			}

			Set<UserPermition> userPermitions = getUserPermitions(user);
			for (UserPermition userPermition : userPermitions) {
				Long packId = userPermition.getPack().getId();
				if (userPermition.getEnabled() && !packs.containsKey(packId)) {
					packs.put(packId, userPermition.getPack());
				} else if (!userPermition.getEnabled()) {
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
			Set<GroupPermition> groupPermitions = getGroupPermitions(group);
			Set<Pack> packs = new HashSet<Pack>();
			for (GroupPermition groupPermition : groupPermitions) {
				packs.add(groupPermition.getPack());
			}
			return packs;
		} catch (HibernateException e) {
			return null;
		}
	}

	public Set<GroupPermition> getGroupPermitions(Group group) {
		Set<GroupPermition> groupPermitions = new HashSet<GroupPermition>();
		try {
			groupPermitionDao.beginTransaction();
			List<GroupPermition> grpPerms = groupPermitionDao
					.findByCriteria(Restrictions
							.eq(FieldConstants.GROUP, group));
			groupPermitionDao.commit();
			groupPermitions.addAll(grpPerms);
		} catch (Throwable e) {
			groupPermitionDao.rollback();
		}
		return groupPermitions;
	}

	public Set<GroupPermition> getGroupsPermitions(Set<Group> groups) {
		try {
			Set<GroupPermition> groupsPermitions = new HashSet<GroupPermition>();
			groupPermitionDao.beginTransaction();
			List<GroupPermition> grpsPerms = groupPermitionDao
					.findByCriteria(Restrictions.in(FieldConstants.GROUP,
							groups));
			groupPermitionDao.commit();
			groupsPermitions.addAll(grpsPerms);

			return groupsPermitions;
		} catch (HibernateException e) {
			groupPermitionDao.rollback();
			throw e;
		}
	}

	public Set<GroupPermition> getPackGroupPermitions(Pack pack) {
		try {
			Set<GroupPermition> groupPermitions = new HashSet<GroupPermition>();
			groupPermitionDao.beginTransaction();
			List<GroupPermition> grpPerms = groupPermitionDao
					.findByCriteria(Restrictions.eq(FieldConstants.PACK, pack));
			groupPermitionDao.commit();
			groupPermitions.addAll(grpPerms);

			return groupPermitions;
		} catch (Throwable e) {
			groupPermitionDao.rollback();
		}
		return null;
	}

	public Set<UserPermition> getPackUserPermitions(Pack pack, boolean enabled) {
		try {
			Set<UserPermition> userPermitions = new HashSet<UserPermition>();
			userPermitionDao.beginTransaction();
			List<UserPermition> usrPerms = userPermitionDao
					.findByCriteria(Restrictions.and(
							Restrictions.eq(FieldConstants.PACK, pack),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermitionDao.commit();
			userPermitions.addAll(usrPerms);

			return userPermitions;
		} catch (Throwable e) {
			userPermitionDao.rollback();
		}
		return null;
	}

	public Set<Pack> getUserPacks(User user, Boolean enabled,
			Boolean excludeFinished) {
		try {
			Set<UserPermition> userPermitions = getUserPermitions(user);
			Set<Pack> packs = new HashSet<Pack>();
			for (UserPermition userPermition : userPermitions) {
				if (enabled == null
						|| userPermition.getEnabled().equals(enabled)) {
					Pack pack = userPermition.getPack();
					if (userPermition.getPass() == null
							|| excludeFinished == null || !excludeFinished) {
						packs.add(pack);
					} else {
						List<Test> finishedTests = testManager.findTestsBy(
								user, pack, Status.FINISHED);
						if (userPermition.getPass() < finishedTests.size()) {
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

	public Set<UserPermition> getUserPermitions(User user) {
		Set<UserPermition> userPermitions = new HashSet<UserPermition>();
		try {
			userPermitionDao.beginTransaction();
			List<UserPermition> usrPerms = userPermitionDao
					.findByCriteria(Restrictions.eq(FieldConstants.USER, user));
			userPermitionDao.commit();
			userPermitions.addAll(usrPerms);
		} catch (HibernateException e) {
			userPermitionDao.rollback();
		}
		return userPermitions;
	}

	public Set<UserPermition> getUsersPermitions(Set<User> users,
			boolean enabled) {
		try {
			Set<UserPermition> usersPermitions = new HashSet<UserPermition>();
			userPermitionDao.beginTransaction();
			List<UserPermition> usrsPerms = userPermitionDao
					.findByCriteria(Restrictions.and(
							Restrictions.in(FieldConstants.USER, users),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermitionDao.commit();
			usersPermitions.addAll(usrsPerms);

			return usersPermitions;
		} catch (Throwable e) {
			userPermitionDao.rollback();
		}
		return null;
	}

}
