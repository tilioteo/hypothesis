/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.interfaces.ExportService;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.model.FieldConstants;

import javax.enterprise.inject.Default;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class ExportServiceImpl implements ExportService {

	private static final Logger log = Logger.getLogger(ExportServiceImpl.class);

	private final HibernateDao<ExportEvent, Long> exportEventDao;
	private final HibernateDao<ExportScore, Long> exportScoreDao;

	public ExportServiceImpl() {
		exportEventDao = new HibernateDao<>(ExportEvent.class);
		exportScoreDao = new HibernateDao<>(ExportScore.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.ExportService#findExportEventsBy(java.lang.
	 * Long, java.util.Date, java.util.Date)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ExportEvent> findExportEventsBy(Long packId, Date dateFrom, Date dateTo) {
		try {
			log.debug("findExportEventsBy");
			exportEventDao.beginTransaction();

			Criteria criteria = exportEventDao.createCriteria();

			if (null == dateFrom) {
				criteria.add(Restrictions.and(Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						// Restrictions.eq(FieldConstants.STATUS,
						// Status.FINISHED),
						// Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.le(FieldConstants.CREATED, dateTo)));
			} else if (null == dateTo) {
				criteria.add(Restrictions.and(Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						// Restrictions.eq(FieldConstants.STATUS,
						// Status.FINISHED),
						// Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.ge(FieldConstants.CREATED, dateFrom)));
			} else {
				criteria.add(Restrictions.and(Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						// Restrictions.eq(FieldConstants.STATUS,
						// Status.FINISHED),
						// Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.between(FieldConstants.CREATED, dateFrom, dateTo)));
			}

			criteria.addOrder(Order.asc(FieldConstants.PROPERTY_TEST_ID));
			criteria.addOrder(Order.asc(FieldConstants.ID));

			List<ExportEvent> events = criteria.list();
			exportEventDao.commit();
			return events;

		} catch (Exception e) {
			log.error(e.getMessage());
			exportEventDao.rollback();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.ExportService#findExportEventsByTestId(java.
	 * util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ExportEvent> findExportEventsByTestId(Collection<Long> testIds) {
		log.debug("findExportEventsByTestId");
		try {
			exportEventDao.beginTransaction();

			Criteria criteria = exportEventDao.createCriteria();

			criteria.add(Restrictions.in(FieldConstants.PROPERTY_TEST_ID, testIds));

			criteria.addOrder(Order.asc(FieldConstants.PROPERTY_TEST_ID));
			criteria.addOrder(Order.asc(FieldConstants.ID));

			List<ExportEvent> events = criteria.list();
			exportEventDao.commit();
			return events;

		} catch (Exception e) {
			log.error(e.getMessage());
			exportEventDao.rollback();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ExportScore> findExportScoresByTestId(Collection<Long> testIds) {
		log.debug("findExportScoresByTestId");
		try {
			exportScoreDao.beginTransaction();

			Criteria criteria = exportScoreDao.createCriteria();

			criteria.add(Restrictions.in(FieldConstants.PROPERTY_TEST_ID, testIds));

			criteria.addOrder(Order.asc(FieldConstants.PROPERTY_TEST_ID));
			criteria.addOrder(Order.asc(FieldConstants.ID));

			List<ExportScore> scores = criteria.list();
			exportScoreDao.commit();
			return scores;

		} catch (Throwable e) {
			log.error(e.getMessage());
			exportScoreDao.rollback();
		}
		return null;
	}

	@Override
	public void releaseConnection() {
		HibernateUtil.closeCurrent();		
	}

}
