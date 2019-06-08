package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.service.OutputService;

public class OutputServiceImpl implements OutputService {

	private static final Logger log = Logger.getLogger(OutputServiceImpl.class);

	private final HibernateDao<BranchOutput, Long> branchOutputDao = new HibernateDao<BranchOutput, Long>(
			BranchOutput.class);

	@Override
	public void saveBranchOutput(long branchId, long testId, String data) {
		log.debug("saveBranchOutput");

		BranchOutput branchOutput = new BranchOutput();
		branchOutput.setBranchId(branchId);
		branchOutput.setTestId(testId);
		branchOutput.setData(data);
		branchOutput.setOutput(data);

		try {
			begin();

			branchOutputDao.makePersistent(branchOutput);

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
	}

}
