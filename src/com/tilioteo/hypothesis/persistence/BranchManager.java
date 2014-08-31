/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.EntityConstants;
import com.tilioteo.hypothesis.dao.BranchTrekDao;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchTrek;
import com.tilioteo.hypothesis.entity.Pack;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchManager {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private BranchTrekDao branchTrekDao;

	public BranchManager(BranchTrekDao branchTrekDao) {
		this.branchTrekDao = branchTrekDao;
	}

	public Map<String, BranchTrek> getBranchTreks(Pack pack, Branch branch) {
		log.debug("getBranchTreks");
		try {
			HashMap<String, BranchTrek> map = new HashMap<String, BranchTrek>();
			branchTrekDao.beginTransaction();
			List<BranchTrek> branchTreks = branchTrekDao
					.findByCriteria(Restrictions.and(
							Restrictions.eq(EntityConstants.PACK, pack),
							Restrictions.eq(EntityConstants.BRANCH, branch)));

			for (BranchTrek branchTrek : branchTreks) {
				map.put(branchTrek.getKey(), branchTrek);
			}

			return map;
		} catch (Throwable e) {
			log.error(e.getMessage());
			branchTrekDao.rollback();
		}

		return null;
	}
}
