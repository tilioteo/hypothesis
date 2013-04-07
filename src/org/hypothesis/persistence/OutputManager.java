/**
 * 
 */
package org.hypothesis.core;

import org.hibernate.HibernateException;
import org.hypothesis.entity.BranchOutput;
import org.hypothesis.entity.SlideOutput;
import org.hypothesis.persistence.hibernate.BranchOutputDao;
import org.hypothesis.persistence.hibernate.SlideOutputDao;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class OutputManager {

	private SlideOutputDao slideOutputDao;
	private BranchOutputDao branchOutputDao;

	public OutputManager(SlideOutputDao slideOutputDao,
			BranchOutputDao branchOutputDao) {
		this.slideOutputDao = slideOutputDao;
		this.branchOutputDao = branchOutputDao;
	}

	public void addBranchOutput(BranchOutput branchOutput) {
		try {
			branchOutputDao.beginTransaction();
			branchOutputDao.makePersistent(branchOutput);
			branchOutputDao.commit();
		} catch (HibernateException e) {
			branchOutputDao.rollback();
			throw e;
		}
	}

	public void addSlideOutput(SlideOutput slideOutput) {
		try {
			slideOutputDao.beginTransaction();
			slideOutputDao.makePersistent(slideOutput);
			slideOutputDao.commit();
		} catch (HibernateException e) {
			slideOutputDao.rollback();
			throw e;
		}
	}

}
