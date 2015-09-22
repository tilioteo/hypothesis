/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.engio.mbassy.listener.Handler;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.tilioteo.hypothesis.broadcast.Broadcaster;
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
import com.tilioteo.hypothesis.event.AbstractRunningEvent;
import com.tilioteo.hypothesis.event.AbstractUserEvent;
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
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.persistence.BranchService;
import com.tilioteo.hypothesis.persistence.OutputService;
import com.tilioteo.hypothesis.persistence.PermissionService;
import com.tilioteo.hypothesis.persistence.PersistenceService;
import com.tilioteo.hypothesis.persistence.TestService;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ProcessManager implements Serializable {
	
	private static Logger log = Logger.getLogger(ProcessManager.class);

	private BranchManager branchManager;
	private TaskManager taskManager;

	private SlideManager slideManager;
	
	private PersistenceService persistenceService;
	private TestService testService;
	private BranchService branchService;
	private PermissionService permissionService;
	private OutputService outputService;
	
	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	private SimpleTest currentTest = null;
	private Pack currentPack = null;
	private Branch currentBranch = null;
	private Task currentTask = null;
	private Slide currentSlide = null;
	
	private ProcessEventBus bus = null;
	
	public ProcessManager() {
		this.bus = ProcessEventBus.get(UI.getCurrent());
		bus.register(this);
		
		branchManager = new BranchManager();
		taskManager = new TaskManager();

		slideManager = new SlideManager();
		permissionService = PermissionService.newInstance();
		testService = permissionService.getTestManager();
		persistenceService = PersistenceService.newInstance();
		branchService = BranchService.newInstance();

		outputService = OutputService.newInstance();
		
		Broadcaster.register(slideManager);
	}

	private Event createEvent(ProcessEvent event) {
		if (event != null && event.getName() != null) {
			ProcessEventType processEvent = ProcessEventTypes.get(event.getName());

			if (processEvent != null) {
				return new Event(processEvent.getId(), event.getName(),	event.getTimestamp());
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
	}

	@Handler
	public void processAfterRender(AfterRenderContentEvent event) {
		slideManager.fireEvent(new ViewportEvent.Show(slideManager.getSlide()));

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
	public void processComponentEvent(AbstractComponentEvent<?> event) {
		saveUserProcessEvent(event);
	}

	@Handler
	public void processSlideEvent(SlideEvent event) {
		saveUserProcessEvent(event);
	}

	@Handler
	public void processContinueTest(ContinueTestEvent event) {
		currentTest = event.getTest();
		saveRunningEvent(event);
		
		currentBranch = persistenceService.merge(branchManager.find(currentTest.getLastBranch()));
		
		if (currentBranch != null) {
			
			currentTask = persistenceService.merge(taskManager.find(currentTest.getLastTask()));
			
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

		//taskManager.find(event.getTask());

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
		String key = branchManager.getNextBranchKey();

		Branch nextBranch = null;
		if (branchMap != null && key != null) {
			nextBranch = branchMap.get(key);
		}

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
			currentSlide = slideManager.get(nextIndex-1);
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
		log.debug(String.format("processPrepareTest: token uid = %s", event.getToken() != null ? event.getToken().getUid() : "NULL"));
		Token token = event.getToken();

		SimpleTest test = testService.getUnattendedTest(token.getUser(), token.getPack(), token.isProduction());
		if (test != null) {
			token.getUser();
			
			if (event.isStartAllowed()) {
				log.debug(String.format("Test start allowed (test id = %s).", test.getId() != null ? test.getId() : "NULL"));
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
		if (slideManager.getViewportComponent() != null) {

			bus.post(new RenderContentEvent(
					slideManager.getViewportComponent(), slideManager.getTimers(), slideManager.getShortcutKeys()));
		} else {
			fireTestError();
		}
	}

	private void saveBranchOutput() {
		String data = branchManager.getSerializedData();

		BranchOutput branchOutput = new BranchOutput(currentTest, currentBranch);
		branchOutput.setXmlData(data);
		branchOutput.setOutput(data);

		outputService.saveBranchOutput(branchOutput);
	}

	private void saveUserProcessEvent(AbstractUserEvent processEvent) {
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
				updateTest(currentTest, processEvent.getTimestamp(), null);
			}
		}
	}

	private void saveRunningEvent(AbstractRunningEvent processEvent) {
		if (currentTest != null) {
			Event event = createEvent(processEvent);

			if (event != null) {
				updateEvent(event);

				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent).getStatus() : null;
				updateTest(currentTest, processEvent.getTimestamp(), status);
			}
		}
	}

	public void updateEvent(Event event) {
		if (event != null) {
			event.setBranch(currentBranch);
			event.setTask(currentTask);
			event.setSlide(currentSlide);
			
			if (event.getType().equals(ProcessEventTypes.getFinishSlideEventId())) {
				String slideData = slideManager.getSerializedSlideData();
				event.setXmlData(slideData);
			}
			
			testService.saveEvent(event, currentTest);
		}
	}

	private void updateActionEventData(Event event, ActionEvent actionEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();

		SlideFactory.writeActionData(doc, actionEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}

	private void updateComponentEventData(Event event, AbstractComponentEvent<?> componentEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeComponentData(doc.getRootElement(), componentEvent);

		event.setXmlData(XmlUtility.writeString(doc));
		
		Date clientTimestamp = componentEvent.getClientTimestamp();
		if (clientTimestamp != null) {
			event.setClientTimeStamp(clientTimestamp.getTime());
		}
	}
	
	private void updateSlideEventData(Event event, SlideEvent slideEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeSlideEventData(doc.getRootElement(), slideEvent);
		
		event.setXmlData(XmlUtility.writeString(doc));
	}
	
	private void updateMergedTest(SimpleTest test, Date date) {
		if (test != null) {
			test.setLastAccess(date);
			test.setLastBranch(currentBranch);
			test.setLastTask(currentTask);
			test.setLastSlide(currentSlide);
			testService.updateTest(test);
		}
	}

	public void updateTest(SimpleTest test, Date date, Status status) {
		test = persistenceService.merge(test);

		if (test != null) {
			
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
			
			updateMergedTest(test, date);
		}
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
		slideManager.setUser(user);
	}
	
	public void purgeFactories() {
		SlideFactory.remove(slideManager);
		BranchFactory.remove(branchManager);
	}
	
	public void clean() {
		bus.unregister(this);
		Broadcaster.unregister(slideManager);
	}
}
