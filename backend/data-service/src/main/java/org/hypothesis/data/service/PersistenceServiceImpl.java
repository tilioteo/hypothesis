/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.interfaces.PersistenceService;
import org.hypothesis.data.model.*;

import javax.enterprise.inject.Default;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class PersistenceServiceImpl implements PersistenceService {

	private static final Logger log = Logger.getLogger(PersistenceServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PersistenceService#merge(org.hypothesis.data.
	 * model.Pack)
	 */
	@Override
	public Pack merge(Pack entity) {
		log.debug(String.format("merge(pack id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				Pack pack = (Pack) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(pack.getBranches());
				HibernateUtil.commitTransaction();

				return pack;
			} catch (Exception e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PersistenceService#merge(org.hypothesis.data.
	 * model.Branch)
	 */
	@Override
	public Branch merge(Branch entity) {
		log.debug(String.format("merge(branch id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				Branch branch = (Branch) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(branch.getTasks());
				HibernateUtil.commitTransaction();

				return branch;
			} catch (Exception e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PersistenceService#merge(org.hypothesis.data.
	 * model.Task)
	 */
	@Override
	public Task merge(Task entity) {
		log.debug(String.format("merge(task id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				Task task = (Task) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(task.getSlides());
				HibernateUtil.commitTransaction();

				return task;
			} catch (Exception e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PersistenceService#merge(org.hypothesis.data.
	 * model.Slide)
	 */
	@Override
	public Slide merge(Slide entity) {
		log.debug(String.format("merge(slide id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				Slide slide = (Slide) HibernateUtil.getSession().merge(entity);
				HibernateUtil.commitTransaction();

				return slide;
			} catch (Exception e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.PersistenceService#merge(org.hypothesis.data.
	 * model.SimpleTest)
	 */
	@Override
	public SimpleTest merge(SimpleTest entity) {
		log.debug(String.format("merge(test id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				SimpleTest test = (SimpleTest) HibernateUtil.getSession().merge(entity);
				HibernateUtil.commitTransaction();

				return test;
			} catch (Exception e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

}
