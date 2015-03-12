/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.apache.log4j.Logger;
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

	private static Logger log = Logger.getLogger(OutputManager.class);

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
		log.debug("saveBranchOutput");
		try {
			branchOutputDao.beginTransaction();
			branchOutputDao.makePersistent(branchOutput);
			branchOutputDao.commit();
		} catch (HibernateException e) {
			log.error(e.getMessage());
			branchOutputDao.rollback();
			//throw e;
		}
	}

	public void saveSlideOutput(SlideOutput slideOutput) {
		log.debug("saveSlideOutput");
		try {
			slideOutputDao.beginTransaction();
			slideOutputDao.makePersistent(slideOutput);
			slideOutputDao.commit();
		} catch (HibernateException e) {
			log.error(e.getMessage());
			slideOutputDao.rollback();
			//throw e;
		}
	}

}
