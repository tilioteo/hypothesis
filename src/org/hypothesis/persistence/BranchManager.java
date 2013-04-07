/**
 * 
 */
package org.hypothesis.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.BranchTrek;
import org.hypothesis.entity.Pack;
import org.hypothesis.persistence.hibernate.BranchTrekDao;

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
