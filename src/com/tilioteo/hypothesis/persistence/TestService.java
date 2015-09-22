/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.dao.EventDao;
import com.tilioteo.hypothesis.dao.SlideOrderDao;
import com.tilioteo.hypothesis.dao.TestDao;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.SlideOrder;
import com.tilioteo.hypothesis.entity.Status;
import com.tilioteo.hypothesis.entity.TableConstants;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class TestService implements Serializable {

	private static Logger log = Logger.getLogger(TestService.class);

	private TestDao testDao;
	private EventDao eventDao;
	private SlideOrderDao slideOrderDao;
	
	public static TestService newInstance() {
		return new TestService(new TestDao(), new EventDao(),
				new SlideOrderDao());
	}
	
	protected TestService(TestDao testDao, EventDao eventDao, SlideOrderDao slideOrderDao) {
		this.testDao = testDao;
		this.eventDao = eventDao;
		this.slideOrderDao = slideOrderDao;
	}

	public List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses) {
		log.debug("findTestsBy(User, Pack, Status[])");
		try {
			testDao.beginTransaction();
			
			int i = 0;
			Integer[] stats = new Integer[statuses.length];
			for (Status status : statuses){
				stats[i++] = status.getCode();
			}

			List<SimpleTest> tests = testDao.findByCriteria(Restrictions.and(
					Restrictions.eq(EntityConstants.PACK, pack), Restrictions
							.and(Restrictions.eq(EntityConstants.USER, user),
									Restrictions.in(FieldConstants.STATUS,
											stats))));
			testDao.commit();
			return tests;
		} catch (Throwable e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SimpleTest> findTestsBy(Pack pack, Collection<User> users, Date dateFrom, Date dateTo) {
		log.debug("findTestsBy(Pack, Collection<User>, Date, Date)");
		try {
			testDao.beginTransaction();

			Criteria criteria = testDao.createCriteria();
			
			criteria.add(Restrictions.eq(EntityConstants.PACK, pack));
			
			if (users != null) {
				criteria.add(Restrictions.in(EntityConstants.USER, users));
			}
			
			if (dateFrom != null) {
				criteria.add(Restrictions.ge(FieldConstants.CREATED, dateFrom));
			}
			
			if (dateTo != null) {
				criteria.add(Restrictions.le(FieldConstants.CREATED, dateTo));
			}
			
			criteria.addOrder(Order.asc(FieldConstants.ID));
			
			List<SimpleTest> tests = criteria.list();
			testDao.commit();
			
			return tests;
			
		} catch (Throwable e) {
			testDao.rollback();
			log.error(e.getMessage());
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
				log.debug("test of user " + user.getUsername() + " (" + user.getId() + ")");
				// TODO broken test finding is disabled at this time
				// continue of testing must be implemented first
				//tests = testDao.findByCriteria(Restrictions.and(
				//		Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
				//				Restrictions.eq(FieldConstants.USER, user),
				//				Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
				// TODO remove this line after uncommenting code block above
				tests = new ArrayList<SimpleTest>();
			} else {
				log.debug("test of annonymous user");
				tests = new ArrayList<SimpleTest>();
			}

			SimpleTest outputTest = null;
			if (tests.size() > 0) {
				log.debug("getting broken test");
				outputTest = tests.get(0);
			} else {
				log.debug("creating new test instance for pack id=" + pack.getId());
				outputTest = new SimpleTest(pack, user);
				outputTest.setProduction(production);
				log.debug("persisting new test instance");
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
		log.debug(String.format("updateTest, test id = %s", test.getId() != null ? test.getId() : "NULL"));
		if (test != null) {
			try {
				testDao.beginTransaction();
				testDao.clear();
				test = testDao.merge(test);
				testDao.makePersistent(test);
				testDao.commit();
				log.debug("test update finished");
			} catch (Throwable e) {
				log.error(e.getMessage());
				testDao.rollback();
			}
		}
	}
	
	public void saveEvent(Event event, SimpleTest test) {
		log.debug(String.format("saveEvent, test id = %s, event id = %s", test.getId() != null ? test.getId() : "NULL", event.getId() != null ? event.getId() : "NULL"));
		if (event != null && test != null) {
			try {
				eventDao.beginTransaction();
				eventDao.makePersistent(event);
				eventDao.flush();
				
				int rank = getLastTestEventRank(test);
				saveTestEventJoin(test, event, ++rank);
				
				eventDao.commit();
				log.debug("event save finished");
			} catch (Throwable e) {
				log.error(e.getMessage());
				eventDao.rollback();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private int getLastTestEventRank(SimpleTest test) {
		log.debug("getLastTestEventRank");
		SQLQuery query = testDao.getSession().createSQLQuery(
				"SELECT max("  + FieldConstants.RANK + ") FROM " +
						TableConstants.TEST_EVENT_TABLE + " WHERE " +
						FieldConstants.TEST_ID + "=:testId GROUP BY " +
						FieldConstants.TEST_ID);
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
		SQLQuery query = testDao.getSession().createSQLQuery(
				"INSERT INTO " + TableConstants.TEST_EVENT_TABLE + " (" +
						FieldConstants.TEST_ID + "," +
						FieldConstants.EVENT_ID + "," +
						FieldConstants.RANK +
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
