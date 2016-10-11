/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.util.Date;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.hypothesis.data.interfaces.AsynchronousService;
import org.hypothesis.data.interfaces.BranchService;
import org.hypothesis.data.interfaces.OutputService;
import org.hypothesis.data.interfaces.SlideService;
import org.hypothesis.data.interfaces.TaskService;
import org.hypothesis.data.interfaces.TestService;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Task;
import org.hypothesis.interfaces.Command;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class AsynchronousServiceImpl implements AsynchronousService {

	@Inject
	private TestService testService;
	@Inject
	private BranchService branchService;
	@Inject
	private TaskService taskService;
	@Inject
	private SlideService slideService;
	@Inject
	private OutputService outputService;

	private final AsynchronousCommandExecutor commandExecutor = new AsynchronousCommandExecutor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.data.service.AsynchronousService#saveBranchOutput(org.
	 * hypothesis.data.model.BranchOutput)
	 */
	@Override
	public void saveBranchOutput(final BranchOutput branchOutput) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				outputService.saveBranchOutput(branchOutput);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.AsynchronousService#saveTestEvent(org.
	 * hypothesis.data.model.Event, java.util.Date, java.lang.String,
	 * org.hypothesis.data.model.Status, java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public void saveTestEvent(final Event event, final Date date, final String slideData, final Status status,
			final Long testId, final Long branchId, final Long taskId, final Long slideId) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				SimpleTest test = testService.findById(testId);

				if (test != null) {
					Branch branch = branchId != null ? branchService.findById(branchId) : null;
					Task task = taskId != null ? taskService.findById(taskId) : null;
					Slide slide = slideId != null ? slideService.findById(slideId) : null;

					// update event
					event.setBranch(branch);
					event.setTask(task);
					event.setSlide(slide);

					if (slideData != null) {
						event.setData(slideData);
					}

					// update test
					if (status != null && !test.getStatus().equals(status)) {
						test.setStatus(status);

						switch (status) {
						case BROKEN_BY_CLIENT:
						case BROKEN_BY_ERROR:
							test.setBroken(date);
							break;
						case STARTED:
							test.setStarted(date);
							break;
						case FINISHED:
							test.setFinished(date);
							break;
						default:
							break;
						}
					}

					test.setLastAccess(date);
					test.setLastBranch(branch);
					test.setLastTask(task);
					test.setLastSlide(slide);

					// persist event and test
					testService.saveEvent(event, test);
					testService.updateTest(test);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.AsynchronousService#cleanup()
	 */
	@Override
	public void cleanup() {
		commandExecutor.stop();
	}
}
