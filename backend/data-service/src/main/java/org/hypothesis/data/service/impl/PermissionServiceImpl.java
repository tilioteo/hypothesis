package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.GroupPermission;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.model.UserPermission;
import org.hypothesis.data.service.PermissionService;

public class PermissionServiceImpl implements PermissionService {

	private static final Logger log = Logger.getLogger(PermissionServiceImpl.class);

	private final HibernateDao<UserPermission, Long> userPermissionDao = new HibernateDao<UserPermission, Long>(
			UserPermission.class);
	private final HibernateDao<GroupPermission, Long> groupPermissionDao = new HibernateDao<GroupPermission, Long>(
			GroupPermission.class);
	private final HibernateDao<Pack, Long> packDao = new HibernateDao<Pack, Long>(Pack.class);

	private final HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(User.class);

	private final PackConverter packConverter = new PackConverter();

	@Override
	public synchronized boolean userCanAccess(Long userId, long packId) {
		log.debug("userCanAccess");
		try {
			begin();

			boolean found = false;
			if (userId != null) {
				// findUserPackIds(userId, true);
				found = getUserPackIdsInternalVN(userId).stream()//
						.anyMatch(id -> id.equals(packId));
			}

			if (!found) {
				found = getPublishedPacksInternal().stream()//
						.map(Pack::getId)//
						.anyMatch(id -> id.equals(packId));
			}

			commit();
			return found;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return false;
	}

	@Override
	public synchronized void disableForVN(long userId, long packId) {
		log.debug("disableForVN");
		try {
			begin();

			getUserPermissions(userId).stream()//
					.filter(up -> up.getPackId() == packId)//
					.forEach(userPermissionDao::makeTransient);

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
			// throw e;
		}
	}

	@Override
	public synchronized List<PackDto> getUserPacksVN(long userId) {
		log.debug("getUserPacksVN");

		try {
			begin();

			List<PackDto> packs = getUserPackIdsInternalVN(userId).stream()//
					.map(id -> packDao.findById(id, false))//
					.map(p -> packConverter.doDto(p, false))//
					.collect(toList());

			commit();
			return packs;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized List<PackDto> getPublishedPacks() {
		log.debug("getPublishedPacks");

		try {
			begin();

			final List<PackDto> packs = getPublishedPacksInternal().stream()//
					.map(p -> packConverter.doDto(p, false))//
					.collect(toList());

			commit();
			return packs;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized List<PackDto> findUserPacks2(long userid, boolean excludeFinished) {
		log.debug("findUserPacks2");
		try {
			begin();
			Set<Long> packIds = new HashSet<>();

			User user = userDao.findById(userid, false);

			if (!user.getGroups().isEmpty()) {
				Set<GroupPermission> groupsPermissions = getGroupsPermissions(user.getGroups());
				for (GroupPermission groupPermission : groupsPermissions) {
					packIds.add(groupPermission.getPackId());
				}
			}

			Set<UserPermission> userPermissions = getUserPermissions(user.getId());
			for (UserPermission userPermission : userPermissions) {
				Long packId = userPermission.getPackId();
				if (userPermission.getEnabled() && !packIds.contains(packId)) {
					packIds.add(packId);
				} else if (!userPermission.getEnabled()) {
					packIds.remove(packId);
				}
			}

			final List<PackDto> dtos = packIds.stream()//
					.filter(Objects::nonNull)//
					.map(id -> packDao.findById(id, false))//
					.map(p -> packConverter.doDto(p, false))//
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
	public synchronized List<PackDto> getGroupPacks(long groupId) {
		return getGroupPacks(Stream.of(groupId).collect(toSet()));
	}

	@Override
	public synchronized List<PackDto> getGroupPacks(Set<Long> groupIds) {
		log.debug("getGroupPacks");
		if (groupIds != null && groupIds.stream().filter(Objects::nonNull).count() > 0) {
			try {
				begin();

				List<PackDto> packs = groupPermissionDao
						.findByCriteria(Restrictions.in(EntityConstants.GROUP_ID, groupIds)).stream()//
						.distinct()//
						.filter(Objects::nonNull)//
						.map(GroupPermission::getPackId)//
						.map(id -> packDao.findById(id, false))//
						.map(p -> packConverter.doDto(p, false))//
						.collect(toList());

				commit();
				return packs;
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}
		return emptyList();
	}

	@Override
	public synchronized void setGroupPermissions(long groupId, Set<PackDto> enabledPacks) {
		log.debug("setGroupPermitions");

		try {
			begin();

			// remove old permissions
			groupPermissionDao.findByCriteria(Restrictions.eq(EntityConstants.GROUP_ID, groupId))//
					.forEach(groupPermissionDao::makeTransient);

			// add
			if (enabledPacks != null) {
				enabledPacks.stream()//
						.filter(Objects::nonNull)//
						.map(PackDto::getId)//
						.map(id -> createGroupPermission(groupId, id))//
						.forEach(groupPermissionDao::makePersistent);
			}

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
	}

	@Override
	public synchronized void setUserPermissions(long userId, Set<PackDto> enabledPacks, Set<PackDto> disabledPacks) {
		log.debug("setUserPermissions");

		try {
			begin();

			// remove old permissions
			userPermissionDao.findByCriteria(Restrictions.eq(EntityConstants.USER_ID, userId))//
					.forEach(userPermissionDao::makeTransient);

			final AtomicInteger aInt = new AtomicInteger(1);
			// add enabled
			if (enabledPacks != null) {
				enabledPacks.stream()//
						.filter(Objects::nonNull)//
						.map(PackDto::getId)//
						.map(id -> createUserPermission(userId, id, aInt.getAndIncrement(), true))//
						.forEach(userPermissionDao::makePersistent);
			}
			// add disabled
			if (disabledPacks != null) {
				disabledPacks.stream()//
						.filter(Objects::nonNull)//
						.map(PackDto::getId)//
						.map(id -> createUserPermission(userId, id, aInt.getAndIncrement(), false))//
						.forEach(userPermissionDao::makePersistent);
			}

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
	}

	@Override
	public List<PackDto> getUserPacks(long userId, boolean enabled) {
		log.debug("getUserPacks");
		try {
			begin();

			final List<PackDto> packs = getUserPermissions(userId).stream()//
					.filter(up -> enabled && up.getEnabled())//
					.map(UserPermission::getPackId)//
					.map(id -> packDao.findById(id, false))//
					.map(p -> packConverter.doDto(p, false)).collect(toList());

			commit();
			return packs;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
			;
		}
		return emptyList();
	}

	private GroupPermission createGroupPermission(long groupId, long packId) {
		final GroupPermission groupPermission = new GroupPermission();

		groupPermission.setGroupId(groupId);
		groupPermission.setPackId(packId);

		return groupPermission;
	}

	private UserPermission createUserPermission(long userId, long packId, int rank, boolean enabled) {
		final UserPermission userPermission = new UserPermission();

		userPermission.setUserId(userId);
		userPermission.setUserId(userId);
		userPermission.setPackId(packId);
		userPermission.setRank(rank);
		userPermission.setEnabled(enabled);

		return userPermission;
	}

	@SuppressWarnings("unchecked")
	private List<Pack> getPublishedPacksInternal() {
		log.debug("getPublishedPacksInternal");

		Criteria criteria = packDao.createCriteria();
		criteria.add(Restrictions.eq(FieldConstants.PUBLISHED, true));
		criteria.addOrder(Order.asc(FieldConstants.ID));
		List<Pack> packs = criteria.list();

		return packs.stream()//
				.filter(Objects::nonNull)//
				.collect(toList());
	}

	@SuppressWarnings("unchecked")
	private List<Long> getUserPackIdsInternalVN(long userId) {
		log.debug("getUserPackIdsInternalVN");

		Criteria criteria = userPermissionDao.createCriteria();
		criteria.add(Restrictions.eq(EntityConstants.USER_ID, userId));
		criteria.addOrder(Order.asc(FieldConstants.RANK));
		List<UserPermission> usrPerms = criteria.list();

		return usrPerms.stream()//
				.filter(Objects::nonNull)//
				.map(UserPermission::getPackId)//
				.collect(toList());
	}

	private Set<UserPermission> getUserPermissions(long userId) {
		log.debug("getUserPermissions");

		List<UserPermission> usrPerms = userPermissionDao
				.findByCriteria(Restrictions.eq(EntityConstants.USER_ID, userId));

		return usrPerms.stream()//
				.collect(toSet());
	}

	private Set<GroupPermission> getGroupsPermissions(Set<Group> groups) {
		log.debug("getGroupsPermissions");

		if (groups != null && !groups.isEmpty()) {
			return groupPermissionDao
					.findByCriteria(Restrictions.in(EntityConstants.GROUP_ID,
							groups.stream().map(Group::getId).collect(toSet())))
					.stream()//
					.filter(Objects::nonNull)//
					.collect(toSet());
		} else {
			return emptySet();
		}
	}

}
