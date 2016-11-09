/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.GenericDao;
import org.hypothesis.data.interfaces.GroupService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class GroupServiceImpl implements GroupService {

	private static final Logger log = Logger.getLogger(GroupServiceImpl.class);

	@Inject
	private GenericDao<Group, Long> groupDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.GroupService#merge(org.hypothesis.data.model.
	 * Group)
	 */
	@Override
	public Group merge(Group group) {
		try {
			groupDao.beginTransaction();
			group = mergeInit(group);
			groupDao.commit();
			return group;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
		return null;
	}

	private Group mergeInit(Group group) {
		groupDao.clear();
		group = groupDao.merge(group);
		Hibernate.initialize(group.getUsers());
		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.GroupService#add(org.hypothesis.data.model.
	 * Group)
	 */
	@Override
	public Group add(Group group) {
		log.debug("addGroup");
		try {
			groupDao.beginTransaction();
			// group = mergeInit(group);
			// groupDao.clear();
			group = groupDao.merge(group);
			group = groupDao.makePersistent(group);
			groupDao.commit();
			return group;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.GroupService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		log.debug("deleteAllGroups");
		try {
			this.findAll().forEach(this::delete);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.GroupService#delete(org.hypothesis.data.model
	 * .Group)
	 */
	@Override
	public void delete(Group group) {
		log.debug("deleteGroup");
		try {
			groupDao.beginTransaction();
			group = mergeInit(group);
			// groupDao.clear();
			groupDao.makeTransient(group);
			groupDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.GroupService#findAll()
	 */
	@Override
	public List<Group> findAll() {
		log.debug("findAllGroups");
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao.findAll();
			groupDao.commit();
			return allGroups;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.GroupService#find(long)
	 */
	@Override
	public Group find(long id) {
		log.debug("findGroup");
		try {
			groupDao.beginTransaction();
			Group grp = groupDao.findById(Long.valueOf(id), true);
			groupDao.commit();
			return grp;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.GroupService#findOwnerGroups(org.hypothesis.
	 * data.model.User)
	 */
	@Override
	public List<Group> findOwnerGroups(User owner) {
		log.debug("findOwnerGroups");
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao
					.findByCriteria(Restrictions.eq(FieldConstants.PROPERTY_OWNER_ID, owner.getId()));
			groupDao.commit();
			return allGroups;
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.GroupService#groupNameExists(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public boolean groupNameExists(Long id, String name) {
		log.debug("groupNameExists");
		try {
			groupDao.beginTransaction();
			Criterion crit = (id == null) ? Restrictions.eq(FieldConstants.NAME, name)
					: Restrictions.and(Restrictions.eq(FieldConstants.NAME, name),
							Restrictions.ne(FieldConstants.ID, id));
			List<Group> groups = groupDao.findByCriteria(crit);
			groupDao.commit();
			return !groups.isEmpty();
		} catch (Exception e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
			return false;
		}
	}

}
