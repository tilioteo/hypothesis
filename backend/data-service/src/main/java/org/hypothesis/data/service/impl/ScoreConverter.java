package org.hypothesis.data.service.impl;

import static org.hypothesis.data.service.impl.Utility.dateToLong;
import static org.hypothesis.data.service.impl.Utility.longToDate;

import org.hypothesis.data.dto.ScoreDto;
import org.hypothesis.data.model.Score;

public class ScoreConverter {

	public ScoreDto toDto(Score score) {
		if (score == null) {
			return null;
		}

		final ScoreDto dto = new ScoreDto();

		dto.setId(score.getId());
		dto.setTimeStamp(longToDate(score.getTimeStamp()));
		dto.setName(score.getName());
		dto.setData(score.getData());
		dto.setBranchId(score.getBranchId());
		dto.setTaskId(score.getTaskId());
		dto.setSlideId(score.getSlideId());
		dto.setTestId(score.getTestId());

		return dto;
	}

	public void fillEntity(ScoreDto dto, Score score) {
		if (dto == null || score == null) {
			return;
		}

		score.setId(dto.getId());
		score.setTimeStamp(dateToLong(dto.getTimeStamp()));
		score.setName(dto.getName());
		score.setData(dto.getData());
		score.setBranchId(dto.getBranchId());
		score.setTaskId(dto.getTaskId());
		score.setSlideId(dto.getSlideId());
		score.setTestId(dto.getTestId());
	}

}
