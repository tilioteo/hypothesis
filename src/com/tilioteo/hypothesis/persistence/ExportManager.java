/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.dao.ExportEventDao;
import com.tilioteo.hypothesis.entity.ExportEvent;
import com.tilioteo.hypothesis.entity.FieldConstants;

/**
 * @author kamil
 *
 */
public class ExportManager {

	private ExportEventDao exportEventDao;

	public static ExportManager newInstance() {
		return new ExportManager(new ExportEventDao());
	}
	
	public ExportManager(ExportEventDao exportEventDao) {
		this.exportEventDao = exportEventDao;
	}
	
	@SuppressWarnings("unchecked")
	public List<ExportEvent> findExportEventsBy(Long packId, Date dateFrom, Date dateTo) {
		try {
			exportEventDao.beginTransaction();
			
			Criteria criteria = exportEventDao.createCriteria();
			
			if (null == dateFrom) {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.le(FieldConstants.CREATED, dateTo)));
			} else if (null == dateTo) {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.ge(FieldConstants.CREATED, dateFrom)));
			} else {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PROPERTY_PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.between(FieldConstants.CREATED, dateFrom, dateTo)));
			}
			
			criteria.addOrder(Order.asc(FieldConstants.PROPERTY_TEST_ID));
			criteria.addOrder(Order.asc(FieldConstants.ID));
			
			List<ExportEvent> events = criteria.list();
			exportEventDao.commit();
			return events;
			
		} catch (Throwable e) {
			exportEventDao.rollback();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ExportEvent> findExportEventsByTestId(Collection<Long> testIds) {
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
			exportEventDao.rollback();
		}
		return null;
	}

}
