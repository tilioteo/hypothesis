/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Collection;
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
	
	private User currentUser = null;

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
			packs = permissionManager.findUserPacks(user, true);
			for (Pack allowedPack : packs) {
				if (allowedPack.getId().equals(pack.getId()))
					return true;
			}
		}

		packs = permissionManager.getPublishedPacks();
		for (Pack allowedPack : packs) {
			if (allowedPack.getId().equals(pack.getId()))
				return true;
		}
		return false;
	}

	@Subscribe
	public void processActionEvent(ActionEvent event) {
		saveUserProcessEvent(event);
	}

	@Subscribe
	public void processAfterRender(AfterRenderContentEvent event) {
		slideManager.fireEvent(new ViewportEvent.Show(slideManager.getSlide()));

		saveRunningEvent(event);
	}

	@Subscribe
	public void processBreakTest(BreakTestEvent event) {
		saveRunningEvent(event);

		currentTest = null;
		testProcessing = false;
		
		currentUser = null;
	}

	@Subscribe
	public void processComponentEvent(AbstractComponentEvent<?> event) {
		saveUserProcessEvent(event);
	}

	@Subscribe
	public void processSlideEvent(SlideEvent event) {
		saveUserProcessEvent(event);
	}

	@Subscribe
	public void processContinueTest(ContinueTestEvent event) {
		currentTest = event.getTest();
		saveRunningEvent(event);
		
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
	public void processError(ErrorTestEvent event) {
		saveRunningEvent(event);

		// TODO add some error description
		ProcessEventBus.get().post(new ErrorNotificationEvent(Messages.getString("Message.Error.Unspecified")));
	}

	@Subscribe
	public void processFinishBranch(FinishBranchEvent event) {
		saveRunningEvent(event);

		// TODO process branch result

		saveBranchOutput();

		ProcessEventBus.get().post(new NextBranchEvent());
	}

	@Subscribe
	public void processFinishSlide(FinishSlideEvent event) {
		slideManager.finishSlide();
		saveRunningEvent(event);

		slideProcessing = false;
		
		if (autoSlideShow) {
			processSlideFollowing(event.getDirection());
		} else {
			ProcessEventBus.get().post(new AfterFinishSlideEvent(event.getDirection()));
		}
	}

	@Subscribe
	public void processFinishTask(FinishTaskEvent event) {
		saveRunningEvent(event);

		//taskManager.find(event.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		ProcessEventBus.get().post(new NextTaskEvent());
	}

	@Subscribe
	public void processFinishTest(FinishTestEvent event) {
		saveRunningEvent(event);
		
		currentTest = null;
		testProcessing = false;
		
		currentUser = null;
	}

	@Subscribe
	public void processNextBranch(NextBranchEvent event) {
		saveRunningEvent(event);

		BranchMap branchMap = persistenceBranchManager.getBranchMap(currentPack, currentBranch);
		String key = branchManager.getNextBranchKey();

		Branch nextBranch = null;
		if (branchMap != null && key != null) {
			nextBranch = branchMap.get(key);
		}

		if (nextBranch != null) {
			currentBranch = persistenceManager.merge(branchManager.find(nextBranch));

			if (currentBranch != null) {
			
				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceManager.merge(taskManager.current());
				
				if (currentTask != null) {

					setSlideManagerParent(currentTask);
					currentSlide = slideManager.current();
			
					if (currentSlide != null) {
						slideProcessing = true;
						renderSlide();
					} else {
						ProcessEventBus.get().post(new FinishTaskEvent());
					}
				} else {
					ProcessEventBus.get().post(new FinishBranchEvent());
				}
			} else {
				ProcessEventBus.get().post(new FinishTestEvent());
			}
		} else {
			ProcessEventBus.get().post(new FinishTestEvent());
		}
	}

	@Subscribe
	public void processNextSlide(NextSlideEvent event) {
		saveRunningEvent(event);

		currentSlide = slideManager.next();
		
		if (currentSlide != null) {
			slideProcessing = true;
			renderSlide();
		} else {
			ProcessEventBus.get().post(new FinishTaskEvent());
		}
	}

	@Subscribe
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

	@Subscribe
	public void processNextTask(NextTaskEvent event) {
		saveRunningEvent(event);

		currentTask = persistenceManager.merge(taskManager.next());
		if (currentTask != null) {

			setSlideManagerParent(currentTask);
			currentSlide = slideManager.current();
			if (currentSlide != null) {
				slideProcessing = true;
				renderSlide();
			} else {
				ProcessEventBus.get().post(new FinishTaskEvent());
			}
		} else {
			ProcessEventBus.get().post(new FinishBranchEvent());
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
	public void processPrepareTest(PrepareTestEvent event) {
		log.debug(String.format("processPrepareTest: token uid = %s", event.getToken() != null ? event.getToken().getUid() : "NULL"));
		Token token = event.getToken();

		SimpleTest test = testManager.getUnattendedTest(token.getUser(), token.getPack(), token.isProduction());
		if (test != null) {
			currentUser = token.getUser();
			
			if (event.isStartAllowed()) {
				log.debug(String.format("Test start allowed (test id = %s).", test.getId() != null ? test.getId() : "NULL"));
				processTest(test);
			} else {
				ProcessEventBus.get().post(new AfterPrepareTestEvent(test));
			}
		} else {
			ProcessEventBus.get().post(new ErrorNotificationEvent(Messages.getString("Message.Error.StartTest")));
		}
	}

	@Subscribe
	public void processStartTest(StartTestEvent event) {
		saveRunningEvent(event);

		renderSlide();
	}

	public void processTest(SimpleTest test) {
		log.debug(String.format("processTest: %s", test != null ? test.getId() : "NULL"));
		if (!testProcessing) {
			testProcessing = true;
			
			currentTest = test;
			
			currentPack = persistenceManager.merge(test.getPack());
			
			branchManager.setListFromParent(currentPack);
			currentBranch = persistenceManager.merge(branchManager.current());
			
			if (currentBranch != null) {
				taskManager.setListFromParent(currentBranch);
				currentTask = persistenceManager.merge(taskManager.current());
				
				if (currentTask != null) {
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
						ProcessEventBus.get().post(new FinishTestEvent());
					}
				} else {
					log.debug("There is no task.");
					ProcessEventBus.get().post(new FinishTestEvent());
				}
			} else {
				log.debug("There is no branch.");
				ProcessEventBus.get().post(new FinishTestEvent());
			}
		} else {
			log.debug("Test is already processing.");
		}
	}
	
	public void processSlideFollowing(Direction direction) {
		if (!slideProcessing) {
			slideProcessing = true;
			
			ProcessEventBus.get().post((Direction.NEXT.equals(direction)) ? new NextSlideEvent() : new PriorSlideEvent());
		} else {
			log.warn("Slide not processing.");
		}
	}

	public void processToken(Token token, boolean startAllowed) {
		if (token != null) {

			if (checkUserPack(token.getUser(), token.getPack())) {
				ProcessEventBus.get().post(new PrepareTestEvent(token, startAllowed));
			} else {
				ProcessEventBus.get().post(new ErrorNotificationEvent(Messages.getString("Message.Error.InsufficientRights")));
			}
		} else {
			log.debug("Invalid token.");
			ProcessEventBus.get().post(new ErrorNotificationEvent(Messages.getString("Message.Error.Token")));
		}
	}

	private void breakCurrentTest() {
		ProcessEventBus.get().post(new BreakTestEvent());
	}

	private void renderSlide() {
		if (slideManager.getViewportComponent() != null) {

			ProcessEventBus.get().post(new RenderContentEvent(
					slideManager.getViewportComponent(), slideManager.getTimers(), slideManager.getShortcutKeys()));
		} else {
			fireTestError();
		}
	}

	private void saveBranchOutput() {
		String data = branchManager.getSerializedData();

		String output = branchManager.getNextBranchKey();

		BranchOutput branchOutput = new BranchOutput(currentTest, currentBranch);
		branchOutput.setXmlData(data);
		branchOutput.setOutput(output);

		outputManager.saveBranchOutput(branchOutput);
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
			
			testManager.saveEvent(event, currentTest);
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
			testManager.updateTest(test);
		}
	}

	public void updateTest(SimpleTest test, Date date, Status status) {
		test = persistenceManager.merge(test);

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
		ProcessEventBus.get().post(new ErrorTestEvent());
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
	
	public void purgeFactories() {
		SlideFactory.remove(slideManager);
		BranchFactory.remove(branchManager);
	}
	
	public void clean() {
		ProcessEventBus.get().unregister(this);
		currentUser = null;
	}
}
