/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.util.Date;

import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Score;
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
public class AsynchronousService {

	private final TestService testService;
	private final BranchService branchService;
	private final TaskService taskService;
	private final SlideService slideService;
	private final OutputService outputService;

	private final AsynchronousCommandExecutor commandExecutor = new AsynchronousCommandExecutor();

	public AsynchronousService(TestService testService, OutputService outputService,
			PersistenceService persistenceService, BranchService branchService, TaskService taskService,
			SlideService slideService) {
		this.testService = testService;
		this.outputService = outputService;
		this.branchService = branchService;
		this.taskService = taskService;
		this.slideService = slideService;

		commandExecutor.setFinishCommand(new Command() {
			@Override
			public void execute() {
				HibernateUtil.closeCurrent();
			}
		});
	}

	public void saveBranchOutput(final BranchOutput branchOutput) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				outputService.saveBranchOutput(branchOutput);
			}
		});
	}

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

	public void cleanup() {
		commandExecutor.stop();
	}

	public void saveTestScore(final Score score, final String scoreData, final Long testId, final Long branchId,
			final Long taskId, final Long slideId) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				SimpleTest test = testService.findById(testId);
				if (test != null) {
					Branch branch = branchId != null ? branchService.findById(branchId) : null;
					Task task = taskId != null ? taskService.findById(taskId) : null;
					Slide slide = slideId != null ? slideService.findById(slideId) : null;

					// update score
					score.setBranch(branch);
					score.setTask(task);
					score.setSlide(slide);
					score.setData(scoreData);

					// persist score
					testService.saveScore(score, test);
				}
			}
		});
	}
}
