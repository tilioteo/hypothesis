package org.hypothesis.data.service;

import java.util.Date;
import java.util.List;

import org.hypothesis.data.api.Status;
import org.hypothesis.data.dto.EventDto;
import org.hypothesis.data.dto.ScoreDto;
import org.hypothesis.data.dto.TestDto;

public interface TestService {

	TestDto getUnattendedTest(Long userId, long packId, boolean production);

	List<TestDto> findManagedTestsOverview(long userId, long packId, Date dateFrom, Date dateTo);

	List<TestDto> findManagedScoresOverview(long userId, Date dateFrom, Date dateTo);

	void saveEvent(EventDto event, Status status);

	void saveScore(ScoreDto score);

}
