package org.hypothesis.data.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.Objects;

import org.hypothesis.data.dto.TaskDto;
import org.hypothesis.data.model.Task;

class TaskConverter {

	private final SlideConverter slideConverter = new SlideConverter();

	public TaskDto toDto(Task task, boolean deep) {
		if (task == null) {
			return null;
		}

		final TaskDto dto = new TaskDto();

		dto.setId(task.getId());
		dto.setData(task.getData());
		dto.setName(task.getName());
		dto.setNote(task.getNote());
		dto.setRandomized(task.isRandomized());

		if (deep) {
			dto.setSlides(task.getSlides().stream()//
					.filter(Objects::nonNull)//
					.map(slideConverter::toDto)//
					.collect(toList()));
		}

		return dto;
	}

}
