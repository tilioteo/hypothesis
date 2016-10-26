package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.Date;

import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Status;

public interface AsynchronousService extends Serializable {

	void saveBranchOutput(BranchOutput branchOutput);

	void saveTestEvent(Event event, Date date, String slideData, Status status, Long testId, Long branchId, Long taskId,
			Long slideId);

	void cleanup();

}