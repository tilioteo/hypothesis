/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.FieldConstants;
import com.tilioteo.hypothesis.dao.TestDao;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Test;
import com.tilioteo.hypothesis.entity.Test.Status;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TestManager {

	private TestDao testDao;

	public static TestManager newInstance() {
		return new TestManager(new TestDao());
	}
	
	protected TestManager(TestDao testDao) {
		this.testDao = testDao;
	}

	public List<Test> findTestsBy(User user, Pack pack, Status... statuses) {
		try {
			testDao.beginTransaction();
			/*
			 * Integer[] stats = new Integer[statuses.length]; for (int i = 0; i
			 * < statuses.length; ++i) { stats[i] = statuses[i].getCode(); }
			 */

			List<Test> tests = testDao.findByCriteria(Restrictions.and(
					Restrictions.eq(FieldConstants.PACK, pack), Restrictions
							.and(Restrictions.eq(FieldConstants.USER, user),
									Restrictions.in(FieldConstants.STATUS,
											statuses))));
			testDao.commit();
			return tests;
		} catch (Throwable e) {
			testDao.rollback();
		}
		return null;
	}

	public Test getUnattendTest(User user, Pack pack, boolean production) {
		try {
			testDao.beginTransaction();

			// TODO broken test finding is disabled at this time
			// continue of testing must be implemented first
			/*
			 * List<Test> tests = testDao.findByCriteria(Restrictions.and(
			 * Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
			 * Restrictions.eq(FieldConstants.USER, user),
			 * Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
			 */
			// TODO remove this line after uncommenting code block above
			List<Test> tests = new ArrayList<Test>();

			Test outputTest = null;
			if (tests.size() > 0) {
				outputTest = tests.get(0);
			} else {
				outputTest = new Test(pack, user);
				outputTest.setProduction(production);
				testDao.makePersistent(outputTest);
			}

			testDao.commit();
			return outputTest;

		} catch (Throwable e) {
			testDao.rollback();
			e.getMessage();
		}
		return null;
	}

	public void updateTest(Test test) {
		if (test != null) {
			try {
				testDao.beginTransaction();
				testDao.makePersistent(test);
				testDao.commit();
			} catch (Throwable e) {
				testDao.rollback();
				e.getMessage();
			}
		}
	}
}
