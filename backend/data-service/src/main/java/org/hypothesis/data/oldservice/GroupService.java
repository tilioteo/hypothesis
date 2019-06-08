/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldservice;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.oldmodel.Group;
import org.hypothesis.data.oldmodel.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class GroupService implements Serializable {

	private static final Logger log = Logger.getLogger(GroupService.class);

	private final HibernateDao<Group, Long> groupDao;

	public static GroupService newInstance() {
		return new GroupService(new HibernateDao<Group, Long>(Group.class));
	}

	protected GroupService(HibernateDao<Group, Long> groupDao) {
		this.groupDao = groupDao;
	}

	public Group merge(Group group) {
		try {
			groupDao.beginTransaction();
			group = mergeInit(group);
			groupDao.commit();
			return group;
		} catch (Throwable e) {
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
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
		}
		return null;
	}

	public void deleteAll() {
		log.debug("deleteAllGroups");
		try {
			List<Group> allGroups = this.findAll();
			for (Group group : allGroups) {
				this.delete(group);
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
	}

	public void delete(Group group) {
		log.debug("deleteGroup");
		try {
			groupDao.beginTransaction();
			group = mergeInit(group);
			// groupDao.clear();
			groupDao.makeTransient(group);
			groupDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
	}

	public List<Group> findAll() {
		log.debug("findAllGroups");
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao.findAll();
			groupDao.commit();
			return allGroups;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
		return null;
	}

	public Group find(long id) {
		log.debug("findGroup");
		try {
			groupDao.beginTransaction();
			Group grp = groupDao.findById(Long.valueOf(id), true);
			groupDao.commit();
			return grp;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
		}
		return null;
	}

	public List<Group> findOwnerGroups(User owner) {
		log.debug("findOwnerGroups");
		try {
			groupDao.beginTransaction();
			List<Group> allGroups = groupDao
					.findByCriteria(Restrictions.eq(FieldConstants.PROPERTY_OWNER_ID, owner.getId()));
			groupDao.commit();
			return allGroups;
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
			return null;
		}
	}

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
		} catch (Throwable e) {
			log.error(e.getMessage());
			groupDao.rollback();
			// throw e;
			return false;
		}
	}

}
