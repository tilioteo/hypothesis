/**
 * 
 */
package com.tilioteo.hypothesis.data.service;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.data.model.BranchOutput;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class OutputService implements Serializable {

	private static Logger log = Logger.getLogger(OutputService.class);

	private HibernateDao<BranchOutput, Long> branchOutputDao;

	public static OutputService newInstance() {
		return new OutputService(new HibernateDao<BranchOutput, Long>(BranchOutput.class));
	}
	
	public OutputService(HibernateDao<BranchOutput, Long> branchOutputDao) {
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
