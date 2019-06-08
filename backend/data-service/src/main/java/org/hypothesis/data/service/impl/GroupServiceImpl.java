package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.GroupDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.service.GroupService;

public class GroupServiceImpl implements GroupService {

	private static final Logger log = Logger.getLogger(GroupServiceImpl.class);

	private final HibernateDao<Group, Long> dao = new HibernateDao<Group, Long>(Group.class);

	private final GroupConverter groupConverter = new GroupConverter();

	@Override
	public synchronized List<GroupDto> findAll() {
		log.debug("findAll");
		try {
			begin();

			final List<GroupDto> groups = dao.findAll().stream()//
					.filter(Objects::nonNull)//
					.map(groupConverter::toDto)//
					.collect(toList());
			commit();

			return groups;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized List<GroupDto> findOwnerGroups(Long userId) {
		log.debug("findOwnerGroups");
		try {
			begin();

			List<GroupDto> groups = dao.findByCriteria(Restrictions.eq(EntityConstants.OWNER_ID, userId)).stream()//
					.filter(Objects::nonNull)//
					.map(groupConverter::toDto)//
					.collect(toList());
			commit();

			return groups;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized GroupDto save(GroupDto group) {
		log.debug("save");
		Objects.requireNonNull(group);

		try {
			begin();

			final Group toSave = group.getId() != null ? dao.findById(group.getId(), true) : new Group();
			if (toSave == null) {
				throw new EntityNotFoundException("group id=" + group.getId());
			}

			groupConverter.fillEntity(group, toSave);
			dao.makePersistent(toSave);

			final GroupDto dto = groupConverter.toDto(toSave);
			commit();

			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized boolean delete(GroupDto group) {
		log.debug("delete");

		if (group != null && group.getId() != null) {
			try {
				begin();

				Group grp = dao.findById(group.getId(), true);
				dao.makeTransient(grp);

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
	public synchronized boolean groupNameExists(Long id, String name) {
		log.debug("groupNameExists");

		try {
			begin();

			Criterion crit = (id == null) ? Restrictions.eq(FieldConstants.NAME, name)
					: Restrictions.and(Restrictions.eq(FieldConstants.NAME, name),
							Restrictions.ne(FieldConstants.ID, id));
			List<Group> groups = dao.findByCriteria(crit).stream()//
					.filter(Objects::nonNull)//
					.collect(toList());

			commit();
			return !groups.isEmpty();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return false;
	}

}
