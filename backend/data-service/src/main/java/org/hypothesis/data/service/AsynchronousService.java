/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.util.Date;

import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Score;
import org.hypothesis.data.model.Status;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class AsynchronousService {

	private final TestService testService;
	private final OutputService outputService;

	private final AsynchronousCommandExecutor commandExecutor = new AsynchronousCommandExecutor();

	public AsynchronousService(TestService testService, OutputService outputService) {
		this.testService = testService;
		this.outputService = outputService;

		commandExecutor.setFinishCommand(HibernateUtil::closeCurrent);
	}

	public void saveBranchOutput(final BranchOutput branchOutput) {
		commandExecutor.add(() -> outputService.saveBranchOutput(branchOutput));
	}

	public void saveTestEvent(final Event event, final Date date, final String slideData, final Status status,
			final Long testId, final Long branchId, final Long taskId, final Long slideId) {
		commandExecutor.add(() -> testService.saveTestEvent(testId, status, event.getTimeStamp(), event.getClientTimeStamp(),
				event.getType(), event.getName(), slideData, branchId, taskId, slideId));
	}

	public void cleanup() {
		commandExecutor.stop();
	}

	public void saveTestScore(final Score score, final String scoreData, final Long testId, final Long branchId,
			final Long taskId, final Long slideId) {
		commandExecutor.add(() -> testService.saveTestScore(testId, score.getTimeStamp(), score.getName(), scoreData, branchId, taskId, slideId));
	}
}
