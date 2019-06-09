package org.hypothesis.data.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.Objects;

import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

class UserConverter {

	private final GroupConverter groupConverter = new GroupConverter(this);

	private final HibernateDao<Group, Long> groupDao = new HibernateDao<Group, Long>(Group.class);

	public SimpleUserDto toSimpleDto(User user) {
		final SimpleUserDto dto = new SimpleUserDto();

		fillSimpleUserDto(dto, user);
		return dto;
	}

	public UserDto toDto(User user) {
		if (user == null) {
			return null;
		}

		final UserDto dto = new UserDto();

		fillSimpleUserDto(dto, user);

		dto.setBirthDate(user.getBirthDate());
		dto.setEducation(user.getEducation());
		dto.setGender(user.getGender());
		dto.setName(user.getName());
		dto.setNote(user.getNote());
		dto.setOwnerId(user.getOwnerId());
		dto.setPassword(user.getPassword());
		dto.setTestingDate(user.getTestingDate());

		return dto;
	}

	private void fillSimpleUserDto(SimpleUserDto dto, User user) {
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEnabled(user.getEnabled());
		dto.setAutoDisable(user.getAutoDisable());
		dto.setTestingSuspended(user.isTestingSuspended());
		dto.setExpireDate(user.getExpireDate());

		dto.setRoles(user.getRoles().stream()//
				.filter(Objects::nonNull)//
				.map(RoleConverter::toDto)//
				.collect(toSet()));
		dto.setGroups(user.getGroups().stream()//
				.filter(Objects::nonNull)//
				.map(g -> groupConverter.toDto(g, false))//
				.collect(toSet()));
	}

	public void fillEntity(UserDto dto, User entity, boolean deep) {
		if (dto == null || entity == null) {
			return;
		}

		entity.setId(dto.getId());
		entity.setUsername(dto.getUsername());
		entity.setEnabled(dto.getEnabled());
		entity.setAutoDisable(dto.isAutoDisable());
		entity.setTestingSuspended(dto.isTestingSuspended());
		entity.setExpireDate(dto.getExpireDate());
		entity.setBirthDate(dto.getBirthDate());
		entity.setEducation(dto.getEducation());
		entity.setGender(dto.getGender());
		entity.setName(dto.getName());
		entity.setNote(dto.getNote());
		entity.setOwnerId(dto.getOwnerId());
		entity.setPassword(dto.getPassword());
		entity.setTestingDate(dto.getTestingDate());

		entity.setRoles(dto.getRoles().stream()//
				.filter(Objects::nonNull)//
				.map(RoleConverter::toEntity)//
				.collect(toSet()));

		if (deep) {
			entity.setGroups(dto.getGroups().stream()//
					.filter(Objects::nonNull)//
					.map(g -> (g.getId() != null)//
							? groupDao.findById(g.getId(), false)//
							: groupConverter.toNewEntity(g))//
					.collect(toSet()));
		}
	}

	public User toNewEntity(UserDto dto, boolean deep) {
		final User user = new User();

		fillEntity(dto, user, deep);

		return user;
	}

}
