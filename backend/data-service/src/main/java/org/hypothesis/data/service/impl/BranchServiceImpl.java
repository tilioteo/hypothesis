package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.BranchKeyMap;
import org.hypothesis.data.dto.BranchPathMap;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.model.BranchTrek;
import org.hypothesis.data.service.BranchService;

public class BranchServiceImpl implements BranchService {

	private static final Logger log = Logger.getLogger(BranchServiceImpl.class);

	private final HibernateDao<BranchTrek, Long> branchTrekDao = new HibernateDao<BranchTrek, Long>(BranchTrek.class);

	@Override
	public synchronized BranchPathMap getBranchPathMap(long packId) {
		log.debug("getBranchPathMap");

		try {
			begin();

			List<BranchTrek> branchTreks = branchTrekDao
					.findByCriteria(Restrictions.eq(EntityConstants.PACK_ID, packId));

			final BranchPathMap branchPathMap = new BranchPathMap();

			for (BranchTrek branchTrek : branchTreks) {
				long branchId = branchTrek.getBranchId();
				BranchKeyMap branchKeyMap = branchPathMap.get(branchId);
				if (branchKeyMap == null) {
					branchKeyMap = new BranchKeyMap();
					branchPathMap.put(branchId, branchKeyMap);
				}

				branchKeyMap.put(branchTrek.getKey(), branchTrek.getNextBranchId());
			}

			commit();
			return branchPathMap;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return null;
	}

}
