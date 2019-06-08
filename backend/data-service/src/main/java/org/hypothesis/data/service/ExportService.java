package org.hypothesis.data.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hypothesis.data.dto.ExportEventDto;
import org.hypothesis.data.dto.ExportScoreDto;

public interface ExportService {

	List<ExportEventDto> findExportEventsBy(long packId, Date dateFrom, Date dateTo);

	List<ExportEventDto> findExportEventsByTestIds(Set<Long> testIds);

	List<ExportScoreDto> findExportScoresByTestIds(Set<Long> testIds);
}
