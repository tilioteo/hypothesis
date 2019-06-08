/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldservice;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.oldmodel.Branch;
import org.hypothesis.data.oldmodel.BranchMap;
import org.hypothesis.data.oldmodel.BranchTrek;
import org.hypothesis.data.oldmodel.Pack;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class BranchService implements Serializable {

	private static final Logger log = Logger.getLogger(BranchService.class);

	private final HibernateDao<Branch, Long> branchDao;
	private final HibernateDao<BranchTrek, Long> branchTrekDao;

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
