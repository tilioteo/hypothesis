/**
 * 
 */
package org.hypothesis.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.SlideOutput;
import org.hypothesis.entity.Test;
import org.hypothesis.entity.User;
import org.hypothesis.entity.Test.Status;
import org.hypothesis.persistence.hibernate.SlideOutputDao;
import org.hypothesis.persistence.hibernate.TestDao;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TestManager {

	private TestDao testDao;
	private SlideOutputDao slideOutputDao;

	public TestManager(TestDao testDao, SlideOutputDao slideOutputDao) {
		this.testDao = testDao;
		this.slideOutputDao = slideOutputDao;
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
	
	@SuppressWarnings("unchecked")
	public List<Test> findFinishedTestsBy(Pack pack, Date dateFrom, Date dateTo) {
		try {
			testDao.beginTransaction();
			
			Criteria criteria = testDao.createCriteria();
			
			if (null == dateFrom) {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PACK, pack),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.le(FieldConstants.CREATED, dateTo)));
			} else if (null == dateTo) {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PACK, pack),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.ge(FieldConstants.CREATED, dateFrom)));
			} else {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PACK, pack),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.between(FieldConstants.CREATED, dateFrom, dateTo)));
			}
			
			criteria.addOrder(Order.asc(FieldConstants.ID));
			return criteria.list();
			
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
	
	public SlideOutput findSlideOutput(Test test, Slide slide) {
		try {
			slideOutputDao.beginTransaction();
			List<SlideOutput> slideOutputs = slideOutputDao.findByCriteria(Restrictions.and(
					Restrictions.eq(FieldConstants.TEST, test),
					Restrictions.eq(FieldConstants.SLIDE, slide)));
			slideOutputDao.commit();
			
			if (!slideOutputs.isEmpty()) {
				/*for (SlideOutput output : slideOutputs) {
					if (output != null) {
						return output;
					}
				}*/
				return slideOutputs.get(0);
			}
		} catch (Throwable e) {
			slideOutputDao.rollback();
			e.getMessage();
		}
		return null;
	}

}
