package org.hypothesis.data.oldservice;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.oldmodel.PackSet;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class PackSetService implements Serializable {

	private static final Logger log = Logger.getLogger(PackSetService.class);

	private final HibernateDao<PackSet, Long> packSetDao;

	public static PackSetService newInstance() {
		return new PackSetService(new HibernateDao<PackSet, Long>(PackSet.class));
	}

	protected PackSetService(HibernateDao<PackSet, Long> userDao) {
		this.packSetDao = userDao;
	}

	public PackSet merge(PackSet packSet) {
		try {
			packSetDao.beginTransaction();
			packSet = mergeInit(packSet);
			packSetDao.commit();
			return packSet;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packSetDao.rollback();
		}
		return null;
	}

	private PackSet mergeInit(PackSet packSet) {
		packSetDao.clear();
		packSet = packSetDao.merge(packSet);
		Hibernate.initialize(packSet.getPacks());
		return packSet;
	}

	public PackSet add(PackSet packSet) {
		log.debug("addPackSet");
		try {
			packSetDao.beginTransaction();
			packSet = packSetDao.merge(packSet);
			packSet = packSetDao.makePersistent(packSet);
			packSetDao.commit();
			return packSet;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packSetDao.rollback();
		}
		return null;
	}

	public void deleteAll() {
		log.debug("deleteAllPackSets");
		try {
			List<PackSet> allPackSets = this.findAll();
			for (PackSet packSet : allPackSets) {
				this.delete(packSet);
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
	}

	public void delete(PackSet packSet) {
		log.debug("deletePackSet");
		try {
			packSetDao.beginTransaction();
			packSet = mergeInit(packSet);
			packSetDao.makeTransient(packSet);
			packSetDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			packSetDao.rollback();
		}
	}

	public List<PackSet> findAll() {
		log.debug("findAllPackSets");
		try {
			packSetDao.beginTransaction();
			List<PackSet> allPackSets = packSetDao.findAll();
			packSetDao.commit();
			return allPackSets;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packSetDao.rollback();
		}
		return null;
	}

	public PackSet find(long id) {
		log.debug("findPackSet");
		try {
			packSetDao.beginTransaction();
			PackSet packSet = packSetDao.findById(Long.valueOf(id), true);
			packSetDao.commit();
			return packSet;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packSetDao.rollback();
		}
		return null;
	}

}
