package org.hypothesis.data.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.Objects;

import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.GroupDto;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

class GroupConverter {

	private final HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(User.class);

	private final UserConverter userConverter;

	public GroupConverter() {
		userConverter = new UserConverter();
	}

	public GroupConverter(UserConverter userConverter) {
		this.userConverter = userConverter;
	}

	public GroupDto toDto(Group group) {
		return toDto(group, true);
	}

	public GroupDto toDto(Group group, boolean deep) {
		if (group == null) {
			return null;
		}

		final GroupDto dto = new GroupDto();

		dto.setId(group.getId());
		dto.setName(group.getName());
		dto.setNote(group.getNote());
		dto.setOwnerId(group.getOwnerId());

		if (deep) {
			dto.setUsers(group.getUsers().stream()//
					.filter(Objects::nonNull)//
					.map(userConverter::toDto)//
					.collect(toSet()));
		}

		return dto;
	}

	public void fillEntity(GroupDto dto, Group entity) {
		if (dto == null || entity == null) {
			return;
		}

		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setNote(dto.getNote());
		entity.setOwnerId(dto.getOwnerId());

		entity.setUsers(dto.getUsers().stream()//
				.filter(Objects::nonNull)//
				.map(u -> (u.getId() != null)//
						? userDao.findById(u.getId(), false)//
						: userConverter.toNewEntity(u, true))//
				.collect(toSet()));
	}

	public Group toNewEntity(GroupDto dto) {
		final Group group = new Group();

		fillEntity(dto, group);

		return group;
	}

}
