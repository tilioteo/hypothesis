/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.dao.BranchTrekDao;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchMap;
import com.tilioteo.hypothesis.entity.BranchTrek;
import com.tilioteo.hypothesis.entity.Pack;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchManager {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private BranchTrekDao branchTrekDao;

	public static BranchManager newInstance() {
		return new BranchManager(new BranchTrekDao());
	}
	
	protected BranchManager(BranchTrekDao branchTrekDao) {
		this.branchTrekDao = branchTrekDao;
	}

	public BranchMap getBranchMap(Pack pack, Branch branch) {
		log.debug("getBranchMap");
		try {
			BranchMap branchMap = new BranchMap();
			branchTrekDao.beginTransaction();
			List<BranchTrek> branchTreks = branchTrekDao
					.findByCriteria(Restrictions.and(
							Restrictions.eq(EntityConstants.PACK, pack),
							Restrictions.eq(EntityConstants.BRANCH, branch)));

			for (BranchTrek branchTrek : branchTreks) {
				branchMap.put(branchTrek.getKey(), branchTrek.getNextBranch());
			}
			branchTrekDao.commit();
			return branchMap;
		} catch (Throwable e) {
			log.error(e.getMessage());
			branchTrekDao.rollback();
		}

		return null;
	}
}
