/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.OutputService;
import org.hypothesis.data.model.BranchOutput;

import javax.enterprise.inject.Default;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class OutputServiceImpl implements OutputService {

	private static final Logger log = Logger.getLogger(OutputServiceImpl.class);

	private final HibernateDao<BranchOutput, Long> branchOutputDao;

	public OutputServiceImpl() {
		branchOutputDao = new HibernateDao<BranchOutput, Long>(BranchOutput.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.OutputService#saveBranchOutput(org.hypothesis
	 * .data.model.BranchOutput)
	 */
	@Override
	public void saveBranchOutput(BranchOutput branchOutput) {
		log.debug("saveBranchOutput");
		try {
			branchOutputDao.beginTransaction();
			branchOutputDao.makePersistent(branchOutput);
			branchOutputDao.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			branchOutputDao.rollback();
			// throw e;
		}
	}

}
