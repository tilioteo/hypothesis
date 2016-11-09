/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.interfaces.GenericDao;
import org.hypothesis.data.interfaces.TestService;
import org.hypothesis.data.model.*;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import java.util.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class TestServiceImpl implements TestService {

	private static final Logger log = Logger.getLogger(TestServiceImpl.class);

	@Inject
	private GenericDao<SimpleTest, Long> testDao;
	@Inject
	private GenericDao<Event, Long> eventDao;
	@Inject
	private GenericDao<SlideOrder, Long> slideOrderDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.TestService#findById(java.lang.Long)
	 */
	@Override
	public SimpleTest findById(Long id) {
		log.debug("TestService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			testDao.beginTransaction();

			SimpleTest test = testDao.findById(id, false);
			testDao.commit();
			return test;
		} catch (Exception e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#findTestsBy(org.hypothesis.data.
	 * model.User, org.hypothesis.data.model.Pack,
	 * org.hypothesis.data.model.Status)
	 */
	@Override
	public List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses) {
		log.debug("findTestsBy(User, Pack, Status[])");
		try {
			testDao.beginTransaction();

			Integer[] stats = Arrays.stream(statuses).map(Status::getCode).toArray(s -> new Integer[s]);
			List<SimpleTest> tests = testDao
					.findByCriteria(Restrictions.and(Restrictions.eq(EntityConstants.PACK, pack),
							Restrictions.and(Restrictions.eq(EntityConstants.USER, user),
									Restrictions.in(FieldConstants.STATUS, stats))));
			testDao.commit();
			return tests;
		} catch (Exception e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#findTestsBy(org.hypothesis.data.
	 * model.Pack, java.util.Collection, java.util.Date, java.util.Date)
	 */
	@Override
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

		} catch (Exception e) {
			log.error(e.getMessage());
			testDao.rollback();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#getUnattendedTest(org.hypothesis.
	 * data.model.User, org.hypothesis.data.model.Pack, boolean)
	 */
	@Override
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
				// tests = testDao.findByCriteria(Restrictions.and(
				// Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
				// Restrictions.eq(FieldConstants.USER, user),
				// Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
				// TODO remove this line after uncommenting code block above
				tests = new ArrayList<>();
			} else {
				log.debug("test of annonymous user");
				tests = new ArrayList<>();
			}

			SimpleTest outputTest = null;
			if (!tests.isEmpty()) {
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

		} catch (Exception e) {
			log.error(e.getMessage());
			testDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#updateTest(org.hypothesis.data.
	 * model.SimpleTest)
	 */
	@Override
	public void updateTest(SimpleTest test) {
		if (test != null) {
			log.debug(String.format("updateTest, test id = %s", test.getId() != null ? test.getId() : "NULL"));
			try {
				testDao.beginTransaction();
				testDao.clear();
				test = testDao.merge(test);
				testDao.makePersistent(test);
				testDao.commit();
				log.debug("test update finished");
			} catch (Exception e) {
				log.error(e.getMessage());
				testDao.rollback();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#saveEvent(org.hypothesis.data.
	 * model.Event, org.hypothesis.data.model.SimpleTest)
	 */
	@Override
	public void saveEvent(Event event, SimpleTest test) {
		if (event != null && test != null) {
			log.debug(String.format("saveEvent, test id = %s, event id = %s",
					test.getId() != null ? test.getId() : "NULL", event.getId() != null ? event.getId() : "NULL"));

			try {
				eventDao.beginTransaction();
				eventDao.makePersistent(event);
				eventDao.flush();

				int rank = getLastTestEventRank(test);
				saveTestEventJoin(test, event, ++rank);

				eventDao.commit();
				log.debug("event save finished");
			} catch (Exception e) {
				log.error(e.getMessage());
				eventDao.rollback();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private int getLastTestEventRank(SimpleTest test) {
		log.debug("getLastTestEventRank");
		SQLQuery query = testDao.getSession()
				.createSQLQuery("SELECT max(" + FieldConstants.RANK + ") FROM " + TableConstants.TEST_EVENT_TABLE
						+ " WHERE " + FieldConstants.TEST_ID + "=:testId GROUP BY " + FieldConstants.TEST_ID);
		query.setParameter("testId", test.getId());
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();

		if (!results.isEmpty()) {
			return (Integer) ((HashMap<?, ?>) results.get(0)).get("max");
		}

		return 0;
	}

	private void saveTestEventJoin(SimpleTest test, Event event, int rank) {
		log.debug("saveTestEventJoin");
		SQLQuery query = testDao.getSession()
				.createSQLQuery("INSERT INTO " + TableConstants.TEST_EVENT_TABLE + " (" + FieldConstants.TEST_ID + ","
						+ FieldConstants.EVENT_ID + "," + FieldConstants.RANK + ") VALUES (:testId,:eventId,:rank)");
		query.setParameter("testId", test.getId());
		query.setParameter("eventId", event.getId());
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#findTaskSlideOrder(org.hypothesis
	 * .data.model.SimpleTest, org.hypothesis.data.model.Task)
	 */
	@Override
	public SlideOrder findTaskSlideOrder(SimpleTest test, Task task) {
		log.debug("findTaskSlideOrder");
		try {
			slideOrderDao.beginTransaction();

			List<SlideOrder> slideOrders = slideOrderDao.findByCriteria(Restrictions
					.and(Restrictions.eq(EntityConstants.TEST, test), Restrictions.eq(EntityConstants.TASK, task)));
			slideOrderDao.commit();

			if (slideOrders.isEmpty() || slideOrders.size() > 1) {
				return null;
			} else {
				return slideOrders.get(0);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			slideOrderDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.TestService#updateSlideOrder(org.hypothesis.
	 * data.model.SlideOrder)
	 */
	@Override
	public void updateSlideOrder(SlideOrder slideOrder) {
		log.debug("updateSlideOrder");
		if (slideOrder != null) {
			try {
				slideOrderDao.beginTransaction();
				slideOrderDao.makePersistent(slideOrder);
				slideOrderDao.commit();
			} catch (Exception e) {
				log.error(e.getMessage());
				slideOrderDao.rollback();
			}
		}
	}

}
