/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.api.Status;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.TableConstants;
import org.hypothesis.data.oldmodel.Event;
import org.hypothesis.data.oldmodel.Pack;
import org.hypothesis.data.oldmodel.Score;
import org.hypothesis.data.oldmodel.SimpleTest;
import org.hypothesis.data.oldmodel.SlideOrder;
import org.hypothesis.data.oldmodel.Task;
import org.hypothesis.data.oldmodel.Test;
import org.hypothesis.data.oldmodel.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class TestService implements Serializable {

	private static final Logger log = Logger.getLogger(TestService.class);

	private final HibernateDao<SimpleTest, Long> simpleTestDao;
	private final HibernateDao<Test, Long> testDao;
	private final HibernateDao<Event, Long> eventDao;
	private final HibernateDao<Score, Long> scoreDao;
	private final HibernateDao<SlideOrder, Long> slideOrderDao;

	public static TestService newInstance() {
		return new TestService(new HibernateDao<SimpleTest, Long>(SimpleTest.class),
				new HibernateDao<Test, Long>(Test.class), new HibernateDao<Event, Long>(Event.class),
				new HibernateDao<Score, Long>(Score.class), new HibernateDao<SlideOrder, Long>(SlideOrder.class));
	}

	protected TestService(HibernateDao<SimpleTest, Long> simpleTestDao, HibernateDao<Test, Long> testDao,
			HibernateDao<Event, Long> eventDao, HibernateDao<Score, Long> scoreDao,
			HibernateDao<SlideOrder, Long> slideOrderDao) {
		this.simpleTestDao = simpleTestDao;
		this.testDao = testDao;
		this.eventDao = eventDao;
		this.scoreDao = scoreDao;
		this.slideOrderDao = slideOrderDao;
	}

	public SimpleTest findById(Long id) {
		log.debug("TestService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			simpleTestDao.beginTransaction();

			SimpleTest test = simpleTestDao.findById(id, false);
			simpleTestDao.commit();
			return test;
		} catch (Throwable e) {
			log.error(e.getMessage());
			simpleTestDao.rollback();
		}
		return null;
	}

	public List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses) {
		log.debug("findTestsBy(User, Pack, Status[])");
		try {
			if (statuses != null && statuses.length > 0) {
				simpleTestDao.beginTransaction();

				int i = 0;
				Integer[] stats = new Integer[statuses.length];
				for (Status status : statuses) {
					stats[i++] = status.getCode();
				}

				List<SimpleTest> tests = simpleTestDao
						.findByCriteria(Restrictions.and(Restrictions.eq(EntityConstants.PACK, pack),
								Restrictions.and(Restrictions.eq(EntityConstants.USER, user),
										Restrictions.in(FieldConstants.STATUS, stats))));
				simpleTestDao.commit();
				return tests;
			} else {
				return Collections.emptyList();
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
			simpleTestDao.rollback();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<SimpleTest> findTestsBy(Pack pack, Collection<User> users, Date dateFrom, Date dateTo) {
		log.debug("findTestsBy(Pack, Collection<User>, Date, Date)");
		try {
			simpleTestDao.beginTransaction();

			Criteria criteria = simpleTestDao.createCriteria();

			criteria.add(Restrictions.eq(EntityConstants.PACK, pack));

			if (users != null && !users.isEmpty()) {
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
			simpleTestDao.commit();

			return tests;

		} catch (Throwable e) {
			log.error(e.getMessage());
			simpleTestDao.rollback();
		}

		return null;
	}

	/*
	 * @SuppressWarnings("unchecked") public List<SimpleTest>
	 * findTestScoresBy(Collection<User> users, Date dateFrom, Date dateTo) {
	 * log.debug("findTestScoresBy(Collection<User>, Date, Date)"); try {
	 * testDao.beginTransaction(); StringBuilder sb = new StringBuilder(
	 * "SELECT DISTINCT a.* FROM " + TableConstants.TEST_TABLE + " a," +
	 * TableConstants.TEST_SCORE_TABLE + " b WHERE a." + FieldConstants.ID +
	 * "=b." + FieldConstants.TEST_ID); if (dateFrom != null) {
	 * sb.append(" AND a." + FieldConstants.CREATED + ">=:dateFrom"); } if
	 * (dateTo != null) { sb.append(" AND a." + FieldConstants.CREATED +
	 * "<=:dateTo"); }
	 * 
	 * SQLQuery query = testDao.getSession().createSQLQuery(sb.toString()); if
	 * (dateFrom != null) { query.setParameter("dateFrom", dateFrom); } if
	 * (dateTo != null) { query.setParameter("dateTo", dateTo); }
	 * query.addEntity(SimpleTest.class);
	 * query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
	 * List<SimpleTest> tests = query.list(); testDao.commit();
	 * 
	 * return tests;
	 * 
	 * } catch (Throwable e) { log.error(e.getMessage()); testDao.rollback(); }
	 * 
	 * return null; }
	 */

	@SuppressWarnings("unchecked")
	public List<Test> findTestScoresBy(Collection<User> users, Date dateFrom, Date dateTo) {
		log.debug("findTestScoresBy(Collection<User>, Date, Date)");
		try {
			testDao.beginTransaction();

			Criteria criteria = testDao.createCriteria();

			if (users != null && !users.isEmpty()) {
				criteria.add(Restrictions.in(EntityConstants.USER, users));
			}

			if (dateFrom != null) {
				criteria.add(Restrictions.ge(FieldConstants.CREATED, dateFrom));
			}

			if (dateTo != null) {
				criteria.add(Restrictions.le(FieldConstants.CREATED, dateTo));
			}

			criteria.add(Restrictions.isNotEmpty(FieldConstants.SCORES));

			criteria.addOrder(Order.asc(FieldConstants.ID));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

			List<Test> tests = criteria.list();
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
			simpleTestDao.beginTransaction();

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
			if (tests.size() > 0) {
				log.debug("getting broken test");
				outputTest = tests.get(0);
			} else {
				log.debug("creating new test instance for pack id=" + pack.getId());
				outputTest = new SimpleTest(pack, user);
				outputTest.setProduction(production);
				log.debug("persisting new test instance");
				simpleTestDao.makePersistent(outputTest);
			}

			simpleTestDao.commit();
			return outputTest;

		} catch (Throwable e) {
			log.error(e.getMessage());
			simpleTestDao.rollback();
		}
		return null;
	}

	public void updateTest(SimpleTest test) {
		if (test != null) {
			log.debug(String.format("updateTest, test id = %s", test.getId() != null ? test.getId() : "NULL"));
			try {
				simpleTestDao.beginTransaction();
				simpleTestDao.clear();
				test = simpleTestDao.merge(test);
				simpleTestDao.makePersistent(test);
				simpleTestDao.commit();
				log.debug("test update finished");
			} catch (Throwable e) {
				log.error(e.getMessage());
				simpleTestDao.rollback();
			}
		}
	}

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
			} catch (Throwable e) {
				log.error(e.getMessage());
				eventDao.rollback();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private int getLastTestEventRank(SimpleTest test) {
		log.debug("getLastTestEventRank");
		SQLQuery query = simpleTestDao.getSession()
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
		SQLQuery query = simpleTestDao.getSession()
				.createSQLQuery("INSERT INTO " + TableConstants.TEST_EVENT_TABLE + " (" + FieldConstants.TEST_ID + ","
						+ FieldConstants.EVENT_ID + "," + FieldConstants.RANK + ") VALUES (:testId,:eventId,:rank)");
		query.setParameter("testId", test.getId());
		query.setParameter("eventId", event.getId());
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

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

	public void saveScore(Score score, SimpleTest test) {
		if (score != null && test != null) {
			log.debug(String.format("saveScore, test id = %s, score id = %s",
					test.getId() != null ? test.getId() : "NULL", score.getId() != null ? score.getId() : "NULL"));

			try {
				scoreDao.beginTransaction();
				scoreDao.makePersistent(score);
				scoreDao.flush();

				int rank = getLastTestScoreRank(test);
				saveTestScoreJoin(test, score, ++rank);

				scoreDao.commit();
				log.debug("score save finished");
			} catch (Throwable e) {
				log.error(e.getMessage());
				scoreDao.rollback();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private int getLastTestScoreRank(SimpleTest test) {
		log.debug("getLastTestScoreRank");
		SQLQuery query = simpleTestDao.getSession()
				.createSQLQuery("SELECT max(" + FieldConstants.RANK + ") FROM " + TableConstants.TEST_SCORE_TABLE
						+ " WHERE " + FieldConstants.TEST_ID + "=:testId GROUP BY " + FieldConstants.TEST_ID);
		query.setParameter("testId", test.getId());
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();

		if (!results.isEmpty()) {
			return (Integer) ((HashMap<?, ?>) results.get(0)).get("max");
		}

		return 0;
	}

	private void saveTestScoreJoin(SimpleTest test, Score score, int rank) {
		log.debug("saveTestScoreJoin");
		SQLQuery query = simpleTestDao.getSession()
				.createSQLQuery("INSERT INTO " + TableConstants.TEST_SCORE_TABLE + " (" + FieldConstants.TEST_ID + ","
						+ FieldConstants.SCORE_ID + "," + FieldConstants.RANK + ") VALUES (:testId,:scoreId,:rank)");
		query.setParameter("testId", test.getId());
		query.setParameter("scoreId", score.getId());
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

}
