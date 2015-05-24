/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.dao.GroupPermissionDao;
import com.tilioteo.hypothesis.dao.PackDao;
import com.tilioteo.hypothesis.dao.UserPermissionDao;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.GroupPermission;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.entity.UserPermission;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class PermissionService implements Serializable {

	private static Logger log = Logger.getLogger(PermissionService.class);

	private UserPermissionDao userPermissionDao;
	private GroupPermissionDao groupPermissionDao;
	private PackDao packDao;
	private TestService testService;
	
	//private PersistenceService persistenceService;
	private UserService userService;

	public static PermissionService newInstance() {
		return new PermissionService(new UserPermissionDao(), new GroupPermissionDao());
	}
	
	protected PermissionService(UserPermissionDao userPermitionDao,
			GroupPermissionDao groupPermitionDao) {
		this(userPermitionDao, groupPermitionDao, TestService.newInstance());
	}

	protected PermissionService(UserPermissionDao userPermitionDao,
			GroupPermissionDao groupPermitionDao, TestService testService) {
		this.userPermissionDao = userPermitionDao;
		this.groupPermissionDao = groupPermitionDao;
		this.packDao = new PackDao();
		this.testService = testService;
		
		//persistenceService = PersistenceService.newInstance();
		userService = UserService.newInstance();
	}
	
	public TestService getTestManager() {
		return testService;
	}

	/*private GroupPermission mergeInit(GroupPermission groupPermission) {
		groupPermissionDao.clear();
		groupPermission = groupPermissionDao.merge(groupPermission);
		return groupPermission;
	}*/

	public GroupPermission addGroupPermission(GroupPermission groupPermission) {
		log.debug("addGroupPermission");
		try {
			groupPermissionDao.beginTransaction();
			groupPermissionDao.clear();
			groupPermission = groupPermissionDao.makePersistent(groupPermission);
			groupPermissionDao.commit();
			return groupPermission;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			//throw e;
			return null;
		}
	}

	public UserPermission addUserPermission(UserPermission userPermission) {
		log.debug("addUserPermission");
		try {
			userPermissionDao.beginTransaction();
			userPermissionDao.clear();
			userPermission = userPermissionDao.makePersistent(userPermission);
			userPermissionDao.commit();
			return userPermission;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			//throw e;
			return null;
		}
	}

	public void deleteGroupPermissions(Group group) {
		log.debug("deleteGroupPermissions");
		try {
			Set<GroupPermission> groupPermissions = getGroupPermissions(group);
			groupPermissionDao.beginTransaction();
			for (GroupPermission groupPermission : groupPermissions) {
				groupPermissionDao.makeTransient(groupPermission);
			}
			groupPermissionDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			//throw e;
		}
	}

	public void deleteUserPermissions(User user) {
		log.debug("deleteUserPermissions");
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			for (UserPermission userPermission : userPermissions) {
				userPermissionDao.makeTransient(userPermission);
			}
			userPermissionDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			//throw e;
		}
	}

	public void deleteUserPermissions(User user, boolean enabled) {
		log.debug("deleteUserPermissions");
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			for (UserPermission userPermission : userPermissions) {
				if (userPermission.getEnabled() == enabled) {
					userPermissionDao.makeTransient(userPermission);
				}
			}
			userPermissionDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			//throw e;
		}
	}

	public List<GroupPermission> findAllGroupPermissions() {
		log.debug("findAllGroupPermissions");
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> groupPermissions = groupPermissionDao.findAll();
			groupPermissionDao.commit();
			return groupPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			return null;
		}
	}

	public List<Pack> findAllPacks() {
		log.debug("findAllPacks");
		try {
			packDao.beginTransaction();
			List<Pack> allPacks = packDao.findAll();
			packDao.commit();
			return allPacks;
		} catch (Throwable e) {
			log.error(e.getMessage());
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
		log.debug("findAllUserPermissions");
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> userPermissions = userPermissionDao.findAll();
			userPermissionDao.commit();
			return userPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			return null;
		}
	}

	public Set<Pack> findUserPacks(User user, boolean excludeFinished) {
		log.debug("findUserPacks");
		Set<Pack> packs = getUserPacks(user, true, excludeFinished);
		Set<Pack> disabledPacks = getUserPacks(user, false, null);

		Set<Group> groups = userService.merge(user).getGroups();
		//Set<Group> groups = persistenceService.merge(user).getGroups();
		if (!groups.isEmpty()) {
			try {
				groupPermissionDao.beginTransaction();
				List<GroupPermission> groupsPermissions = groupPermissionDao.findByCriteria(Restrictions.in(EntityConstants.GROUP, groups));
				groupPermissionDao.commit();

				for (GroupPermission groupPermission : groupsPermissions) {
					Pack groupPack = groupPermission.getPack();
					if (!disabledPacks.contains(groupPack)) {
						packs.add(/*persistenceService.merge*/(groupPack));
					}
				}
			} catch (Throwable e) {
				log.error(e.getMessage());
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
		log.debug("findUserPacks2");
		try {
			// Set<Pack> packs = new HashSet<Pack>();
			Hashtable<Long, Pack> packs = new Hashtable<Long, Pack>();
			user = userService.merge(user);
			//user = persistenceService.merge(user);
			if (!user.getGroups().isEmpty()) {
				Set<GroupPermission> groupsPermissions = getGroupsPermissions(user.getGroups());
				for (GroupPermission groupPermission : groupsPermissions) {
					packs.put(groupPermission.getPack().getId(), groupPermission.getPack());
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
		} catch (Throwable e) {
			log.error(e.getMessage());
			//throw e;
			return null;
		}
	}

	public Set<Pack> getGroupPacks(Group group) {
		log.debug("getGroupPacks");
		try {
			Set<GroupPermission> groupPermissions = getGroupPermissions(group);
			Set<Pack> packs = new HashSet<Pack>();
			for (GroupPermission groupPermission : groupPermissions) {
				packs.add(groupPermission.getPack());
			}
			return packs;
		} catch (Throwable e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public Set<GroupPermission> getGroupPermissions(Group group) {
		log.debug("getGroupPermissions");
		Set<GroupPermission> groupPermissions = new HashSet<GroupPermission>();
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao.findByCriteria(
					Restrictions.eq(EntityConstants.GROUP, group));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
		}
		return groupPermissions;
	}

	public Set<GroupPermission> getGroupsPermissions(Set<Group> groups) {
		log.debug("getGroupsPermissions");
		try {
			Set<GroupPermission> groupsPermissions = new HashSet<GroupPermission>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpsPerms = groupPermissionDao.findByCriteria(
					Restrictions.in(EntityConstants.GROUP, groups));
			groupPermissionDao.commit();
			groupsPermissions.addAll(grpsPerms);

			return groupsPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			//throw e;
			return null;
		}
	}

	public Set<GroupPermission> getPackGroupPermissions(Pack pack) {
		log.debug("getPackGroupPermissions");
		try {
			Set<GroupPermission> groupPermissions = new HashSet<GroupPermission>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao.findByCriteria(
					Restrictions.eq(EntityConstants.PACK, pack));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);

			return groupPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
		}
		return null;
	}

	public Set<UserPermission> getPackUserPermissions(Pack pack, boolean enabled) {
		log.debug("getPackUserPermissions");
		try {
			Set<UserPermission> userPermissions = new HashSet<UserPermission>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao.findByCriteria(
					Restrictions.and(
							Restrictions.eq(EntityConstants.PACK, pack),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);

			return userPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return null;
	}

	public Set<Pack> getUserPacks(User user, Boolean enabled, Boolean excludeFinished) {
		log.debug("getUserPacks");
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			Set<Pack> packs = new HashSet<Pack>();
			for (UserPermission userPermission : userPermissions) {
				if (enabled == null	|| userPermission.getEnabled().equals(enabled)) {
					Pack pack = userPermission.getPack();
					if (userPermission.getPass() == null || excludeFinished == null || !excludeFinished) {
						packs.add(pack);
					} /*else {
						List<SimpleTest> finishedTests = testService.findTestsBy(user, pack, Status.FINISHED);
						if (userPermission.getPass() < finishedTests.size()) {
							packs.add(pack);
						}
					}*/
				}
			}
			return packs;
		} catch (Throwable e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public Set<UserPermission> getUserPermissions(User user) {
		log.debug("getUserPermissions");
		Set<UserPermission> userPermissions = new HashSet<UserPermission>();
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao.findByCriteria(
					Restrictions.eq(EntityConstants.USER, user));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return userPermissions;
	}

	public Set<UserPermission> getUsersPermissions(Set<User> users,
			boolean enabled) {
		log.debug("getUsersPermissions");
		try {
			Set<UserPermission> usersPermissions = new HashSet<UserPermission>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrsPerms = userPermissionDao.findByCriteria(
					Restrictions.and(
							Restrictions.in(EntityConstants.USER, users),
							Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			usersPermissions.addAll(usrsPerms);

			return usersPermissions;
		} catch (Throwable e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pack> getPublishedPacks() {
		log.debug("getPublishedPacks");
		try {
			packDao.beginTransaction();
			Criteria criteria = packDao.createCriteria();
			criteria.add(Restrictions.eq(FieldConstants.PUBLISHED, true));
			criteria.addOrder(Order.asc(FieldConstants.ID));
			List<Pack> packs = criteria.list();
			packDao.commit();
			return packs;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packDao.rollback();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Pack> getSimplePublishedPacks() {
		log.debug("getSimplePublishedPacks");
		try {
			packDao.beginTransaction();
			Criteria criteria = packDao.createCriteria();
			criteria.add(Restrictions.eq(FieldConstants.PUBLISHED, true));
			criteria.addOrder(Order.asc(FieldConstants.ID));
			List<Pack> packs = criteria.list();
			packDao.commit();
			return packs;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packDao.rollback();
		}
		return null;
	}

}
