/**
 * 
 */
package com.tilioteo.hypothesis.data.service;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.data.model.Branch;
import com.tilioteo.hypothesis.data.model.BranchMap;
import com.tilioteo.hypothesis.data.model.BranchTrek;
import com.tilioteo.hypothesis.data.model.Pack;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class BranchService implements Serializable {

	private static Logger log = Logger.getLogger(BranchService.class);

	private HibernateDao<Branch, Long> branchDao;
	private HibernateDao<BranchTrek, Long> branchTrekDao;

	public static BranchService newInstance() {
		return new BranchService(new HibernateDao<Branch, Long>(Branch.class),
				new HibernateDao<BranchTrek, Long>(BranchTrek.class));
	}

	protected BranchService(HibernateDao<Branch, Long> branchDao, HibernateDao<BranchTrek, Long> branchTrekDao) {
		this.branchDao = branchDao;
		this.branchTrekDao = branchTrekDao;
	}

	public Branch findById(Long id) {
		log.debug("BranchService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			branchDao.beginTransaction();

			Branch branch = branchDao.findById(id, false);
			branchDao.commit();
			return branch;
		} catch (Throwable e) {
			log.error(e.getMessage());
			branchDao.rollback();
		}
		return null;
	}

	public BranchMap getBranchMap(Pack pack, Branch branch) {
		log.debug("getBranchMap");
		try {
			BranchMap branchMap = new BranchMap();
			branchTrekDao.beginTransaction();
			List<BranchTrek> branchTreks = branchTrekDao.findByCriteria(Restrictions
					.and(Restrictions.eq(EntityConstants.PACK, pack), Restrictions.eq(EntityConstants.BRANCH, branch)));

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
