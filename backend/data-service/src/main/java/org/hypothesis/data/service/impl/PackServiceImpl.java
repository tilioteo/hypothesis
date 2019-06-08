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
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.service.PackService;

public class PackServiceImpl implements PackService {

	private static final Logger log = Logger.getLogger(PackServiceImpl.class);

	private final HibernateDao<Pack, Long> dao = new HibernateDao<>(Pack.class);
	private final PackConverter converter = new PackConverter();

	@Override
	public synchronized PackDto getById(long packId) {
		return getById(packId, false);
	}

	@Override
	public synchronized PackDto getById(long packId, boolean deep) {
		log.debug(String.format("getById: id = %s", packId));
		try {
			begin();

			final PackDto dto = getByIdInternal(packId, deep);

			commit();
			return dto;
		} catch (Throwable e) {
			log.error(e.getMessage());
			rollback();
		}
		return null;
	}

	@Override
	public synchronized List<PackDto> findAll() {
		log.debug("findAll");
		try {
			begin();

			final List<PackDto> packs = dao.findAll().stream()//
					.filter(Objects::nonNull)//
					.map(p -> converter.doDto(p, false))//
					.collect(toList());

			commit();
			return packs;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

	PackDto getByIdInternal(long packId, boolean deep) {
		Pack pack = dao.findById(packId, false);

		if (pack == null) {
			throw new EntityNotFoundException("pack id=" + packId);
		}

		return converter.doDto(pack, deep);
	}

}
