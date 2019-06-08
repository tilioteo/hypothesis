package org.hypothesis.data.service.impl;

import org.hypothesis.data.api.Status;
import org.hypothesis.data.dto.TestDto;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Test;

class TestConverter {

	private final PackServiceImpl packService = new PackServiceImpl();
	private final UserServiceImpl userService = new UserServiceImpl();

	public TestDto toDto(Test test, boolean deep) {
		if (test == null) {
			return null;
		}

		final TestDto dto = new TestDto();

		dto.setId(test.getId());
		dto.setProduction(test.isProduction());
		dto.setPack(packService.getByIdInternal(test.getPackId(), true));
		dto.setUser(test.getUserId() != null ? userService.getDtoByIdInternal(test.getUserId()) : null);
		dto.setStatus(test.getStatus() != null ? Status.get(test.getStatus()) : null);
		dto.setCreated(test.getCreated());
		dto.setStarted(test.getStarted());
		dto.setBroken(test.getBroken());
		dto.setFinished(test.getFinished());
		dto.setLastAccess(test.getLastAccess());
		dto.setLastBranchId(test.getLastBranchId());
		dto.setLastTaskId(test.getLastTaskId());
		dto.setLastSlideId(test.getLastSlideId());

		if (deep) {

		}

		return dto;
	}

	public TestDto toDto(SimpleTest test) {
		if (test == null) {
			return null;
		}

		final TestDto dto = new TestDto();

		dto.setId(test.getId());
		dto.setProduction(test.isProduction());
		dto.setPack(packService.getByIdInternal(test.getPackId(), true));
		dto.setUser(test.getUserId() != null ? userService.getDtoByIdInternal(test.getUserId()) : null);
		dto.setStatus(test.getStatus() != null ? Status.get(test.getStatus()) : null);
		dto.setCreated(test.getCreated());
		dto.setStarted(test.getStarted());
		dto.setBroken(test.getBroken());
		dto.setFinished(test.getFinished());
		dto.setLastAccess(test.getLastAccess());
		dto.setLastBranchId(test.getLastBranchId());
		dto.setLastTaskId(test.getLastTaskId());
		dto.setLastSlideId(test.getLastSlideId());

		return dto;
	}

}
