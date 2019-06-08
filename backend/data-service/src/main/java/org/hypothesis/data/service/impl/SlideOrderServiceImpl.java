package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.SlideOrderDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.model.SlideOrder;
import org.hypothesis.data.service.SlideOrderService;

public class SlideOrderServiceImpl implements SlideOrderService {

	private static final Logger log = Logger.getLogger(SlideOrderServiceImpl.class);

	private final HibernateDao<SlideOrder, Long> slideOrderDao = new HibernateDao<SlideOrder, Long>(SlideOrder.class);

	private final SlideOrderConverter slideOrderConverter = new SlideOrderConverter();

	@Override
	public synchronized SlideOrderDto findSlideOrder(long testId, long taskId) {
		log.debug("findTaskSlideOrder");
		try {
			begin();

			List<SlideOrder> slideOrders = slideOrderDao
					.findByCriteria(Restrictions.and(Restrictions.eq(EntityConstants.TEST_ID, testId),
							Restrictions.eq(EntityConstants.TASK_ID, taskId)));

			final SlideOrderDto dto = (slideOrders.isEmpty() || slideOrders.size() > 1) ? null
					: slideOrderConverter.toDto(slideOrders.get(0));

			commit();
			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized SlideOrderDto saveSlideOrder(long testId, long taskId, List<Integer> order) {
		log.debug("findTaskSlideOrder");
		if (order != null && !order.isEmpty()) {
			SlideOrder entity = new SlideOrder();
			slideOrderConverter.fillEntity(entity, testId, taskId, order);

			try {
				begin();

				slideOrderDao.makePersistent(entity);

				commit();
				return slideOrderConverter.toDto(entity);
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}
		return null;
	}

}
