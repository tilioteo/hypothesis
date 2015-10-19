/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Date;

import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchOutput;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Status;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.persistence.BranchService;
import com.tilioteo.hypothesis.persistence.OutputService;
import com.tilioteo.hypothesis.persistence.PersistenceService;
import com.tilioteo.hypothesis.persistence.SlideService;
import com.tilioteo.hypothesis.persistence.TaskService;
import com.tilioteo.hypothesis.persistence.TestService;
import com.tilioteo.hypothesis.processing.Command;

/**
 * @author kamil
 *
 */
public class AsynchronousService {
	
	private TestService testService;
	private BranchService branchService;
	private TaskService taskService;
	private SlideService slideService;
	private OutputService outputService;
	
	private AsynchronousCommandExecutor commandExecutor = new AsynchronousCommandExecutor();
	
	public AsynchronousService(TestService testService, OutputService outputService, PersistenceService persistenceService, BranchService branchService, TaskService taskService, SlideService slideService) {
		this.testService = testService;
		this.outputService = outputService;
		this.branchService = branchService;
		this.taskService = taskService;
		this.slideService = slideService;
	}

	public void saveBranchOutput(final BranchOutput branchOutput) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				outputService.saveBranchOutput(branchOutput);
			}
		});
	}

	public void saveTestEvent(final Event event, final Date date, final String slideData, final Status status, final Long testId, final Long branchId, final Long taskId, final Long slideId) {
		commandExecutor.add(new Command() {
			@Override
			public void execute() {
				SimpleTest test = testService.findById(testId);

				if (test != null) {
					Branch branch = branchService.findById(branchId);
					Task task = taskService.findById(taskId);
					Slide slide = slideService.findById(slideId);

					// update event
					event.setBranch(branch);
					event.setTask(task);
					event.setSlide(slide);
				
					if (slideData != null) {
						event.setXmlData(slideData);
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
}
