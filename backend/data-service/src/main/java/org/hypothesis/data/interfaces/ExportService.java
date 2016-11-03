package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.ExportEvent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ExportService extends Serializable {

	List<ExportEvent> findExportEventsBy(Long packId, Date dateFrom, Date dateTo);

	List<ExportEvent> findExportEventsByTestId(Collection<Long> testIds);

}