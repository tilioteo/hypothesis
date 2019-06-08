package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;
import static org.hypothesis.data.service.impl.Utility.longToDate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hypothesis.data.api.Gender;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.ExportEventDto;
import org.hypothesis.data.dto.ExportScoreDto;
import org.hypothesis.data.interfaces.EntityConstants;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.model.ExportEvent;
import org.hypothesis.data.model.ExportScore;
import org.hypothesis.data.service.ExportService;

public class ExportServiceImpl implements ExportService {

	private static final Logger log = Logger.getLogger(ExportServiceImpl.class);

	private final HibernateDao<ExportEvent, Long> exportEventDao = new HibernateDao<ExportEvent, Long>(
			ExportEvent.class);

	private final HibernateDao<ExportScore, Long> exportScoreDao = new HibernateDao<ExportScore, Long>(
			ExportScore.class);

	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<ExportEventDto> findExportEventsBy(long packId, Date dateFrom, Date dateTo) {
		log.debug("findExportEventsBy");

		try {
			begin();

			Criteria criteria = exportEventDao.createCriteria().add(Restrictions.eq(EntityConstants.PACK_ID, packId))
			// .add(Restrictions.eq(FieldConstants.STATUS, Status.FINISHED))
			// .add(Restrictions.isNotNull(FieldConstants.FINISHED))
			;

			if (dateFrom != null) {
				criteria.add(Restrictions.ge(FieldConstants.CREATED, dateFrom));
			}
			if (dateTo != null) {
				criteria.add(Restrictions.le(FieldConstants.CREATED, dateTo));
			}

			criteria.addOrder(Order.asc(EntityConstants.TEST_ID));
			criteria.addOrder(Order.asc(FieldConstants.ID));

			List<ExportEvent> events = criteria.list();
			final List<ExportEventDto> dtos = events.stream()//
					.filter(Objects::nonNull)//
					.map(this::toExportEventDto)//
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
	public synchronized List<ExportEventDto> findExportEventsByTestIds(Set<Long> testIds) {
		log.debug("findExportEventsByTestIds");

		if (testIds != null && !testIds.isEmpty()) {
			try {
				begin();

				Criteria criteria = exportEventDao.createCriteria();

				criteria.add(Restrictions.in(EntityConstants.TEST_ID, testIds));

				criteria.addOrder(Order.asc(EntityConstants.TEST_ID));
				criteria.addOrder(Order.asc(FieldConstants.ID));

				List<ExportEvent> events = criteria.list();
				final List<ExportEventDto> dtos = events.stream()//
						.filter(Objects::nonNull)//
						.map(this::toExportEventDto)//
						.collect(toList());

				commit();
				return dtos;
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}
		return emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<ExportScoreDto> findExportScoresByTestIds(Set<Long> testIds) {
		log.debug("findExportScoresByTestIds");

		if (testIds != null && !testIds.isEmpty()) {
			try {
				begin();

				Criteria criteria = exportScoreDao.createCriteria();

				criteria.add(Restrictions.in(EntityConstants.TEST_ID, testIds));

				criteria.addOrder(Order.asc(EntityConstants.TEST_ID));
				criteria.addOrder(Order.asc(FieldConstants.ID));

				List<ExportScore> scores = criteria.list();
				final List<ExportScoreDto> dtos = scores.stream()//
						.filter(Objects::nonNull)//
						.map(this::toExportScoreDto)//
						.collect(toList());

				commit();
				return dtos;
			} catch (Exception e) {
				log.error(e.getMessage());
				rollback();
			}
		}
		return emptyList();
	}

	private ExportEventDto toExportEventDto(ExportEvent entity) {
		if (entity == null) {
			return null;
		}

		final ExportEventDto dto = new ExportEventDto();

		dto.setBranchId(entity.getBranchId());
		dto.setBranchName(entity.getBranchName());
		dto.setClientTimeStamp(longToDate(entity.getClientTimeStamp()));
		dto.setCreated(entity.getCreated());
		dto.setData(entity.getData());
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPackId(entity.getPackId());
		dto.setPackName(entity.getPackName());
		dto.setSlideId(entity.getSlideId());
		dto.setSlideName(entity.getSlideName());
		dto.setTaskId(entity.getTaskId());
		dto.setTaskName(entity.getTaskName());
		dto.setTestId(entity.getTestId());
		dto.setTimeStamp(longToDate(entity.getTimeStamp()));
		dto.setType(entity.getType());
		dto.setUserId(entity.getUserId());

		return dto;
	}

	private ExportScoreDto toExportScoreDto(ExportScore entity) {
		if (entity == null) {
			return null;
		}

		final ExportScoreDto dto = new ExportScoreDto();

		dto.setBirthDate(entity.getBirthDate());
		dto.setBranchId(entity.getBranchId());
		dto.setBranchName(entity.getBranchName());
		dto.setCreated(entity.getCreated());
		dto.setData(entity.getData());
		dto.setEducation(entity.getEducation());
		dto.setFirstName(entity.getFirstName());
		dto.setGender(Gender.get(entity.getGender()));
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setNote(entity.getNote());
		dto.setPackId(entity.getPackId());
		dto.setPackName(entity.getPackName());
		dto.setPassword(entity.getPassword());
		dto.setSlideId(entity.getSlideId());
		dto.setSlideName(entity.getSlideName());
		dto.setTaskId(entity.getTaskId());
		dto.setTaskName(entity.getTaskName());
		dto.setTestId(entity.getTestId());
		dto.setTimeStamp(longToDate(entity.getTimeStamp()));
		dto.setUserId(entity.getUserId());
		dto.setUsername(entity.getUsername());

		return dto;
	}

}
