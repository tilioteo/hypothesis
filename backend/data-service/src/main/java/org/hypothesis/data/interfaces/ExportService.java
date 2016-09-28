package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hypothesis.data.model.ExportEvent;

public interface ExportService extends Serializable {

	List<ExportEvent> findExportEventsBy(Long packId, Date dateFrom, Date dateTo);

	List<ExportEvent> findExportEventsByTestId(Collection<Long> testIds);

}