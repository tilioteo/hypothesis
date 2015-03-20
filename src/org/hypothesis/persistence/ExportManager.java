/**
 * 
 */
package org.hypothesis.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.entity.ExportEvent;
import org.hypothesis.persistence.hibernate.ExportEventDao;

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
						Restrictions.eq(FieldConstants.PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.le(FieldConstants.CREATED, dateTo)));
			} else if (null == dateTo) {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.ge(FieldConstants.CREATED, dateFrom)));
			} else {
				criteria.add(Restrictions.and(
						Restrictions.eq(FieldConstants.PACK_ID, packId),
						//Restrictions.eq(FieldConstants.STATUS, Status.FINISHED),
						//Restrictions.isNotNull(FieldConstants.FINISHED),
						Restrictions.between(FieldConstants.CREATED, dateFrom, dateTo)));
			}
			
			criteria.addOrder(Order.asc(FieldConstants.TEST_ID));
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
