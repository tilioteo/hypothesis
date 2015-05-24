/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.dao.BranchOutputDao;
import com.tilioteo.hypothesis.entity.BranchOutput;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class OutputService implements Serializable {

	private static Logger log = Logger.getLogger(OutputService.class);

	private BranchOutputDao branchOutputDao;

	public static OutputService newInstance() {
		return new OutputService(new BranchOutputDao());
	}
	
	public OutputService(BranchOutputDao branchOutputDao) {
		this.branchOutputDao = branchOutputDao;
	}

	public void saveBranchOutput(BranchOutput branchOutput) {
		log.debug("saveBranchOutput");
		try {
			branchOutputDao.beginTransaction();
			branchOutputDao.makePersistent(branchOutput);
			branchOutputDao.commit();
		} catch (Throwable e) {
			log.error(e.getMessage());
			branchOutputDao.rollback();
			//throw e;
		}
	}

}
