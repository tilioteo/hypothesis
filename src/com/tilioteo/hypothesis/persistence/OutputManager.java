/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import com.tilioteo.hypothesis.dao.BranchOutputDao;
import com.tilioteo.hypothesis.entity.BranchOutput;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class OutputManager implements Serializable {

	private static Logger log = Logger.getLogger(OutputManager.class);

	private BranchOutputDao branchOutputDao;

	public static OutputManager newInstance() {
		return new OutputManager(new BranchOutputDao());
	}
	
	public OutputManager(BranchOutputDao branchOutputDao) {
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

}
