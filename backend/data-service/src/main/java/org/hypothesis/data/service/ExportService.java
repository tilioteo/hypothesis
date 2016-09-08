/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.data.model.FieldConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ExportService implements Serializable {

	private static final Logger log = Logger.getLogger(ExportService.class);

	private final HibernateDao<ExportEvent, Long> exportEventDao;

	public static ExportService newInstance() {
		return new ExportService(new HibernateDao<ExportEvent, Long>(ExportEvent.class));
	}

	public ExportService(HibernateDao<ExportEvent, Long> exportEventDao) {
		this.exportEventDao = exportEventDao;
	}

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

		} catch (Throwable e) {
			log.error(e.getMessage());
			exportEventDao.rollback();
		}
		return null;
	}

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

		} catch (Throwable e) {
			log.error(e.getMessage());
			exportEventDao.rollback();
		}
		return null;
	}

}
