package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.Utility.dateToLong;
import static org.hypothesis.data.service.impl.Utility.longToDate;

import org.hypothesis.data.dto.EventDto;
import org.hypothesis.data.model.Event;

class EventConverter {

	public EventDto toDto(Event event) {
		if (event == null) {
			return null;
		}

		final EventDto dto = new EventDto();

		dto.setId(event.getId());
		dto.setTimeStamp(longToDate(event.getTimeStamp()));
		dto.setClientTimeStamp(longToDate(event.getClientTimeStamp()));
		dto.setType(event.getType());
		dto.setName(event.getName());
		dto.setData(event.getData());
		dto.setBranchId(event.getBranchId());
		dto.setTaskId(event.getTaskId());
		dto.setSlideId(event.getSlideId());
		dto.setTestId(event.getTestId());

		return dto;
	}

	public void fillEntity(EventDto dto, Event event) {
		if (dto == null || event == null) {
			return;
		}

		event.setId(dto.getId());
		event.setTimeStamp(dateToLong(dto.getTimeStamp()));
		event.setClientTimeStamp(dateToLong(dto.getClientTimeStamp()));
		event.setType(dto.getType());
		event.setName(dto.getName());
		event.setData(dto.getData());
		event.setBranchId(dto.getBranchId());
		event.setTaskId(dto.getTaskId());
		event.setSlideId(dto.getSlideId());
		event.setTestId(dto.getTestId());
	}

}
