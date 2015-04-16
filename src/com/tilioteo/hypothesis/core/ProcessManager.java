/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchMap;
import com.tilioteo.hypothesis.entity.BranchOutput;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.SlideOrder;
import com.tilioteo.hypothesis.entity.Status;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.AbstractProcessEvent;
import com.tilioteo.hypothesis.event.ActionEvent;
import com.tilioteo.hypothesis.event.AfterFinishSlideEvent;
import com.tilioteo.hypothesis.event.AfterPrepareTestEvent;
import com.tilioteo.hypothesis.event.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.BreakTestEvent;
import com.tilioteo.hypothesis.event.ContinueTestEvent;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.ErrorTestEvent;
import com.tilioteo.hypothesis.event.FinishBranchEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.FinishTaskEvent;
import com.tilioteo.hypothesis.event.FinishTestEvent;
import com.tilioteo.hypothesis.event.NextBranchEvent;
import com.tilioteo.hypothesis.event.NextSlideEvent;
import com.tilioteo.hypothesis.event.NextTaskEvent;
import com.tilioteo.hypothesis.event.PrepareTestEvent;
import com.tilioteo.hypothesis.event.PriorSlideEvent;
import com.tilioteo.hypothesis.event.ProcessEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.event.ProcessEventType;
import com.tilioteo.hypothesis.event.ProcessEventTypes;
import com.tilioteo.hypothesis.event.RenderContentEvent;
import com.tilioteo.hypothesis.event.SlideEvent;
import com.tilioteo.hypothesis.event.StartTestEvent;
import com.tilioteo.hypothesis.persistence.OutputManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.PersistenceManager;
import com.tilioteo.hypothesis.persistence.TestManager;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessManager {
	
	private static Logger log = Logger.getLogger(ProcessManager.class);

	private BranchManager branchManager;
	private TaskManager taskManager;

	private SlideManager slideManager;
	
	private PersistenceManager persistenceManager;
	private TestManager testManager;
	private com.tilioteo.hypothesis.persistence.BranchManager persistenceBranchManager;
	private PermissionManager permissionManager;
	private OutputManager outputManager;
	
	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	private SimpleTest currentTest = null;
	private Pack currentPack = null;
	private Branch currentBranch = null;
	private Task currentTask = null;
	private Slide currentSlide = null;

	public ProcessManager() {
		
		ProcessEventBus.get().register(this);
		
		branchManager = new BranchManager();
		taskManager = new TaskManager();

		slideManager = new SlideManager();
		permissionManager = PermissionManager.newInstance();
		testManager = permissionManager.getTestManager();
		persistenceManager = PersistenceManager.newInstance();
		persistenceBranchManager = com.tilioteo.hypothesis.persistence.BranchManager.newInstance();

		outputManager = OutputManager.newInstance();
	}

	private Event createEvent(ProcessEvent event) {
		log.debug(String.format("createEvent: name = %s, timestamp = %s", event != null ? event.getName() : "NULL", event != null ? event.getTimestamp().toString() : "NULL"));
		if (event != null && event.getName() != null) {
			ProcessEventType processEvent = ProcessEventTypes.get(event.getName());
			if (processEvent != null) {
				return new Event(processEvent.getId(), event.getName(),	event.getTimestamp());
			}
		}
		return null;
	}

	private boolean checkUserPack(User user, Pack pack) {
		return true;

		// TODO use code bellow when the security will be implemented
		
		/*Collection<Pack> packs;
		if (null != user) {
			packs = permissionManager.findUserPacks(user, true);
		} else {
			packs = permissionManager.getPublishedPacks();
		}
		for (Pack pack2 : packs) {
			if (pack2.getId().equals(pack.getId()))
				return true;
		}
		return false;*/
	}

	@Subscribe
	public void processActionEvent(ActionEvent event) {
		log.debug(String.format("processActionEvent: action id = %s", event.getAction() != null ? event.getAction().getId() : "NULL"));
		saveActionEvent(event);
		
	}

	@Subscribe
	public void processAfterRender(AfterRenderContentEvent eventObj) {
		log.debug("processAfterRender::");
		slideManager.fireEvent(new SlideManager.ShowEvent(currentSlide));

		saveProcessEvent(eventObj);
	}

	@Subscribe
	public void processBreakTest(BreakTestEvent eventObj) {
		log.debug(String.format("processBreakTest: test id = %s", eventObj.getTest() != null ? eventObj.getTest().getId() : "NULL"));
		saveProcessEvent(eventObj);

		currentTest = null;
		testProcessing = false;
	}

	@Subscribe
	public void processComponentEvent(AbstractComponentEvent<?> event) {
		log.debug(String.format("processComponentEvent: component id = %s", event.getComponentData() != null ? event.getComponentData().getComponentId() : "NULL"));
		saveComponentEvent(event);
	}

	@Subscribe
	public void processSlideEvent(SlideEvent event) {
		log.debug(String.format("processSlideEvent: slide id = %s", event.getComponentData() != null ? event.getComponentData().getComponentId() : "NULL"));
		saveSlideEvent(event);
	}

	@Subscribe
	public void processContinueTest(ContinueTestEvent eventObj) {
		log.debug(String.format("processContinueTest: test id = %s", eventObj.getTest() != null ? eventObj.getTest().getId() : "NULL"));
		currentTest = eventObj.getTest();
		saveProcessEvent(eventObj);
		
		currentBranch = persistenceManager.merge(branchManager.find(currentTest.getLastBranch()));
		
		if (currentBranch != null) {
			
			currentTask = persistenceManager.merge(taskManager.find(currentTest.getLastTask()));
			
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

	@Subscribe
	public void processError(ErrorTestEvent eventObj) {
		log.debug(String.format("processError: id = %s", eventObj.getTest() != null ? eventObj.getTest().getId() : "NULL"));
		if (SimpleTest.DUMMY_TEST != eventObj.getTest()) {
			saveProcessEvent(eventObj);
		}

		// TODO add some error description
		ProcessEventBus.get().post(new ErrorNotificationEvent(eventObj.getTest(),
				Messages.getString("Error.Unspecified")));
	}

	@Subscribe
	public void processFinishBranch(FinishBranchEvent eventObj) {
		log.debug(String.format("processFinishBranch: branch id = %s", eventObj.getBranch() != null ? eventObj.getBranch().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//branchManager.find(eventObj.getBranch());

		// TODO process branch result

		saveBranchOutput();

		ProcessEventBus.get().post(new NextBranchEvent(currentBranch));
	}

	@Subscribe
	public void processFinishSlide(FinishSlideEvent eventObj) {
		log.debug(String.format("processFinishSlide: slide id = %s", eventObj.getSlide() != null ? eventObj.getSlide().getId() : "NULL"));

		slideManager.finishSlide();
		saveProcessEvent(eventObj);

		slideProcessing = false;
		
		if (autoSlideShow) {
			log.debug("Auto slideshow enabled.");
			processSlideFollowing(currentSlide, eventObj.getDirection());
		} else {
			ProcessEventBus.get().post(new AfterFinishSlideEvent(currentSlide, eventObj.getDirection()));
		}
	}

	@Subscribe
	public void processFinishTask(FinishTaskEvent eventObj) {
		log.debug(String.format("processFinishTask: task id = %s", eventObj.getTask() != null ? eventObj.getTask().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//taskManager.find(eventObj.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		ProcessEventBus.get().post(new NextTaskEvent(currentTask));
	}

	@Subscribe
	public void processFinishTest(FinishTestEvent eventObj) {
		log.debug(String.format("processFinishTest: test id = %s", eventObj.getTest() != null ? eventObj.getTest().getId() : "NULL"));
		saveProcessEvent(eventObj);
		
		currentTest = null;
		testProcessing = false;
	}

	@Subscribe
	public void processNextBranch(NextBranchEvent eventObj) {
		log.debug(String.format("processNextBranch: next id = %s", eventObj.getBranch() != null ? eventObj.getBranch().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//branchManager.find(eventObj.getBranch());
		//currentBranch = persistenceManager.merge(branchManager.current());
		
		BranchMap branchMap = persistenceBranchManager.getBranchMap(currentPack, currentBranch);
		String key = branchManager.getNextBranchKey();

		Branch nextBranch = null;
		if (branchMap != null && key != null) {
			nextBranch = branchMap.get(key);
		}

		if (nextBranch != null) {
			currentBranch = persistenceManager.merge(branchManager.find(nextBranch));

			if (currentBranch != null) {
				log.debug(String.format("Branch = %s", currentBranch.getId() != null ? currentBranch.getId() : "NULL"));
			
				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceManager.merge(taskManager.current());
				
				if (currentTask != null) {
					log.debug(String.format("Task = %s", currentTask.getId() != null ? currentTask.getId() : "NULL"));
			
					setSlideManagerParent(currentTask);
					currentSlide = slideManager.current();
			
					if (currentSlide != null) {
						log.debug(String.format("Slide = %s", currentSlide.getId() != null ? currentSlide.getId() : "NULL"));
						slideProcessing = true;
						renderSlide();
					} else {
						log.debug("There are no more slides in task.");
						ProcessEventBus.get().post(new FinishTaskEvent(currentTask));
					}
				} else {
					log.debug("There are no more tasks in branch.");
					ProcessEventBus.get().post(new FinishBranchEvent(currentBranch));
				}
			} else {
				log.debug("There are no more branches in pack.");
				ProcessEventBus.get().post(new FinishTestEvent(currentTest));
			}
		} else {
			log.debug("There is not next branch.");
			ProcessEventBus.get().post(new FinishTestEvent(currentTest));
		}
	}

	@Subscribe
	public void processNextSlide(NextSlideEvent eventObj) {
		log.debug(String.format("processNextSlide: next id = %s", eventObj.getSlide() != null ? eventObj.getSlide().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//slideManager.find(eventObj.getSlide());

		currentSlide = slideManager.next();
		if (currentSlide != null) {
			log.debug(String.format("Slide = %s", currentSlide.getId() != null ? currentSlide.getId() : "NULL"));
			slideProcessing = true;
			renderSlide();
		} else {
			log.debug("There are no more slides in task.");
			ProcessEventBus.get().post(new FinishTaskEvent(currentTask));
		}
	}

	@Subscribe
	public void processPriorSlide(PriorSlideEvent eventObj) {
		log.debug(String.format("processPriorSlide: prior id = %s", eventObj.getSlide() != null ? eventObj.getSlide().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//slideManager.find(eventObj.getSlide());
		
		Slide slide = slideManager.prior();
		if (slide != null) {
			currentSlide = slide;
		}
		
		if (currentSlide != null) {
			log.debug(String.format("Slide = %s", currentSlide.getId() != null ? currentSlide.getId() : "NULL"));
			slideProcessing = true;
			renderSlide();
		} else {
			// TODO what will happen when there is not prior slide?
		}
	}

	@Subscribe
	public void processNextTask(NextTaskEvent eventObj) {
		log.debug(String.format("processNextTask: next id = %s", eventObj.getTask() != null ? eventObj.getTask().getId() : "NULL"));
		saveProcessEvent(eventObj);

		currentTask = persistenceManager.merge(taskManager.next());
		if (currentTask != null) {
			log.debug(String.format("Task = %s", currentTask.getId() != null ? currentTask.getId() : "NULL"));

			setSlideManagerParent(currentTask);
			currentSlide = slideManager.current();
			if (currentSlide != null) {
				log.debug(String.format("Slide = %s", currentSlide.getId() != null ? currentSlide.getId() : "NULL"));
				slideProcessing = true;
				renderSlide();
			} else {
				log.debug("There are no more slides in task.");
				ProcessEventBus.get().post(new FinishTaskEvent(currentTask));
			}
		} else {
			log.debug("There are no more tasks in branch.");
			ProcessEventBus.get().post(new FinishBranchEvent(currentBranch));
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
		SlideOrder slideOrder = testManager.findTaskSlideOrder(test, task);
		if (null == slideOrder) {
			slideOrder = new SlideOrder(test, task);
			order = slideManager.createRandomOrder();
			slideOrder.setOrder(order);
			testManager.updateSlideOrder(slideOrder);
		} else {
			order = slideOrder.getOrder();
		}
		
		slideManager.setOrder(order);
	}

	@Subscribe
	public void processPrepareTest(PrepareTestEvent eventObj) {
		log.debug(String.format("processPrepareTest: token uid = %s", eventObj.getToken() != null ? eventObj.getToken().getUid() : "NULL"));
		Token token = eventObj.getToken();

		SimpleTest test = testManager.getUnattendedTest(token.getUser(), token.getPack(), token.isProduction());
		if (test != null) {
			if (eventObj.isStartAllowed()) {
				log.debug(String.format("Test start allowed (test id = %s).", test.getId() != null ? test.getId() : "NULL"));
				processTest(test);
			} else {
				ProcessEventBus.get().post(new AfterPrepareTestEvent(test));
			}
		} else {
			ProcessEventBus.get().post(new ErrorNotificationEvent(SimpleTest.DUMMY_TEST, Messages.getString("Error.StartTest")));
		}
	}

	@Subscribe
	public void processStartTest(StartTestEvent eventObj) {
		log.debug(String.format("processStartTest: test id = %s", eventObj.getTest() != null ? eventObj.getTest().getId() : "NULL"));
		saveProcessEvent(eventObj);

		//currentTest = eventObj.getTest();

		renderSlide();
	}

	public void processTest(SimpleTest test) {
		log.debug(String.format("processTest: %s", test != null ? test.getId() : "NULL"));
		if (!testProcessing) {
			testProcessing = true;
			
			currentTest = test;
			
			currentPack = persistenceManager.merge(test.getPack());
			log.debug(String.format("Pack = %s", currentPack != null ? currentPack.getId() : "NULL"));
			
			branchManager.setListFromParent(currentPack);
			currentBranch = persistenceManager.merge(branchManager.current());
			
			if (currentBranch != null) {
				log.debug(String.format("Branch = %s", currentBranch != null ? currentBranch.getId() : "NULL"));
			
				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceManager.merge(taskManager.current());
				
				if (currentTask != null) {
					log.debug(String.format("Task = %s", currentTask != null ? currentTask.getId() : "NULL"));
			
					setSlideManagerParent(currentTask);
					currentSlide = slideManager.current();
			
					if (currentSlide != null) {
						slideProcessing = true;
			
						if (test.getStatus().equals(Status.CREATED)) {
							log.debug("Test was newly created.");
							ProcessEventBus.get().post(new StartTestEvent(test));
						} else {
							log.debug("Test continues from last point.");
							ProcessEventBus.get().post(new ContinueTestEvent(test));
						}
					} else {
						log.debug("There is no slide.");
						ProcessEventBus.get().post(new FinishTestEvent(currentTest));
					}
				} else {
					log.debug("There is no task.");
					ProcessEventBus.get().post(new FinishTestEvent(currentTest));
				}
			} else {
				log.debug("There is no branch.");
				ProcessEventBus.get().post(new FinishTestEvent(currentTest));
			}
		} else {
			log.debug("Test is already processing.");
		}
	}
	
	public void processSlideFollowing(Slide slide, Direction direction) {
		log.debug(String.format("processSlideFollowing: slide id = %s, direction = %s", slide != null ? slide.getId() : "NULL", direction != null ? direction.name() : "NULL"));
		if (!slideProcessing) {
			slideProcessing = true;
			
			ProcessEventBus.get().post((Direction.NEXT.equals(direction)) ? new NextSlideEvent(slide) : new PriorSlideEvent(slide));
		} else {
			log.warn("Slide not processing.");
		}
	}

	public void processToken(Token token, boolean startAllowed) {
		log.debug(String.format("processToken: id = %s, startAllowed = %s", token != null ? token.getUid() : "NULL", Boolean.toString(startAllowed)));
		if (token != null) {

			if (checkUserPack(token.getUser(), token.getPack())) {
				ProcessEventBus.get().post(new PrepareTestEvent(token, startAllowed));
			} else {
				// TODO set localizable resource
				ProcessEventBus.get().post(new ErrorNotificationEvent(
						SimpleTest.DUMMY_TEST, Messages.getString("Error.InsufficientRights")));
			}
		} else {
			log.debug("Invalid token.");
			// TODO set localizable resource
			ProcessEventBus.get().post(new ErrorNotificationEvent(
					SimpleTest.DUMMY_TEST, Messages.getString("Error.Token")));
		}
	}

	private void breakCurrentTest() {
		log.debug("breakCurrentTest::");
		ProcessEventBus.get().post(new BreakTestEvent(this.currentTest));
	}

	private void renderSlide() {
		log.debug("renderSlide::");
		if (slideManager.getViewport() != null &&
				slideManager.getViewport().getComponent() != null) {
			ProcessEventBus.get().post(new RenderContentEvent(
					slideManager.getViewport(), slideManager.getTimers(), slideManager.getShortcutKeys()));
		} else {
			fireTestError();
		}
	}

	private void saveBranchOutput() {
		log.debug("saveBranchOutput::");
		String data = branchManager.getSerializedData();

		String output = branchManager.getNextBranchKey();

		BranchOutput branchOutput = new BranchOutput(currentTest, currentBranch);
		branchOutput.setXmlData(data);
		branchOutput.setOutput(output);

		outputManager.saveBranchOutput(branchOutput);
	}

	private void saveActionEvent(ActionEvent actionEvent) {
		log.debug(String.format("saveActionEvent: name = %s", actionEvent.getName()));
		saveTestProcessEvent(actionEvent);
	}

	private void saveComponentEvent(AbstractComponentEvent<?> componentEvent) {
		log.debug(String.format("saveComponentEvent: name = %s", componentEvent.getName()));
		componentEvent.updateTimestamp();
		saveTestProcessEvent(componentEvent);
	}
	
	private void saveSlideEvent(SlideEvent slideEvent) {
		log.debug(String.format("saveSlideEvent: name = %s", slideEvent.getName()));
		slideEvent.updateTimestamp();
		saveTestProcessEvent(slideEvent);
	}
	
	private void saveTestProcessEvent(ProcessEvent processEvent) {
		log.debug(String.format("saveTestProcessEvent: name = %s", processEvent.getName()));
		if (currentTest != null) {
			Event event = createEvent(processEvent);
			if (event != null) {
				if (processEvent instanceof ActionEvent) {
					updateActionEventData(event, (ActionEvent)processEvent);
				} else if (processEvent instanceof AbstractComponentEvent) {
					updateComponentEventData(event, (AbstractComponentEvent<?>)processEvent);
				} else if (processEvent instanceof SlideEvent) {
					updateSlideEventData(event, (SlideEvent)processEvent);
				}
				updateEvent(event);
				updateTest(currentTest, processEvent.getTimestamp());
			}
		}
	}

	private void saveProcessEvent(AbstractProcessEvent processEvent) {
		log.debug(String.format("saveProcessEvent: name = %s", processEvent.getName()));
		/*if (processEvent instanceof AbstractTestEvent)
			currentTest = ((AbstractTestEvent) processEvent).getTest();*/

		if (currentTest != null) {
			Event event = createEvent(processEvent);
			if (event != null) {
				updateEvent(event);

				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent).getStatus() : null;
				if (status != null)
					updateTest(currentTest, processEvent.getTimestamp(), status);
				else
					updateTest(currentTest, processEvent.getTimestamp());
			}
		}
	}

	public void updateEvent(Event event) {
		log.debug("updateEvent::");
		if (event != null) {
			event.setBranch(currentBranch);
			event.setTask(currentTask);
			event.setSlide(currentSlide);
			
			if (event.getType().equals(ProcessEventTypes.getFinishSlideEventId())) {
				String slideData = slideManager.getSerializedSlideData();
				event.setXmlData(slideData);
			}
			
			testManager.saveEvent(event, currentTest);
		}
	}

	private void updateActionEventData(Event event, ActionEvent actionEvent) {
		log.debug("updateActionEventData::");
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeActionData(doc, actionEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}

	private void updateComponentEventData(Event event, AbstractComponentEvent<?> componentEvent) {
		log.debug("updateComponentEventData::");
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeComponentData(doc.getRootElement(), componentEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}
	
	private void updateSlideEventData(Event event, SlideEvent slideEvent) {
		log.debug("updateComponentEventData::");
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeSlideEventData(doc.getRootElement(), slideEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}
	
	private void updateMergedTest(SimpleTest test, Date date) {
		log.debug(String.format("updateMergedTest: test id = %s, date = %s", test != null ? test.getId() : "NULL", date != null ? date.toString() : "NULL"));
		if (test != null) {
			test.setLastAccess(date);
			test.setLastBranch(currentBranch);
			test.setLastTask(currentTask);
			test.setLastSlide(currentSlide);
			testManager.updateTest(test);
		}
	}

	private void updateTest(SimpleTest test, Date date) {
		log.debug(String.format("updateTest: test id = %s, date = %s", test != null ? test.getId() : "NULL", date != null ? date.toString() : "NULL"));
		test = persistenceManager.merge(test);
		updateMergedTest(test, date);
	}

	public void updateTest(SimpleTest test, Date date, Status status) {
		log.debug(String.format("updateTest: test id = %s, date = %s, status = %s", test != null ? test.getId() : "NULL", date != null ? date.toString() : "NULL", status != null ? status.getCode() : "NULL"));
		test = persistenceManager.merge(test);
		if (test != null && !test.getStatus().equals(status)) {
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
			updateMergedTest(test, date);
		}
	}
	
	public void fireTestError() {
		if (currentTest != null) {
			ProcessEventBus.get().post(new ErrorTestEvent(currentTest));
		} else {
			ProcessEventBus.get().post(new ErrorTestEvent(SimpleTest.DUMMY_TEST));
		}
	}
	
	public void requestBreakTest() {
		log.debug("requestBreakTest::");
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
	
	public void purgeFactories() {
		SlideFactory.remove(slideManager);
		BranchFactory.remove(branchManager);
	}
}
