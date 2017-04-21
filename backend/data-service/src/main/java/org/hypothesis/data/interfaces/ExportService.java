/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.ExportEvent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ExportService extends Serializable {

	List<ExportEvent> findExportEventsBy(Long packId, Date dateFrom, Date dateTo);

	List<ExportEvent> findExportEventsByTestId(Collection<Long> testIds);
	
	void releaseConnection();

}