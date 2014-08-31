/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.EntityConstants;
import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;
import com.tilioteo.hypothesis.dao.EventDao;
import com.tilioteo.hypothesis.dao.SlideOrderDao;
import com.tilioteo.hypothesis.dao.TestDao;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SlideOrder;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Status;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class TestManager {

	private static Logger log = Logger.getLogger(TestManager.class);

	private TestDao testDao;
	private EventDao eventDao;
	private SlideOrderDao slideOrderDao;

	public static TestManager newInstance() {
		return new TestManager(new TestDao(), new EventDao(), new SlideOrderDao());
	}
	
	protected TestManager(TestDao testDao, EventDao eventDao, SlideOrderDao slideOrderDao) {
		this.testDao = testDao;
		this.eventDao = eventDao;
		this.slideOrderDao = slideOrderDao;
	}

	public List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses) {
		log.debug("findTestsBy");
		try {
			testDao.beginTransaction();
			/*
			 * Integer[] stats = new Integer[statuses.length]; for (int i = 0; i
			 * < statuses.length; ++i) { stats[i] = statuses[i].getCode(); }
			 */

			List<SimpleTest> tests = testDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityConstants.PACK, pack), Restrictions
							.and(Restrictions.eq(EntityConstants.USER, user),
									Restrictions.in(EntityFieldConstants.STATUS,
											statuses))));
			testDao.commit();
			return tests;
		} catch (Throwable e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}

	public SimpleTest getUnattendedTest(User user, Pack pack, boolean production) {
		log.debug("getUnattendedTest");
		try {
			testDao.beginTransaction();
			
			List<SimpleTest> tests;
			
			// anonymous user cannot continue broken test 
			if (user != null) {
				// TODO broken test finding is disabled at this time
				// continue of testing must be implemented first
				//tests = testDao.findByCriteria(Restrictions.and(
				//		Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
				//				Restrictions.eq(FieldConstants.USER, user),
				//				Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
				// TODO remove this line after uncommenting code block above
				tests = new ArrayList<SimpleTest>();
			} else {
				tests = new ArrayList<SimpleTest>();
			}

			SimpleTest outputTest = null;
			if (tests.size() > 0) {
				outputTest = tests.get(0);
			} else {
				outputTest = new SimpleTest(pack, user);
				outputTest.setProduction(production);
				testDao.makePersistent(outputTest);
			}

			testDao.commit();
			return outputTest;

		} catch (Throwable e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}

	public void updateTest(SimpleTest test) {
		log.debug("updateTest");
		if (test != null) {
			try {
				testDao.beginTransaction();
				testDao.makePersistent(test);
				testDao.commit();
			} catch (Throwable e) {
				log.error(e.getMessage());
				testDao.rollback();
			}
		}
	}
	
	public void saveEvent(Event event, SimpleTest test) {
		log.debug("saveEvent");
		if (event != null && test != null) {
			try {
				eventDao.beginTransaction();
				eventDao.makePersistent(event);
				eventDao.flush();
				
				int rank = getLastTestEventRank(test);
				saveTestEventJoin(test, event, ++rank);
				
				eventDao.commit();
			} catch (Throwable e) {
				log.error(e.getMessage());
				eventDao.rollback();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private int getLastTestEventRank(SimpleTest test) {
		log.debug("getLastTestEventRank");
		SQLQuery query = eventDao.getSession().createSQLQuery(
				"SELECT max("  + EntityFieldConstants.RANK + ") FROM " +
						EntityTableConstants.TEST_EVENT_TABLE + " WHERE " +
						EntityFieldConstants.TEST_ID + "=:testId GROUP BY " +
						EntityFieldConstants.TEST_ID);
		query.setParameter("testId", test.getId());
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		
		if (results.size() > 0) {
			return (Integer)((HashMap<?, ?>)results.get(0)).get("max");
		}
		
		return 0;
	}
	
	private void saveTestEventJoin(SimpleTest test, Event event, int rank) {
		log.debug("saveTestEventJoin");
		SQLQuery query = eventDao.getSession().createSQLQuery(
				"INSERT INTO " + EntityTableConstants.TEST_EVENT_TABLE + " (" +
						EntityFieldConstants.TEST_ID + "," +
						EntityFieldConstants.EVENT_ID + "," +
						EntityFieldConstants.RANK +
						") VALUES (:testId,:eventId,:rank)");
		query.setParameter("testId", test.getId());
		query.setParameter("eventId", event.getId());
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

	public SlideOrder findTaskSlideOrder(SimpleTest test, Task task) {
		log.debug("findTaskSlideOrder");
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
			log.error(e.getMessage());
			slideOrderDao.rollback();
		}
		return null;
	}

	public void updateSlideOrder(SlideOrder slideOrder) {
		log.debug("updateSlideOrder");
		if (slideOrder != null) {
			try {
				slideOrderDao.beginTransaction();
				slideOrderDao.makePersistent(slideOrder);
				slideOrderDao.commit();
			} catch (Throwable e) {
				log.error(e.getMessage());
				slideOrderDao.rollback();
			}
		}
	}
}
