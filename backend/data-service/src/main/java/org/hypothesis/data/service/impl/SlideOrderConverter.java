package org.hypothesis.data.service.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hypothesis.data.dto.SlideOrderDto;
import org.hypothesis.data.model.SlideOrder;

class SlideOrderConverter {

	public SlideOrderDto toDto(SlideOrder entity) {
		if (entity == null) {
			return null;
		}

		final SlideOrderDto dto = new SlideOrderDto();

		dto.setId(entity.getId());
		dto.setTestId(entity.getTestId());
		dto.setTaskId(entity.getTaskId());
		dto.setOrder(toList(entity.getData()));

		return dto;
	}

	public void fillEntity(SlideOrder entity, long testId, long taskId, List<Integer> order) {
		entity.setTestId(testId);
		entity.setTaskId(taskId);
		entity.setData(toString(order));
	}

	private List<Integer> toList(String data) {
		if (data != null) {
			return unmodifiableList(Stream.of(data.split(","))//
					.map(String::trim)//
					.map(Integer::getInteger)//
					.filter(Objects::nonNull)//
					.collect(Collectors.toList()));
		} else {
			return emptyList();
		}
	}

	private String toString(List<Integer> order) {
		if (order != null && !order.isEmpty()) {
			return order.stream()//
					.filter(Objects::nonNull)//
					.map(i -> i.toString())//
					.collect(Collectors.joining(","));
		} else {
			return null;
		}
	}

}
