/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.util.List;

import javax.enterprise.inject.Default;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.BranchService;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchMap;
import org.hypothesis.data.model.BranchTrek;
import org.hypothesis.data.model.Pack;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class BranchServiceImpl implements BranchService {

	private static final Logger log = Logger.getLogger(BranchServiceImpl.class);

	private final HibernateDao<Branch, Long> branchDao;
	private final HibernateDao<BranchTrek, Long> branchTrekDao;

	public BranchServiceImpl() {
		branchDao = new HibernateDao<>(Branch.class);
		branchTrekDao = new HibernateDao<>(BranchTrek.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.BranchService#findById(java.lang.Long)
	 */
	@Override
	public Branch findById(Long id) {
		log.debug("BranchService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			branchDao.beginTransaction();

			Branch branch = branchDao.findById(id, false);
			branchDao.commit();
			return branch;
		} catch (Exception e) {
			log.error(e.getMessage());
			branchDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.BranchService#getBranchMap(org.hypothesis.
	 * data.model.Pack, org.hypothesis.data.model.Branch)
	 */
	@Override
	public BranchMap getBranchMap(Pack pack, Branch branch) {
		log.debug("getBranchMap");
		try {
			BranchMap branchMap = new BranchMap();
			branchTrekDao.beginTransaction();
			List<BranchTrek> branchTreks = branchTrekDao.findByCriteria(Restrictions
					.and(Restrictions.eq(EntityConstants.PACK, pack), Restrictions.eq(EntityConstants.BRANCH, branch)));

			branchTreks.forEach(e -> branchMap.put(e.getKey(), e.getNextBranch()));
			branchTrekDao.commit();
			return branchMap;
		} catch (Exception e) {
			log.error(e.getMessage());
			branchTrekDao.rollback();
		}

		return null;
	}
}
