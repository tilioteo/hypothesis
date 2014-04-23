/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.hibernate.HibernateException;

import com.tilioteo.hypothesis.dao.BranchOutputDao;
import com.tilioteo.hypothesis.dao.SlideOutputDao;
import com.tilioteo.hypothesis.entity.BranchOutput;
import com.tilioteo.hypothesis.entity.SlideOutput;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class OutputManager {

	private SlideOutputDao slideOutputDao;
	private BranchOutputDao branchOutputDao;

	public static OutputManager newInstance() {
		return new OutputManager(new SlideOutputDao(), new BranchOutputDao());
	}
	
	public OutputManager(SlideOutputDao slideOutputDao,
			BranchOutputDao branchOutputDao) {
		this.slideOutputDao = slideOutputDao;
		this.branchOutputDao = branchOutputDao;
	}

	public void saveBranchOutput(BranchOutput branchOutput) {
		try {
			branchOutputDao.beginTransaction();
			branchOutputDao.makePersistent(branchOutput);
			branchOutputDao.commit();
		} catch (HibernateException e) {
			branchOutputDao.rollback();
			throw e;
		}
	}

	public void saveSlideOutput(SlideOutput slideOutput) {
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
