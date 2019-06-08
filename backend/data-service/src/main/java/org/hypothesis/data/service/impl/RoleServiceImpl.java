package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hypothesis.data.service.impl.TransactionManager.begin;
import static org.hypothesis.data.service.impl.TransactionManager.commit;
import static org.hypothesis.data.service.impl.TransactionManager.rollback;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.service.RoleService;

public class RoleServiceImpl implements RoleService {

	private static final Logger log = Logger.getLogger(RoleServiceImpl.class);

	private final HibernateDao<Role, Long> dao = new HibernateDao<Role, Long>(Role.class);

	@Override
	public List<RoleDto> findAll() {
		log.debug("findAll");

		try {
			begin();

			final List<RoleDto> roles = dao.findAll().stream()//
					.filter(Objects::nonNull)//
					.map(RoleConverter::toDto)//
					.collect(toList());

			commit();
			return roles;
		} catch (Exception e) {
			log.error(e.getMessage());
			rollback();
		}
		return emptyList();
	}

}
