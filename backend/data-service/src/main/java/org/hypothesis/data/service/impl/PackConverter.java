package org.hypothesis.data.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.Objects;

import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.model.Pack;

class PackConverter {

	private final BranchConverter branchConverter = new BranchConverter();

	public PackDto doDto(Pack pack, boolean deep) {
		if (pack == null) {
			return null;
		}

		final PackDto dto = new PackDto();

		dto.setId(pack.getId());
		dto.setName(pack.getName());
		dto.setDescription(pack.getDescription());
		dto.setNote(pack.getNote());
		dto.setPublished(pack.getPublished());

		if (deep) {
			dto.setBranches(pack.getBranches().stream()//
					.filter(Objects::nonNull)//
					.map(b -> branchConverter.toDto(b, true))//
					.collect(toList()));
		}

		return dto;
	}

	public void fillEntity(PackDto dto, Pack entity, boolean deep) {
		if (dto == null || entity == null) {
			return;
		}

		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setNote(dto.getNote());
		entity.setPublished(dto.getPublished());

		if (deep) {

		}
	}

	public Pack toNewEntity(PackDto dto, boolean deep) {
		final Pack pack = new Pack();

		fillEntity(dto, pack, deep);

		return pack;
	}

}
