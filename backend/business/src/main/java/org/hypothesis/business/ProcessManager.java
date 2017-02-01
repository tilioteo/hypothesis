/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.HasStatus;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchMap;
import org.hypothesis.data.model.BranchOutput;
import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Score;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.SlideOrder;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Task;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.AsynchronousService;
import org.hypothesis.data.service.BranchService;
import org.hypothesis.data.service.OutputService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.PersistenceService;
import org.hypothesis.data.service.SlideService;
import org.hypothesis.data.service.TaskService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.event.data.ScoreData;
import org.hypothesis.event.data.ScoreData.Source;
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
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.server.Messages;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ProcessManager implements Serializable {

	private static final Logger log = Logger.getLogger(ProcessManager.class);

	private final BranchManager branchManager;
	private final TaskManager taskManager;

	private final SlideManager slideManager;

	private final PersistenceService persistenceService;
	private final TestService testService;
	private final BranchService branchService;
	private final PermissionService permissionService;
	private final OutputService outputService;

	private final AsynchronousService asynchronousService;

	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	private SimpleTest currentTest = null;
	private Pack currentPack = null;
	private Branch currentBranch = null;
	private Task currentTask = null;
	private Slide currentSlide = null;

	private final ProcessEventBus bus;

	public ProcessManager(ProcessEventBus bus) {
		this.bus = bus;
		bus.register(this);

		branchManager = new BranchManager();
		taskManager = new TaskManager();
		slideManager = new SlideManager();

		permissionService = PermissionService.newInstance();
		testService = permissionService.getTestManager();
		persistenceService = PersistenceService.newInstance();
		branchService = BranchService.newInstance();

		outputService = OutputService.newInstance();

		asynchronousService = new AsynchronousService(testService, outputService, persistenceService, branchService,
				TaskService.newInstance(), SlideService.newInstance());
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

	@Handler
	public void processActionEvent(ActionEvent event) {
		saveUserProcessEvent(event);
		saveActionScore(event);
	}

	@Handler
	public void processAfterRender(AfterRenderContentEvent event) {
		saveRunningEvent(event);
	}

	@Handler
	public void processBreakTest(BreakTestEvent event) {
		saveRunningEvent(event);

		testProcessing = false;
		slideProcessing = false;
		currentTest = null;
		currentPack = null;
		currentBranch = null;
		currentTask = null;
		currentSlide = null;
	}

	@Handler
	public void processComponentEvent(ComponentEvent event) {
		saveUserProcessEvent(event);
	}

	@Handler
	public void processContinueTest(ContinueTestEvent event) {
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

	@Handler
	public void processError(ErrorTestEvent event) {
		saveRunningEvent(event);

		// TODO add some error description
		bus.post(new ErrorNotificationEvent(Messages.getString("Message.Error.Unspecified")));
	}

	@Handler
	public void processFinishBranch(FinishBranchEvent event) {
		saveRunningEvent(event);

		// TODO process branch result

		saveBranchOutput();

		bus.post(new NextBranchEvent());
	}

	@Handler
	public void processFinishSlide(FinishSlideEvent event) {
		slideManager.finishSlide();
		saveRunningEvent(event);
		saveSlideScore(event);

		slideProcessing = false;

		if (Direction.NEXT.equals(event.getDirection())) {
			branchManager.addSlideOutputs(currentSlide, slideManager.getOutputs());
		}

		if (autoSlideShow) {
			processSlideFollowing(event.getDirection());
		} else {
			bus.post(new AfterFinishSlideEvent(event.getDirection()));
		}
	}

	@Handler
	public void processFinishTask(FinishTaskEvent event) {
		saveRunningEvent(event);

		// taskManager.find(event.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		bus.post(new NextTaskEvent());
	}

	@Handler
	public void processFinishTest(FinishTestEvent event) {
		saveRunningEvent(event);

		currentTest = null;
		testProcessing = false;
	}

	@Handler
	public void processNextBranch(NextBranchEvent event) {
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
						bus.post(new FinishTaskEvent());
					}
				} else {
					bus.post(new FinishBranchEvent());
				}
			} else {
				bus.post(new FinishTestEvent());
			}
		} else {
			bus.post(new FinishTestEvent());
		}
	}

	@Handler
	public void processNextSlide(NextSlideEvent event) {
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
			bus.post(new FinishTaskEvent());
		}
	}

	@Handler
	public void processPriorSlide(PriorSlideEvent event) {
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

	@Handler
	public void processNextTask(NextTaskEvent event) {
		saveRunningEvent(event);

		currentTask = persistenceService.merge(taskManager.next());
		if (currentTask != null) {

			setSlideManagerParent(currentTask);
			currentSlide = slideManager.current();

			if (currentSlide != null) {
				slideProcessing = true;
				renderSlide();
			} else {
				bus.post(new FinishTaskEvent());
			}
		} else {
			bus.post(new FinishBranchEvent());
		}
	}

	private void setSlideManagerParent(Task task) {
		slideManager.setListFromParent(task);

		if (task != null && task.isRandomized()) {
			setTaskSlidesRandomOrder(currentTest, task);
		}
	}

	private void setTaskSlidesRandomOrder(SimpleTest test, Task task) {
		List<Integer> order = null;
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

	@Handler
	public void processPrepareTest(PrepareTestEvent event) {
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
				bus.post(new AfterPrepareTestEvent(test));
			}
		} else {
			log.error("No test got!");
			bus.post(new ErrorNotificationEvent(Messages.getString("Message.Error.StartTest")));
		}
	}

	@Handler
	public void processStartTest(StartTestEvent event) {
		saveRunningEvent(event);

		renderSlide();
	}

	public void processTest(SimpleTest test) {
		log.debug(String.format("processTest: %s", test != null ? test.getId() : "NULL"));
		if (!testProcessing) {
			testProcessing = true;

			currentTest = test;

			currentPack = persistenceService.merge(test.getPack());

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

						if (test.getStatus().equals(Status.CREATED)) {
							log.debug("Test was newly created.");
							bus.post(new StartTestEvent(test));
						} else {
							log.debug("Test continues from last point.");
							bus.post(new ContinueTestEvent(test));
						}
					} else {
						log.debug("There is no slide.");
						bus.post(new FinishTestEvent());
					}
				} else {
					log.debug("There is no task.");
					bus.post(new FinishTestEvent());
				}
			} else {
				log.debug("There is no branch.");
				bus.post(new FinishTestEvent());
			}
		} else {
			log.debug("Test is already processing.");
		}
	}

	public void processSlideFollowing(Direction direction) {
		if (!slideProcessing) {
			slideProcessing = true;

			bus.post((Direction.NEXT.equals(direction)) ? new NextSlideEvent() : new PriorSlideEvent());
		} else {
			log.warn("Slide not processing.");
		}
	}

	public void processToken(Token token, boolean startAllowed) {
		if (token != null) {
			setCurrentUser(token.getUser());
			if (checkUserPack(token.getUser(), token.getPack())) {
				bus.post(new PrepareTestEvent(token, startAllowed));
			} else {
				bus.post(new ErrorNotificationEvent(Messages.getString("Message.Error.InsufficientRights")));
			}
		} else {
			log.debug("Invalid token.");
			setCurrentUser(null);
			bus.post(new ErrorNotificationEvent(Messages.getString("Message.Error.Token")));
		}
	}

	private void breakCurrentTest() {
		bus.post(new BreakTestEvent());
	}

	private void renderSlide() {
		if (slideManager.getSlideContainer() != null) {

			bus.post(new RenderContentEvent(slideManager.getSlideContainer()));
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

	private void saveActionScore(ActionEvent event) {
		if (!event.getAction().getScores().isEmpty()) {
			Score score = new Score(event.getAction().getId(), event.getTimestamp());
			saveScore(currentTest, score,
					new ScoreData(Source.ACTION, event.getAction().getId(), event.getAction().getScores()));
		}
	}

	private void saveSlideScore(FinishSlideEvent event) {
		if (!slideManager.getScores().isEmpty()) {
			Long slideId = currentSlide != null ? currentSlide.getId() : null;
			Score score = new Score(event.getName(), event.getTimestamp());
			saveScore(currentTest, score, new ScoreData(Source.SLIDE, slideId.toString(), slideManager.getScores()));
		}
	}

	private void saveScore(SimpleTest test, Score score, ScoreData data) {
		String scoreData = slideManager.getScoreData(data);

		Long testId = test != null ? test.getId() : null;
		Long branchId = currentBranch != null ? currentBranch.getId() : null;
		Long taskId = currentTask != null ? currentTask.getId() : null;
		Long slideId = currentSlide != null ? currentSlide.getId() : null;

		asynchronousService.saveTestScore(score, scoreData, testId, branchId, taskId, slideId);
	}

	public void fireTestError() {
		bus.post(new ErrorTestEvent());
	}

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

	public void clean() {
		asynchronousService.cleanup();
		bus.unregister(this);
	}
}
