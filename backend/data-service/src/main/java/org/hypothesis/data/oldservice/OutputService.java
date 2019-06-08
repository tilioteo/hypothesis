/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldservice;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.oldmodel.BranchOutput;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class OutputService implements Serializable {

	private static final Logger log = Logger.getLogger(OutputService.class);

	private final HibernateDao<BranchOutput, Long> branchOutputDao;

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
			// throw e;
		}
	}

}
