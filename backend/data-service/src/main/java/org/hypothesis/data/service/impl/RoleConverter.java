package org.hypothesis.data.service.impl;

import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.model.Role;

class RoleConverter {

	public static RoleDto toDto(Role role) {
		if (role == null) {
			return null;
		}

		final RoleDto dto = new RoleDto();

		dto.setId(role.getId());
		dto.setName(role.getName());

		return dto;
	}

}
