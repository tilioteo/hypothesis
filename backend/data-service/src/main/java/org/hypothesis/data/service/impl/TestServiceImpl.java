package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.api.Status;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.EventDto;
import org.hypothesis.data.dto.ScoreDto;
import org.hypothesis.data.dto.TestDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.TableConstants;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Score;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.TestService;

public class TestServiceImpl implements TestService {

	private static final Logger log = Logger.getLogger(TestServiceImpl.class);

	private final HibernateDao<SimpleTest, Long> testDao = new HibernateDao<SimpleTest, Long>(SimpleTest.class);

	private final HibernateDao<Event, Long> eventDao = new HibernateDao<Event, Long>(Event.class);

	private final HibernateDao<Score, Long> scoreDao = new HibernateDao<Score, Long>(Score.class);

	private final HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(User.class);

	private final TestConverter converter = new TestConverter();

	private final EventConverter eventConverter = new EventConverter();

	private final ScoreConverter scoreConverter = new ScoreConverter();

	@Override
	public synchronized TestDto getUnattendedTest(Long userId, long packId, boolean production) {
		log.debug("getUnattendedTest");

		try {
			begin();

			List<SimpleTest> tests;

			// anonymous user cannot continue broken test
			if (userId != null) {
				log.debug("test of user id=" + userId);
				// TODO broken test finding is disabled at this time
				// continue of testing must be implemented first
				// tests = testDao.findByCriteria(Restrictions.and(
				// Restrictions.eq(FieldConstants.PACK, pack), Restrictions.and(
				// Restrictions.eq(FieldConstants.USER, user),
				// Restrictions.ne(FieldConstants.STATUS, Status.FINISHED))));
				// TODO remove this line after uncommenting code block above
				tests = emptyList();
			} else {
				log.debug("test of annonymous user");
				tests = emptyList();
			}

			SimpleTest outputTest = null;
			if (tests.size() > 0) {
				log.debug("getting broken test");
				outputTest = tests.get(0);
			} else {
				log.debug("creating new test instance for pack id=" + packId);
				outputTest = new SimpleTest();
				outputTest.setPackId(packId);
				outputTest.setUserId(userId);
				outputTest.setProduction(production);
				outputTest.setCreated(new Date());
				outputTest.setLastAccess(outputTest.getCreated());
				outputTest.setStatus(Status.CREATED.getCode());
				log.debug("persisting new test instance");
				testDao.makePersistent(outputTest);
			}

			final TestDto dto = converter.toDto(outputTest);

			commit();
			return dto;

		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<TestDto> findManagedTestsOverview(long userId, long packId, Date dateFrom, Date dateTo) {
		log.debug("findManagedTestsOverview");

		try {
			begin();

			Set<Long> userIds = getOwnerUserIds(userId);
			userIds.add(userId);

			Criteria criteria = testDao.createCriteria();

			criteria.add(Restrictions.eq(EntityConstants.PACK_ID, packId))
					.add(Restrictions.in(EntityConstants.USER_ID, userIds));

			if (dateFrom != null) {
				criteria.add(Restrictions.ge(FieldConstants.CREATED, dateFrom));
			}

			if (dateTo != null) {
				criteria.add(Restrictions.le(FieldConstants.CREATED, dateTo));
			}

			criteria.addOrder(Order.asc(FieldConstants.ID));

			final List<SimpleTest> tests = criteria.list();
			final List<TestDto> dtos = tests.stream()//
					.filter(Objects::nonNull)//
					.map(converter::toDto)//
					.collect(toList());

			commit();
			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<TestDto> findManagedScoresOverview(long userId, Date dateFrom, Date dateTo) {
		log.debug("findManagedScoresOverview");

		try {
			begin();

			Set<Long> userIds = getOwnerUserIds(userId);
			userIds.add(userId);

			Criteria criteria = testDao.createCriteria();

			criteria.add(Restrictions.in(EntityConstants.USER_ID, userIds));

			if (dateFrom != null) {
				criteria.add(Restrictions.ge(FieldConstants.CREATED, dateFrom));
			}

			if (dateTo != null) {
				criteria.add(Restrictions.le(FieldConstants.CREATED, dateTo));
			}

			criteria.add(Restrictions.isNotEmpty(FieldConstants.SCORES));

			criteria.addOrder(Order.asc(FieldConstants.ID));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

			List<SimpleTest> tests = criteria.list();
			final List<TestDto> dtos = tests.stream()//
					.filter(Objects::nonNull)//
					.map(converter::toDto)//
					.collect(toList());

			commit();
			return dtos;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}

		return emptyList();
	}

	@Override
	public synchronized void saveEvent(EventDto event, Status status) {
		log.debug("saveEvent");

		Objects.requireNonNull(event);

		final long testId = event.getTestId();

		try {
			begin();

			SimpleTest test = testDao.findById(testId, false);

			if (test != null) {
				Event entity = new Event();
				eventConverter.fillEntity(event, entity);
				entity.setTestId(null);// prevents from inserting into join
										// table

				// update test
				if (status != null && !Objects.equals(test.getStatus(), status.getCode())) {
					test.setStatus(status.getCode());

					switch (status) {
					case BROKEN_BY_CLIENT:
					case BROKEN_BY_ERROR:
						test.setBroken(event.getTimeStamp());
						break;
					case STARTED:
						test.setStarted(event.getTimeStamp());
						break;
					case FINISHED:
						test.setFinished(event.getTimeStamp());
						break;
					default:
						break;
					}
				}

				test.setLastAccess(event.getTimeStamp());
				test.setLastBranchId(event.getBranchId());
				test.setLastTaskId(event.getTaskId());
				test.setLastSlideId(event.getSlideId());

				testDao.makePersistent(test);

				// persist event and test
				eventDao.makePersistent(entity);
				eventDao.flush();

				int rank = getLastTestEventRank(testId);
				saveTestEventJoin(testId, entity.getId(), ++rank);

			} else {
				throw new EntityNotFoundException("test id=" + testId);
			}

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
	}

	@Override
	public synchronized void saveScore(ScoreDto score) {
		log.debug("saveScore");

		Objects.requireNonNull(score);

		final long testId = score.getTestId();

		Score entity = new Score();
		scoreConverter.fillEntity(score, entity);
		entity.setTestId(null); // prevents from inserting into join table

		try {
			begin();

			scoreDao.makePersistent(entity);
			scoreDao.flush();

			int rank = getLastTestScoreRank(testId);
			saveTestScoreJoin(testId, entity.getId(), ++rank);

			commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
	}

	private Set<Long> getOwnerUserIds(long userId) {
		return userDao.findByCriteria(Restrictions.eq(EntityConstants.OWNER_ID, userId)).stream()//
				.filter(Objects::nonNull)//
				.map(User::getId)//
				.collect(toSet());
	}

	private int getLastTestEventRank(long testId) {
		SQLQuery query = testDao.getSession()
				.createSQLQuery("SELECT max(" + FieldConstants.RANK + ") FROM " + TableConstants.TEST_EVENT_TABLE
						+ " WHERE " + FieldConstants.TEST_ID + "=:testId GROUP BY " + FieldConstants.TEST_ID);
		query.setParameter("testId", testId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List<?> results = query.list();

		if (!results.isEmpty()) {
			return (Integer) ((Map<?, ?>) results.get(0)).get("max");
		}

		return 0;
	}

	private void saveTestEventJoin(long testId, long eventId, int rank) {
		SQLQuery query = testDao.getSession()
				.createSQLQuery("INSERT INTO " + TableConstants.TEST_EVENT_TABLE + " (" + FieldConstants.TEST_ID + ","
						+ FieldConstants.EVENT_ID + "," + FieldConstants.RANK + ") VALUES (:testId,:eventId,:rank)");
		query.setParameter("testId", testId);
		query.setParameter("eventId", eventId);
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

	private int getLastTestScoreRank(long testId) {
		SQLQuery query = testDao.getSession()
				.createSQLQuery("SELECT max(" + FieldConstants.RANK + ") FROM " + TableConstants.TEST_SCORE_TABLE
						+ " WHERE " + FieldConstants.TEST_ID + "=:testId GROUP BY " + FieldConstants.TEST_ID);
		query.setParameter("testId", testId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List<?> results = query.list();

		if (!results.isEmpty()) {
			return (Integer) ((HashMap<?, ?>) results.get(0)).get("max");
		}

		return 0;
	}

	private void saveTestScoreJoin(long testId, long scoreId, int rank) {
		SQLQuery query = testDao.getSession()
				.createSQLQuery("INSERT INTO " + TableConstants.TEST_SCORE_TABLE + " (" + FieldConstants.TEST_ID + ","
						+ FieldConstants.SCORE_ID + "," + FieldConstants.RANK + ") VALUES (:testId,:scoreId,:rank)");
		query.setParameter("testId", testId);
		query.setParameter("scoreId", scoreId);
		query.setParameter("rank", rank);
		query.executeUpdate();
	}

}
