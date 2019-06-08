package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.PackSetDto;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.PackSet;
import org.hypothesis.data.service.PackSetService;

public class PackSetServiceImpl implements PackSetService {

	private static final Logger log = Logger.getLogger(PackSetService.class);

	private final HibernateDao<PackSet, Long> dao = new HibernateDao<PackSet, Long>(PackSet.class);

	private final HibernateDao<Pack, Long> packDao = new HibernateDao<Pack, Long>(Pack.class);

	private final PackConverter packConverter = new PackConverter();

	@Override
	public synchronized List<PackSetDto> findAll() {
		log.debug("findAll");

		try {
			begin();

			final List<PackSetDto> packSets = dao.findAll().stream()//
					.filter(Objects::nonNull)//
					.map(this::toDto)//
					.collect(toList());

			commit();
			return packSets;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	@Override
	public synchronized PackSetDto save(PackSetDto packSet) {
		log.debug("save");
		Objects.requireNonNull(packSet);

		try {
			begin();

			final PackSet toSave = packSet.getId() != null ? dao.findById(packSet.getId(), true) : new PackSet();
			if (toSave == null) {
				throw new EntityNotFoundException("packSet id=" + packSet.getId());
			}

			fillEntity(packSet, toSave);
			dao.makePersistent(toSave);

			final PackSetDto dto = toDto(toSave);
			commit();

			return dto;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	private PackSetDto toDto(PackSet entity) {
		final PackSetDto dto = new PackSetDto();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPacks(entity.getPacks().stream()//
				.filter(Objects::nonNull)//
				.map(p -> packConverter.doDto(p, false))//
				.collect(toList()));

		return dto;
	}

	private void fillEntity(PackSetDto dto, PackSet entity) {
		entity.setName(dto.getName());

		entity.setPacks(dto.getPacks().stream()//
				.filter(Objects::nonNull)//
				.map(packDto -> (packDto.getId() != null)//
						? packDao.findById(packDto.getId(), false)//
						: packConverter.toNewEntity(packDto, false))//
				.collect(toList()));
	}

}
