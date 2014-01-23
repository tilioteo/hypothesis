/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.FieldConstants;
import com.tilioteo.hypothesis.dao.BranchTrekDao;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchTrek;
import com.tilioteo.hypothesis.entity.Pack;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchManager {

	private BranchTrekDao branchTrekDao;

	public BranchManager(BranchTrekDao branchTrekDao) {
		this.branchTrekDao = branchTrekDao;
	}

	public Map<String, BranchTrek> getBranchTreks(Pack pack, Branch branch) {
		try {
			HashMap<String, BranchTrek> map = new HashMap<String, BranchTrek>();
			branchTrekDao.beginTransaction();
			List<BranchTrek> branchTreks = branchTrekDao
					.findByCriteria(Restrictions.and(
							Restrictions.eq(FieldConstants.PACK, pack),
							Restrictions.eq(FieldConstants.BRANCH, branch)));

			for (BranchTrek branchTrek : branchTreks) {
				map.put(branchTrek.getKey(), branchTrek);
			}

			return map;
		} catch (Throwable e) {
		}

		return null;
	}
}
