/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import static org.hypothesis.utility.ObjectUtility.getId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.hypothesis.business.data.UserControlData;
import org.hypothesis.business.data.UserSession;
import org.hypothesis.business.data.UserTestState;
import org.hypothesis.data.api.Status;
import org.hypothesis.data.dto.BranchDto;
import org.hypothesis.data.dto.BranchKeyMap;
import org.hypothesis.data.dto.BranchPathMap;
import org.hypothesis.data.dto.EventDto;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.ScoreDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.SlideDto;
import org.hypothesis.data.dto.SlideOrderDto;
import org.hypothesis.data.dto.TaskDto;
import org.hypothesis.data.dto.TestDto;
import org.hypothesis.data.dto.TokenDto;
import org.hypothesis.data.interfaces.HasStatus;
import org.hypothesis.data.service.BranchService;
import org.hypothesis.data.service.OutputService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.SlideOrderService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.impl.BranchServiceImpl;
import org.hypothesis.data.service.impl.OutputServiceImpl;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.SlideOrderServiceImpl;
import org.hypothesis.data.service.impl.TestServiceImpl;
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
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.utility.UIMessageUtility;
import org.hypothesis.utility.UserControlDataUtility;

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

	private static final Set<String> AUDIT_FILTER_EVENTS = Stream.of(ProcessEventTypes.Action,
			ProcessEventTypes.ClientSimEvent, ProcessEventTypes.Message, ProcessEventTypes.Null)
			.collect(Collectors.toSet());

	private final BranchManager branchManager;
	private final TaskManager taskManager;

	private final SlideManager slideManager;

	private final TestService testService;
	private final BranchService branchService;
	private final PermissionService permissionService;
	private final OutputService outputService;
	private final SlideOrderService slideOrderService;
	private final UserControlServiceImpl userControlService;

	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	private TestDto currentTest = null;
	private PackDto currentPack = null;
	private BranchDto currentBranch = null;
	private TaskDto currentTask = null;
	private SlideDto currentSlide = null;

	private BranchPathMap branchPathMap = null;

	// private User currentUser = null;
	private SimpleUserDto currentUser = null;
	private String mainUID = null;

	private final ProcessEventBus bus;
	private final ExportVNManager exportVNManager;

	private ExecutorService executorService;

	public ProcessManager(ProcessEventBus bus) {
		this.bus = bus;
		bus.register(this);

		branchManager = new BranchManager();
		taskManager = new TaskManager();
		slideManager = new SlideManager();

		permissionService = new PermissionServiceImpl();
		testService = new TestServiceImpl();
		branchService = new BranchServiceImpl();
		outputService = new OutputServiceImpl();
		slideOrderService = new SlideOrderServiceImpl();

		userControlService = new UserControlServiceImpl();

		/*
		 * asynchronousService = new
		 * AsynchronousService(TestService.newInstance(),
		 * OutputService.newInstance(), PersistenceService.newInstance(),
		 * BranchService.newInstance(), TaskService.newInstance(),
		 * SlideService.newInstance());
		 */

		exportVNManager = new ExportVNManager();
	}

	private EventDto createEvent(AbstractProcessEvent event) {
		if (event != null && event.getName() != null) {
			ProcessEventType processEvent = ProcessEventTypes.get(event.getName());

			if (processEvent != null) {
				final EventDto eventDto = new EventDto();
				eventDto.setType(processEvent.getId());
				eventDto.setName(event.getName());
				eventDto.setTimeStamp(event.getTimestamp());
				return eventDto;
			}
		}

		return null;
	}

	/*
	 * private boolean checkUserPack(User user, Pack pack) { Collection<Pack>
	 * packs; if (user != null) { // packs =
	 * permissionService.findUserPacks(user, true); packs =
	 * permissionService.getUserPacksVN(user); for (Pack allowedPack : packs) {
	 * if (allowedPack.getId().equals(pack.getId())) return true; } }
	 * 
	 * packs = permissionService.getPublishedPacks(); for (Pack allowedPack :
	 * packs) { if (allowedPack.getId().equals(pack.getId())) return true; }
	 * 
	 * return false; }
	 */

	private boolean checkUserPack(Long userId, long packId) {
		return permissionService.userCanAccess(userId, packId);
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

		branchManager.findById(currentTest.getLastBranchId());
		currentBranch = branchManager.current();

		if (currentBranch != null) {

			taskManager.setList(currentBranch.getTasks());
			taskManager.findById(currentTest.getLastTaskId());
			currentTask = taskManager.current();

			if (currentTask != null) {

				setSlideManagerParent(currentTask);
				slideManager.findById(currentTest.getLastSlideId());
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

		tryDisablePack();

		exportScores(currentTest.getId());

		currentTest = null;
		testProcessing = false;
		// setCurrentUser(null);
		setCurrentUser(null);
	}

	private void exportScores(Long id) {
		exportVNManager.exportSingleTestScore(id);
	}

	@Handler
	public void processNextBranch(NextBranchEvent event) {
		saveRunningEvent(event);

		BranchKeyMap branchMap = branchPathMap.get(currentBranch.getId());
		BranchDto nextBranch = branchManager.getNextBranch(branchMap);

		if (nextBranch != null) {
			currentBranch = nextBranch;

			if (currentBranch != null) {

				taskManager.setList(currentBranch.getTasks());
				currentTask = taskManager.current();

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
		} else if (nextIndex < 0 || nextIndex > slideManager.size()) {
			currentSlide = null;
		} else {
			currentSlide = slideManager.findByIndex(nextIndex - 1);
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

		SlideDto slide = slideManager.prior();

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

		currentTask = taskManager.next();
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

	private void setSlideManagerParent(TaskDto task) {
		slideManager.setList(task.getSlides());

		if (task != null && task.isRandomized()) {
			setTaskSlidesRandomOrder(currentTest, task);
		}
	}

	private void setTaskSlidesRandomOrder(TestDto test, TaskDto task) {
		List<Integer> order = null;
		SlideOrderDto slideOrder = slideOrderService.findSlideOrder(test.getId(), task.getId());
		if (null == slideOrder) {
			order = slideManager.createRandomOrder();
			slideOrder = slideOrderService.saveSlideOrder(test.getId(), task.getId(), order);
		} else {
			order = slideOrder.getOrder();
		}

		slideManager.setOrder(order);
	}

	@Handler
	public void processPrepareTest(PrepareTestEvent event) {
		log.debug(String.format("processPrepareTest: token uid = %s",
				event.getToken() != null ? event.getToken().getId() : "NULL"));
		TokenDto token = event.getToken();
		mainUID = token.getViewUid();

		TestDto test = testService.getUnattendedTest(getId(token.getUser()), token.getPackId(), token.isProduction());
		if (test != null) {
			branchPathMap = branchService.getBranchPathMap(test.getPack().getId());

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
		// tryDisablePack();

		renderSlide();
	}

	/*
	 * private void tryDisablePack() { if (currentUser != null &&
	 * currentUser.getAutoDisable()) {
	 * permissionService.deleteUserPermissionVN(currentUser, currentPack); }
	 * 
	 * }
	 */

	private void tryDisablePack() {
		if (currentUser != null && currentUser.isAutoDisable()) {
			permissionService.disableForVN(currentUser.getId(), currentPack.getId());
		}

	}

	public void processTest(TestDto test) {
		log.debug(String.format("processTest: %s", test != null ? test.getId() : "NULL"));
		if (!testProcessing) {
			testProcessing = true;

			currentTest = test;

			currentPack = test.getPack();

			branchManager.setList(currentPack.getBranches());
			currentBranch = branchManager.current();

			if (currentBranch != null) {
				taskManager.setList(currentBranch.getTasks());
				currentTask = taskManager.current();

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

	/*
	 * public void processToken(Token token, boolean startAllowed) { if (token
	 * != null) { setCurrentUser(token.getUser()); if
	 * (checkUserPack(token.getUserId, token.getPackId())) { bus.post(new
	 * PrepareTestEvent(token, startAllowed)); } else { bus.post(new
	 * ErrorNotificationEvent(Messages.getString(
	 * "Message.Error.InsufficientRights"))); } } else {
	 * log.debug("Invalid token."); setCurrentUser(null); bus.post(new
	 * ErrorNotificationEvent(Messages.getString("Message.Error.Token"))); } }
	 */

	public void processToken(TokenDto token, boolean startAllowed) {
		if (token != null) {
			setCurrentUser(token.getUser());
			if (checkUserPack(getId(token.getUser()), token.getPackId())) {
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
		async(() -> outputService.saveBranchOutput(currentBranch.getId(), currentTest.getId(),
				branchManager.getSerializedData()));
	}

	private void saveUserProcessEvent(AbstractUserEvent processEvent) {
		if (currentTest != null) {
			EventDto event = createEvent(processEvent);

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
			EventDto event = createEvent(processEvent);

			if (event != null) {
				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent).getStatus() : null;

				saveTestEvent(currentTest, event, processEvent.getTimestamp(), status);
			}
		}
	}

	private void updateActionEventData(EventDto event, ActionEvent actionEvent) {
		event.setData(slideManager.getActionData(actionEvent));
	}

	private void updateComponentEventData(EventDto event, ComponentEvent componentEvent) {
		event.setData(slideManager.getComponentData(componentEvent));

		Date clientTimestamp = componentEvent.getClientTimestamp();
		if (clientTimestamp != null) {
			event.setClientTimeStamp(clientTimestamp);
		}
	}

	private void saveTestEvent(TestDto test, EventDto event, Date date, Status status) {
		String slideData = event.getType().equals(ProcessEventTypes.getFinishSlideEventId())
				? slideManager.getSerializedSlideData() : null;

		event.setTestId(test.getId());
		event.setBranchId(getId(currentBranch));
		event.setTaskId(getId(currentTask));
		event.setSlideId(getId(currentSlide));
		event.setTimeStamp(date);
		event.setData(slideData);

		async(() -> testService.saveEvent(event, status));

		updateTestAudit(date, currentUser, event, test, currentPack, currentBranch, currentTask, currentSlide);
	}

	private void saveActionScore(ActionEvent event) {
		if (!event.getAction().getScores().isEmpty()) {
			ScoreDto score = new ScoreDto();
			score.setName(event.getAction().getId());
			score.setTimeStamp(event.getTimestamp());
			saveScore(currentTest, score,
					new ScoreData(Source.ACTION, event.getAction().getId(), event.getAction().getScores()));
		}
	}

	private void saveSlideScore(FinishSlideEvent event) {
		if (!slideManager.getScores().isEmpty()) {
			Long slideId = getId(currentSlide);
			ScoreDto score = new ScoreDto();
			score.setName(event.getName());
			score.setTimeStamp(event.getTimestamp());
			saveScore(currentTest, score, new ScoreData(Source.SLIDE, slideId.toString(), slideManager.getScores()));
		}
	}

	private void saveScore(TestDto test, ScoreDto score, ScoreData data) {
		score.setData(slideManager.getScoreData(data));
		score.setTestId(test.getId());
		score.setBranchId(getId(currentBranch));
		score.setTaskId(getId(currentTask));
		score.setSlideId(getId(currentSlide));

		async(() -> testService.saveScore(score));
	}

	private void updateTestAudit(Date date, SimpleUserDto user, EventDto event, TestDto test, PackDto pack,
			BranchDto branch, TaskDto task, SlideDto slide) {
		if (!AUDIT_FILTER_EVENTS.contains(event.getName())) {
			UserControlData data = userControlService.ensureUserControlData(user);
			userControlService.updateUserControlDataWithSession(data, mainUID);
			UserSession session = UserControlDataUtility.getUserSession(data, mainUID);
			if (session != null) {

				UserTestState state = ensureTestState(session);

				state.setEventTime(date);
				state.setEventName(event.getName());

				if (pack != null) {
					state.setPackId(pack.getId());
					state.setPackName(pack.getName());
					state.setPackDescription(pack.getDescription());
				} else {
					state.setPackId(null);
					state.setPackName("");
					state.setPackDescription("");
				}

				if (branch != null) {
					state.setBranchName(branch.getId().toString());
					state.setBranchDescription(branch.getNote());
				} else {
					state.setBranchName("");
					state.setBranchDescription("");
				}

				if (task != null) {
					state.setTaskName(task.getName());
					state.setTaskDescription(task.getNote());
				} else {
					state.setTaskName("");
					state.setTaskDescription("");
				}

				if (slide != null) {
					state.setSlideName(slide.getId().toString());
					state.setSlideDescription(slide.getNote());
				} else {
					state.setSlideName("");
					state.setSlideDescription("");
				}

				BroadcastService.broadcast(UIMessageUtility.createRefreshUserTestStateMessage(user.getId()));
			}
		}
	}

	private void async(Runnable runnable) {
		if (executorService == null) {
			executorService = Executors.newSingleThreadExecutor();
		}

		executorService.submit(runnable);
	}

	private void shutdownExecutorService() {
		if (executorService != null) {
			try {
				log.info("Attempting to shutdown executor service.");
				executorService.shutdown();
				executorService.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.error("Executor service interupted after 10 seconds.");
			} finally {
				if (!executorService.isTerminated()) {
					log.error("Terminating executor service.");
				}
				executorService.shutdownNow();
			}
		}
	}

	private UserTestState ensureTestState(UserSession session) {
		UserTestState state = session.getState();
		if (state == null) {
			state = new UserTestState();
			session.setState(state);
		}

		return state;
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

	/*
	 * public void setCurrentUser(User user) {
	 * SessionManager.setLoggedUser(user); currentUser = user;
	 * slideManager.setUserId(user != null ? user.getId() : null); }
	 */

	public void setCurrentUser(SimpleUserDto user) {
		SessionManager.setLoggedUser2(user);
		currentUser = user;
		slideManager.setUserId(getId(user));
	}

	public void clean() {
		bus.unregister(this);
		shutdownExecutorService();
	}
}
