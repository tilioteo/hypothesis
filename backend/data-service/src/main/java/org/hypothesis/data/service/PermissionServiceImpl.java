/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.GenericDao;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.*;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class PermissionServiceImpl implements PermissionService {

	private static final Logger log = Logger.getLogger(PermissionServiceImpl.class);

	@Inject
	private GenericDao<UserPermission, Long> userPermissionDao;
	@Inject
	private GenericDao<GroupPermission, Long> groupPermissionDao;
	@Inject
	private GenericDao<Pack, Long> packDao;

	@Inject
	private UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#addGroupPermission(org.
	 * hypothesis.data.model.GroupPermission)
	 */
	@Override
	public GroupPermission addGroupPermission(GroupPermission groupPermission) {
		log.debug("addGroupPermission");
		try {
			groupPermissionDao.beginTransaction();
			groupPermissionDao.clear();
			groupPermission = groupPermissionDao.makePersistent(groupPermission);
			groupPermissionDao.commit();
			return groupPermission;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#addUserPermission(org.
	 * hypothesis.data.model.UserPermission)
	 */
	@Override
	public UserPermission addUserPermission(UserPermission userPermission) {
		log.debug("addUserPermission");
		try {
			userPermissionDao.beginTransaction();
			userPermissionDao.clear();
			userPermission = userPermissionDao.makePersistent(userPermission);
			userPermissionDao.commit();
			return userPermission;
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#deleteGroupPermissions(org.
	 * hypothesis.data.model.Group)
	 */
	@Override
	public void deleteGroupPermissions(Group group) {
		log.debug("deleteGroupPermissions");
		try {
			Set<GroupPermission> groupPermissions = getGroupPermissions(group);
			groupPermissionDao.beginTransaction();
			groupPermissions.forEach(groupPermissionDao::makeTransient);
			groupPermissionDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			// throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#deleteUserPermissions(org.
	 * hypothesis.data.model.User)
	 */
	@Override
	public void deleteUserPermissions(User user) {
		log.debug("deleteUserPermissions");
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			userPermissions.forEach(userPermissionDao::makeTransient);
			userPermissionDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			// throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#deleteUserPermissions(org.
	 * hypothesis.data.model.User, boolean)
	 */
	@Override
	public void deleteUserPermissions(User user, boolean enabled) {
		log.debug("deleteUserPermissions");
		try {
			Set<UserPermission> userPermissions = getUserPermissions(user);
			userPermissionDao.beginTransaction();
			userPermissions.stream().filter(f -> f.getEnabled() == enabled).forEach(userPermissionDao::makeTransient);
			userPermissionDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			// throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#findAllGroupPermissions()
	 */
	@Override
	public List<GroupPermission> findAllGroupPermissions() {
		log.debug("findAllGroupPermissions");
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> groupPermissions = groupPermissionDao.findAll();
			groupPermissionDao.commit();
			return groupPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#findAllPacks()
	 */
	@Override
	public List<Pack> findAllPacks() {
		log.debug("findAllPacks");
		try {
			packDao.beginTransaction();
			List<Pack> allPacks = packDao.findAll();
			packDao.commit();
			return allPacks;
		} catch (Exception e) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#findAllUserPermissions()
	 */
	@Override
	public List<UserPermission> findAllUserPermissions() {
		log.debug("findAllUserPermissions");
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> userPermissions = userPermissionDao.findAll();
			userPermissionDao.commit();
			return userPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#findUserPacks(org.
	 * hypothesis.data.model.User, boolean)
	 */
	@Override
	public Set<Pack> findUserPacks(User user, boolean excludeFinished) {
		log.debug("findUserPacks");
		Set<Pack> packs = getUserPacks(user, true, excludeFinished);
		Set<Pack> disabledPacks = getUserPacks(user, false, null);

		Set<Group> groups = userService.merge(user).getGroups();
		// Set<Group> groups = persistenceService.merge(user).getGroups();
		if (!groups.isEmpty()) {
			try {
				groupPermissionDao.beginTransaction();
				List<GroupPermission> groupsPermissions = groupPermissionDao
						.findByCriteria(Restrictions.in(EntityConstants.GROUP, groups));
				groupPermissionDao.commit();

				groupsPermissions.stream().filter(f -> !disabledPacks.contains(f.getPack()))
						.forEach(e -> packs.add(e.getPack()));
			} catch (Exception e) {
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
	 * HashSet<>(); Session session = Util.getSession();
	 * session.beginTransaction(); Query query = session.createSQLQuery(
	 * "select * from tbl_pack where tbl_pack.id in " +
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#findUserPacks2(org.
	 * hypothesis.data.model.User, boolean)
	 */
	@Override
	public Set<Pack> findUserPacks2(User user, boolean excludeFinished) {
		log.debug("findUserPacks2");
		try {
			Hashtable<Long, Pack> packs = new Hashtable<>();
			user = userService.merge(user);

			if (!user.getGroups().isEmpty()) {
				getGroupsPermissions(user.getGroups()).forEach(e -> packs.put(e.getPack().getId(), e.getPack()));
			}

			getUserPermissions(user).forEach(e -> {
				Long packId = e.getPack().getId();
				if (e.getEnabled() && !packs.containsKey(packId)) {
					packs.put(packId, e.getPack());
				} else if (!e.getEnabled()) {
					packs.remove(packId);
				}
			});

			return new HashSet<Pack>(packs.values());
		} catch (Exception e) {
			log.error(e.getMessage());
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#getGroupPacks(org.
	 * hypothesis.data.model.Group)
	 */
	@Override
	public Set<Pack> getGroupPacks(Group group) {
		log.debug("getGroupPacks");
		try {
			Set<Pack> packs = new HashSet<>();
			getGroupPermissions(group).forEach(e -> packs.add(e.getPack()));
			return packs;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getGroupPermissions(org.
	 * hypothesis.data.model.Group)
	 */
	@Override
	public Set<GroupPermission> getGroupPermissions(Group group) {
		log.debug("getGroupPermissions");
		Set<GroupPermission> groupPermissions = new HashSet<>();
		try {
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao
					.findByCriteria(Restrictions.eq(EntityConstants.GROUP, group));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
		}
		return groupPermissions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getGroupsPermissions(java.
	 * util.Set)
	 */
	@Override
	public Set<GroupPermission> getGroupsPermissions(Set<Group> groups) {
		log.debug("getGroupsPermissions");
		try {
			Set<GroupPermission> groupsPermissions = new HashSet<>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpsPerms = groupPermissionDao
					.findByCriteria(Restrictions.in(EntityConstants.GROUP, groups));
			groupPermissionDao.commit();
			groupsPermissions.addAll(grpsPerms);

			return groupsPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getPackGroupPermissions(org
	 * .hypothesis.data.model.Pack)
	 */
	@Override
	public Set<GroupPermission> getPackGroupPermissions(Pack pack) {
		log.debug("getPackGroupPermissions");
		try {
			Set<GroupPermission> groupPermissions = new HashSet<>();
			groupPermissionDao.beginTransaction();
			List<GroupPermission> grpPerms = groupPermissionDao
					.findByCriteria(Restrictions.eq(EntityConstants.PACK, pack));
			groupPermissionDao.commit();
			groupPermissions.addAll(grpPerms);

			return groupPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupPermissionDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getPackUserPermissions(org.
	 * hypothesis.data.model.Pack, boolean)
	 */
	@Override
	public Set<UserPermission> getPackUserPermissions(Pack pack, boolean enabled) {
		log.debug("getPackUserPermissions");
		try {
			Set<UserPermission> userPermissions = new HashSet<>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityConstants.PACK, pack), Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);

			return userPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getUserPacks(org.hypothesis
	 * .data.model.User, java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	public Set<Pack> getUserPacks(User user, Boolean enabled, Boolean excludeFinished) {
		log.debug("getUserPacks");
		try {
			return getUserPermissions(user).stream().filter(f -> enabled == null || f.getEnabled().equals(enabled))
					.map(UserPermission::getPack).collect(Collectors.toSet());
			// .forEach(e -> {
			// Pack pack = e.getPack();
			// // TODO check pass is not null
			// // if (userPermission.getPass() == null ||
			// // excludeFinished
			// // == null || !excludeFinished) {
			// packs.add(pack);
			// //
			// // } else { List<SimpleTest> finishedTests =
			// // testService.findTestsBy(user, pack, Status.FINISHED);
			// // if (userPermission.getPass() < finishedTests.size())
			// // { packs.add(pack); } }
			// });
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getUserPermissions(org.
	 * hypothesis.data.model.User)
	 */
	@Override
	public Set<UserPermission> getUserPermissions(User user) {
		log.debug("getUserPermissions");
		Set<UserPermission> userPermissions = new HashSet<>();
		try {
			userPermissionDao.beginTransaction();
			List<UserPermission> usrPerms = userPermissionDao
					.findByCriteria(Restrictions.eq(EntityConstants.USER, user));
			userPermissionDao.commit();
			userPermissions.addAll(usrPerms);
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return userPermissions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getUsersPermissions(java.
	 * util.Set, boolean)
	 */
	@Override
	public Set<UserPermission> getUsersPermissions(Set<User> users, boolean enabled) {
		log.debug("getUsersPermissions");
		try {
			Set<UserPermission> usersPermissions = new HashSet<>();
			userPermissionDao.beginTransaction();
			List<UserPermission> usrsPerms = userPermissionDao.findByCriteria(Restrictions.and(
					Restrictions.in(EntityConstants.USER, users), Restrictions.eq(FieldConstants.ENABLED, enabled)));
			userPermissionDao.commit();
			usersPermissions.addAll(usrsPerms);

			return usersPermissions;
		} catch (Exception e) {
			log.error(e.getMessage());
			userPermissionDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PermissionService#getPublishedPacks()
	 */
	@Override
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
		} catch (Exception e) {
			log.error(e.getMessage());
			packDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PermissionService#getSimplePublishedPacks()
	 */
	@Override
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
		} catch (Exception e) {
			log.error(e.getMessage());
			packDao.rollback();
		}
		return null;
	}

}
