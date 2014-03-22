/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.EntityConstants;
import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.dao.SlideOrderDao;
import com.tilioteo.hypothesis.dao.TestDao;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SlideOrder;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.entity.Test;
import com.tilioteo.hypothesis.entity.Test.Status;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TestManager {

	private TestDao testDao;
	private SlideOrderDao slideOrderDao;

	public static TestManager newInstance() {
		return new TestManager(new TestDao(), new SlideOrderDao());
	}
	
	protected TestManager(TestDao testDao, SlideOrderDao slideOrderDao) {
		this.testDao = testDao;
		this.slideOrderDao = slideOrderDao;
	}

	public List<Test> findTestsBy(User user, Pack pack, Status... statuses) {
		try {
			testDao.beginTransaction();
			/*
			 * Integer[] stats = new Integer[statuses.length]; for (int i = 0; i
			 * < statuses.length; ++i) { stats[i] = statuses[i].getCode(); }
			 */

			List<Test> tests = testDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityConstants.PACK, pack), Restrictions
							.and(Restrictions.eq(EntityConstants.USER, user),
									Restrictions.in(EntityFieldConstants.STATUS,
											statuses))));
			testDao.commit();
			return tests;
		} catch (Throwable e) {
			testDao.rollback();
		}
		return null;
	}

	public Test getUnattendedTest(User user, Pack pack, boolean production) {
		try {
			testDao.beginTransaction();
			
			List<Test> tests;
			
			// anonymous user cannot continue broken test 
			if (user != null) {
				// TODO broken test finding is disabled at this time
				// continue of testing must be implemented first
				//tests = testDao.findByCriteria(Restrictions.and(
				//		Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
				//				Restrictions.eq(FieldConstants.USER, user),
				//				Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
				// TODO remove this line after uncommenting code block above
				tests = new ArrayList<Test>();
			} else {
				tests = new ArrayList<Test>();
			}

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

	public SlideOrder findTaskSlideOrder(Test test, Task task) {
		try {
			slideOrderDao.beginTransaction();
			
			List<SlideOrder> slideOrders = slideOrderDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityConstants.TEST, test),
					Restrictions.eq(EntityConstants.TASK, task)));
			slideOrderDao.commit();
			
			if (slideOrders.isEmpty() || slideOrders.size() > 1) {
				return null;
			} else {
				SlideOrder slideOrder = slideOrders.get(0);
				return slideOrder;
			}
			
		} catch (Throwable e) {
			slideOrderDao.rollback();
		}
		return null;
	}

	public void updateSlideOrder(SlideOrder slideOrder) {
		if (slideOrder != null) {
			try {
				slideOrderDao.beginTransaction();
				slideOrderDao.makePersistent(slideOrder);
				slideOrderDao.commit();
			} catch (Throwable e) {
				slideOrderDao.rollback();
				e.getMessage();
			}
		}
	}
}
