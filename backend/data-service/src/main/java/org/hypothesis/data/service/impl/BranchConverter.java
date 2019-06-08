package org.hypothesis.data.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.Objects;

import org.hypothesis.data.dto.BranchDto;
import org.hypothesis.data.model.Branch;

class BranchConverter {

	private final TaskConverter taskConverter = new TaskConverter();

	public BranchDto toDto(Branch entity, boolean deep) {
		if (entity == null) {
			return null;
		}

		final BranchDto dto = new BranchDto();

		dto.setId(entity.getId());
		dto.setData(entity.getData());
		dto.setNote(entity.getNote());

		if (deep) {
			dto.setTasks(entity.getTasks().stream()//
					.filter(Objects::nonNull)//
					.map(t -> taskConverter.toDto(t, true))//
					.collect(toList()));
		}

		return dto;
	}

}
