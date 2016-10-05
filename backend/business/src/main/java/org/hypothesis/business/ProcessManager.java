/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.AsynchronousService;
import org.hypothesis.data.interfaces.BranchService;
import org.hypothesis.data.interfaces.HasStatus;
import org.hypothesis.data.interfaces.OutputService;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.interfaces.PersistenceService;
import org.hypothesis.data.interfaces.TestService;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchMap;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.SlideOrder;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Task;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.event.model.AbstractProcessEvent;
import org.hypothesis.event.model.AbstractRunningEvent;
import org.hypothesis.event.model.AbstractUserEvent;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.AfterFinishSlideEvent;
import org.hypothesis.event.model.AfterPrepareTestEvent;
import org.hypothesis.event.model.AfterRenderContentEvent;
import org.hypothesis.event.model.BreakTestEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.event.model.ContinueTestEvent;
import org.hypothesis.event.model.ErrorNotificationEvent;
import org.hypothesis.event.model.ErrorTestEvent;
import org.hypothesis.event.model.FinishBranchEvent;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.event.model.FinishSlideEvent.Direction;
import org.hypothesis.event.model.FinishTaskEvent;
import org.hypothesis.event.model.FinishTestEvent;
import org.hypothesis.event.model.NextBranchEvent;
import org.hypothesis.event.model.NextSlideEvent;
import org.hypothesis.event.model.NextTaskEvent;
import org.hypothesis.event.model.PrepareTestEvent;
import org.hypothesis.event.model.PriorSlideEvent;
import org.hypothesis.event.model.ProcessEventType;
import org.hypothesis.event.model.ProcessEventTypes;
import org.hypothesis.event.model.RenderContentEvent;
import org.hypothesis.event.model.StartTestEvent;
import org.hypothesis.server.Messages;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ProcessManager implements Serializable {

	private static Logger log = Logger.getLogger(ProcessManager.class);

	private BranchManager branchManager;
	private TaskManager taskManager;

	private SlideManager slideManager;

	@Inject
	private PersistenceService persistenceService;
	@Inject
	private TestService testService;
	@Inject
	private BranchService branchService;

	@Inject
	private PermissionService permissionService;
	@Inject
	private OutputService outputService;

	@Inject
	private AsynchronousService asynchronousService;

	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	private SimpleTest currentTest = null;
	private Pack currentPack = null;
	private Branch currentBranch = null;
	private Task currentTask = null;
	private Slide currentSlide = null;

	@Inject
	private javax.enterprise.event.Event<ProcessEvent> procEvent;

	public ProcessManager() {
		branchManager = new BranchManager();
		taskManager = new TaskManager();
		slideManager = new SlideManager();
	}

	private Event createEvent(AbstractProcessEvent event) {
		if (event != null && event.getName() != null) {
			ProcessEventType processEvent = ProcessEventTypes.get(event.getName());

			if (processEvent != null) {
				return new Event(processEvent.getId(), event.getName(), event.getTimestamp());
			}
		}

		return null;
	}

	private boolean checkUserPack(User user, Pack pack) {
		Collection<Pack> packs;
		if (user != null) {
			packs = permissionService.findUserPacks(user, true);
			for (Pack allowedPack : packs) {
				if (allowedPack.getId().equals(pack.getId()))
					return true;
			}
		}

		packs = permissionService.getPublishedPacks();
		for (Pack allowedPack : packs) {
			if (allowedPack.getId().equals(pack.getId()))
				return true;
		}

		return false;
	}

	/**
	 * Handler method for {@link ActionEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processActionEvent(@Observes ActionEvent event) {
		saveUserProcessEvent(event);
	}

	/**
	 * Handler method for {@link AfterRenderContentEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processAfterRender(@Observes AfterRenderContentEvent event) {
		saveRunningEvent(event);
	}

	/**
	 * Handler method for {@link BreakTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processBreakTest(@Observes BreakTestEvent event) {
		saveRunningEvent(event);

		testProcessing = false;
		slideProcessing = false;
		currentTest = null;
		currentPack = null;
		currentBranch = null;
		currentTask = null;
		currentSlide = null;
	}

	/**
	 * Handler method for {@link ComponentEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processComponentEvent(@Observes ComponentEvent event) {
		saveUserProcessEvent(event);
	}

	/**
	 * Handler method for {@link ContinueTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processContinueTest(@Observes ContinueTestEvent event) {
		currentTest = event.getTest();
		saveRunningEvent(event);

		persistenceService.merge(branchManager.find(currentTest.getLastBranch()));
		currentBranch = branchManager.current();

		if (currentBranch != null) {

			taskManager.setListFromParent(currentBranch);
			persistenceService.merge(taskManager.find(currentTest.getLastTask()));
			currentTask = taskManager.current();

			if (currentTask != null) {

				setSlideManagerParent(currentTask);
				slideManager.find(currentTest.getLastSlide());
				currentSlide = slideManager.current();

				if (currentSlide != null) {
					renderSlide();
				}
			}
		}
	}

	/**
	 * Handler method for {@link ErrorTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processError(@Observes ErrorTestEvent event) {
		saveRunningEvent(event);

		// TODO add some error description
		procEvent.fire(new ErrorNotificationEvent(Messages.getString("Message.Error.Unspecified")));
	}

	/**
	 * Handler method for {@link FinishBranchEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processFinishBranch(@Observes FinishBranchEvent event) {
		saveRunningEvent(event);

		// TODO process branch result

		saveBranchOutput();

		procEvent.fire(new NextBranchEvent());
	}

	/**
	 * Handler method for {@link FinishSlideEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processFinishSlide(@Observes FinishSlideEvent event) {
		slideManager.finishSlide();
		saveRunningEvent(event);

		slideProcessing = false;

		if (Direction.NEXT.equals(event.getDirection())) {
			branchManager.addSlideOutputs(currentSlide, slideManager.getOutputs());
		}

		if (autoSlideShow) {
			processSlideFollowing(event.getDirection());
		} else {
			procEvent.fire(new AfterFinishSlideEvent(event.getDirection()));
		}
	}

	/**
	 * Handler method for {@link FinishTaskEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processFinishTask(@Observes FinishTaskEvent event) {
		saveRunningEvent(event);

		// taskManager.find(event.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		procEvent.fire(new NextTaskEvent());
	}

	/**
	 * Handler method for {@link FinishTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processFinishTest(@Observes FinishTestEvent event) {
		saveRunningEvent(event);

		currentTest = null;
		testProcessing = false;
	}

	/**
	 * Handler method for {@link NextBranchEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processNextBranch(@Observes NextBranchEvent event) {
		saveRunningEvent(event);

		BranchMap branchMap = branchService.getBranchMap(currentPack, currentBranch);
		Branch nextBranch = branchManager.getNextBranch(branchMap);

		if (nextBranch != null) {
			currentBranch = persistenceService.merge(nextBranch);

			if (currentBranch != null) {

				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceService.merge(taskManager.current());

				if (currentTask != null) {

					setSlideManagerParent(currentTask);
					currentSlide = slideManager.current();

					if (currentSlide != null) {
						slideProcessing = true;
						renderSlide();
					} else {
						procEvent.fire(new FinishTaskEvent());
					}
				} else {
					procEvent.fire(new FinishBranchEvent());
				}
			} else {
				procEvent.fire(new FinishTestEvent());
			}
		} else {
			procEvent.fire(new FinishTestEvent());
		}
	}

	/**
	 * Handler method for {@link NextSlideEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processNextSlide(@Observes NextSlideEvent event) {
		saveRunningEvent(event);

		taskManager.addSlideOutputs(currentSlide, slideManager.getOutputs());
		int nextIndex = taskManager.getNextSlideIndex(currentSlide);
		if (nextIndex == 0) { //
			currentSlide = slideManager.next();
		} else if (nextIndex < 0 || nextIndex > slideManager.getCount()) {
			currentSlide = null;
		} else {
			currentSlide = slideManager.get(nextIndex - 1);
		}

		if (currentSlide != null) {
			slideProcessing = true;
			renderSlide();
		} else {
			procEvent.fire(new FinishTaskEvent());
		}
	}

	/**
	 * Handler method for {@link PriorSlideEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processPriorSlide(@Observes PriorSlideEvent event) {
		saveRunningEvent(event);

		Slide slide = slideManager.prior();

		if (slide != null) {
			currentSlide = slide;
		}

		if (currentSlide != null) {
			slideProcessing = true;
			renderSlide();
		} else {
			// TODO what will happen when there is not prior slide?
		}
	}

	/**
	 * Handler method for {@link NextTaskEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processNextTask(@Observes NextTaskEvent event) {
		saveRunningEvent(event);

		currentTask = persistenceService.merge(taskManager.next());
		if (currentTask != null) {

			setSlideManagerParent(currentTask);
			currentSlide = slideManager.current();

			if (currentSlide != null) {
				slideProcessing = true;
				renderSlide();
			} else {
				procEvent.fire(new FinishTaskEvent());
			}
		} else {
			procEvent.fire(new FinishBranchEvent());
		}
	}

	private void setSlideManagerParent(Task task) {
		slideManager.setListFromParent(task);

		if (task != null && task.isRandomized()) {
			setTaskSlidesRandomOrder(currentTest, task);
		}
	}

	private void setTaskSlidesRandomOrder(SimpleTest test, Task task) {
		List<Integer> order;
		SlideOrder slideOrder = testService.findTaskSlideOrder(test, task);
		if (null == slideOrder) {
			slideOrder = new SlideOrder(test, task);
			order = slideManager.createRandomOrder();
			slideOrder.setOrder(order);
			testService.updateSlideOrder(slideOrder);
		} else {
			order = slideOrder.getOrder();
		}

		slideManager.setOrder(order);
	}

	/**
	 * Handler method for {@link PrepareTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processPrepareTest(@Observes PrepareTestEvent event) {
		log.debug(String.format("processPrepareTest: token uid = %s",
				event.getToken() != null ? event.getToken().getUid() : "NULL"));
		Token token = event.getToken();

		SimpleTest test = testService.getUnattendedTest(token.getUser(), token.getPack(), token.isProduction());
		if (test != null) {
			token.getUser();

			if (event.isStartAllowed()) {
				log.debug(String.format("Test start allowed (test id = %s).",
						test.getId() != null ? test.getId() : "NULL"));
				processTest(test);
			} else {
				procEvent.fire(new AfterPrepareTestEvent(test));
			}
		} else {
			log.error("No test got!");
			procEvent.fire(new ErrorNotificationEvent(Messages.getString("Message.Error.StartTest")));
		}
	}

	/**
	 * Handler method for {@link StartTestEvent}
	 * 
	 * @param event
	 *            observed event class object
	 */
	public void processStartTest(@Observes StartTestEvent event) {
		saveRunningEvent(event);

		renderSlide();
	}

	/**
	 * Process the test by whole pack structure
	 * 
	 * @param test
	 */
	public void processTest(SimpleTest test) {
		log.debug(String.format("processTest: %s", test != null ? test.getId() : "NULL"));
		if (!testProcessing) {
			testProcessing = true;

			currentTest = test;

			currentPack = persistenceService.merge(test.getPack()); // null
																	// value is
																	// checked
																	// in parent
																	// method

			branchManager.setListFromParent(currentPack);
			currentBranch = persistenceService.merge(branchManager.current());

			if (currentBranch != null) {
				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceService.merge(taskManager.current());

				if (currentTask != null) {
					setSlideManagerParent(currentTask);
					currentSlide = slideManager.current();

					if (currentSlide != null) {
						slideProcessing = true;

						if (test.getStatus() == Status.CREATED) {
							log.debug("Test was newly created.");
							procEvent.fire(new StartTestEvent(test));
						} else {
							log.debug("Test continues from last point.");
							procEvent.fire(new ContinueTestEvent(test));
						}
					} else {
						log.debug("There is no slide.");
						procEvent.fire(new FinishTestEvent());
					}
				} else {
					log.debug("There is no task.");
					procEvent.fire(new FinishTestEvent());
				}
			} else {
				log.debug("There is no branch.");
				procEvent.fire(new FinishTestEvent());
			}
		} else {
			log.debug("Test is already processing.");
		}
	}

	/**
	 * go to the following slide according to the direction
	 * 
	 * @param direction
	 */
	public void processSlideFollowing(Direction direction) {
		if (!slideProcessing) {
			slideProcessing = true;

			procEvent.fire(Direction.NEXT.equals(direction) ? new NextSlideEvent() : new PriorSlideEvent());
		} else {
			log.warn("Slide not processing.");
		}
	}

	/**
	 * Make some operations based token data before test can be prepared
	 * 
	 * @param token
	 * @param startAllowed
	 *            if true then test will be started immediately after
	 *            preparation process
	 */
	public void processToken(Token token, boolean startAllowed) {
		if (token != null) {
			setCurrentUser(token.getUser());
			if (checkUserPack(token.getUser(), token.getPack())) {
				procEvent.fire(new PrepareTestEvent(token, startAllowed));
			} else {
				procEvent.fire(new ErrorNotificationEvent(Messages.getString("Message.Error.InsufficientRights")));
			}
		} else {
			log.debug("Invalid token.");
			setCurrentUser(null);
			procEvent.fire(new ErrorNotificationEvent(Messages.getString("Message.Error.Token")));
		}
	}

	private void breakCurrentTest() {
		procEvent.fire(new BreakTestEvent());
	}

	private void renderSlide() {
		if (slideManager.getSlideContainer() != null) {

			procEvent.fire(new RenderContentEvent(slideManager.getSlideContainer()));
		} else {
			fireTestError();
		}
	}

	private void saveBranchOutput() {
		String data = branchManager.getSerializedData();

		BranchOutput branchOutput = new BranchOutput(currentTest, currentBranch);
		branchOutput.setData(data);
		branchOutput.setOutput(data);

		asynchronousService.saveBranchOutput(branchOutput);
	}

	private void saveUserProcessEvent(AbstractUserEvent processEvent) {
		if (currentTest != null) {
			Event event = createEvent(processEvent);

			if (event != null) {
				if (processEvent instanceof ActionEvent) {
					updateActionEventData(event, (ActionEvent) processEvent);
				} else if (processEvent instanceof ComponentEvent) {
					updateComponentEventData(event, (ComponentEvent) processEvent);
				}

				saveTestEvent(currentTest, event, processEvent.getTimestamp(), null);
			}
		}
	}

	private void saveRunningEvent(AbstractRunningEvent processEvent) {
		if (currentTest != null) {
			Event event = createEvent(processEvent);

			if (event != null) {
				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent).getStatus() : null;

				saveTestEvent(currentTest, event, processEvent.getTimestamp(), status);
			}
		}
	}

	private void updateActionEventData(Event event, ActionEvent actionEvent) {
		event.setData(slideManager.getActionData(actionEvent));
	}

	private void updateComponentEventData(Event event, ComponentEvent componentEvent) {
		event.setData(slideManager.getComponentData(componentEvent));

		Date clientTimestamp = componentEvent.getClientTimestamp();
		if (clientTimestamp != null) {
			event.setClientTimeStamp(clientTimestamp.getTime());
		}
	}

	private void saveTestEvent(SimpleTest test, Event event, Date date, Status status) {
		String slideData = event.getType().equals(ProcessEventTypes.getFinishSlideEventId())
				? slideManager.getSerializedSlideData() : null;

		Long testId = test != null ? test.getId() : null;
		Long branchId = currentBranch != null ? currentBranch.getId() : null;
		Long taskId = currentTask != null ? currentTask.getId() : null;
		Long slideId = currentSlide != null ? currentSlide.getId() : null;

		asynchronousService.saveTestEvent(event, date, slideData, status, testId, branchId, taskId, slideId);
	}

	/**
	 * Fires general error in test processing
	 */
	public void fireTestError() {
		procEvent.fire(new ErrorTestEvent());
	}

	/**
	 * Break current test if processing
	 */
	public void requestBreakTest() {
		// test is processing
		if (currentTest != null) {
			breakCurrentTest();
		}
	}

	public void setAutoSlideShow(boolean value) {
		if (!testProcessing) {
			this.autoSlideShow = value;
		}
	}

	public void setCurrentUser(User user) {
		slideManager.setUserId(user != null ? user.getId() : null);
	}

	/**
	 * Do cleanup of process manager
	 */
	public void clean() {
		asynchronousService.cleanup();
	}
}
