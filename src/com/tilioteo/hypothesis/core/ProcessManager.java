/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;

import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchMap;
import com.tilioteo.hypothesis.entity.BranchOutput;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.SlideOrder;
import com.tilioteo.hypothesis.entity.SlideOutput;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.entity.Test;
import com.tilioteo.hypothesis.entity.Test.Status;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.AbstractNotificationEvent;
import com.tilioteo.hypothesis.event.AbstractProcessEvent;
import com.tilioteo.hypothesis.event.AbstractRunningEvent;
import com.tilioteo.hypothesis.event.AbstractTestEvent;
import com.tilioteo.hypothesis.event.ActionEvent;
import com.tilioteo.hypothesis.event.AfterFinishSlideEvent;
import com.tilioteo.hypothesis.event.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.BreakTestEvent;
import com.tilioteo.hypothesis.event.ContinueTestEvent;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.ErrorTestEvent;
import com.tilioteo.hypothesis.event.FinishBranchEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.AfterPrepareTestEvent;
import com.tilioteo.hypothesis.event.FinishTaskEvent;
import com.tilioteo.hypothesis.event.FinishTestEvent;
import com.tilioteo.hypothesis.event.NextBranchEvent;
import com.tilioteo.hypothesis.event.NextSlideEvent;
import com.tilioteo.hypothesis.event.NextTaskEvent;
import com.tilioteo.hypothesis.event.PrepareTestEvent;
import com.tilioteo.hypothesis.event.PriorSlideEvent;
import com.tilioteo.hypothesis.event.ProcessEvent;
import com.tilioteo.hypothesis.event.ProcessEventListener;
import com.tilioteo.hypothesis.event.ProcessEventManager;
import com.tilioteo.hypothesis.event.ProcessEventType;
import com.tilioteo.hypothesis.event.ProcessEventTypes;
import com.tilioteo.hypothesis.event.RenderContentEvent;
import com.tilioteo.hypothesis.event.StartTestEvent;
import com.tilioteo.hypothesis.persistence.OutputManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.TestManager;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessManager implements ProcessEventListener {

	private Pack pack = null;

	private ProcessEventManager processEventManager;
	private BranchManager branchManager;
	private TaskManager taskManager;
	private Test test = null;

	private SlideManager slideManager;
	private TestManager testManager;
	private PermissionManager permissionManager;
	private OutputManager outputManager;
	
	private boolean testProcessing = false;
	private boolean slideProcessing = false;
	private boolean autoSlideShow = true;

	public ProcessManager(ProcessEventListener listener) {
		processEventManager = new ProcessEventManager();
		branchManager = new BranchManager();
		taskManager = new TaskManager();

		processEventManager.addListener(this,
				PrepareTestEvent.class,
				AbstractRunningEvent.class,
				AfterRenderContentEvent.class,
				AbstractComponentEvent.class);
		if (listener != null) {
			processEventManager.addListener(listener,
					AfterPrepareTestEvent.class,
					RenderContentEvent.class,
					AbstractRunningEvent.class,
					AbstractNotificationEvent.class);
		}

		slideManager = new SlideManager(processEventManager);
		permissionManager = PermissionManager.newInstance();
		testManager = permissionManager.getTestManager();

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

	public void handleEvent(ProcessEvent event) {
		if (event instanceof PrepareTestEvent) {
			processPrepareTest((PrepareTestEvent) event);
		} else if (event instanceof StartTestEvent) {
			processStartTest((StartTestEvent) event);
		} else if (event instanceof ContinueTestEvent) {
			processContinueTest((ContinueTestEvent) event);
		} else if (event instanceof NextSlideEvent) {
			processNextSlide((NextSlideEvent) event);
		} else if (event instanceof PriorSlideEvent) {
			processPriorSlide((PriorSlideEvent) event);
		} else if (event instanceof FinishSlideEvent) {
			processFinishSlide((FinishSlideEvent) event);
		} else if (event instanceof NextTaskEvent) {
			processNextTask((NextTaskEvent) event);
		} else if (event instanceof FinishTaskEvent) {
			processFinishTask((FinishTaskEvent) event);
		} else if (event instanceof NextBranchEvent) {
			processNextBranch((NextBranchEvent) event);
		} else if (event instanceof FinishBranchEvent) {
			processFinishBranch((FinishBranchEvent) event);
		} else if (event instanceof BreakTestEvent) {
			processBreakTest((BreakTestEvent) event);
		} else if (event instanceof FinishTestEvent) {
			processFinishTest((FinishTestEvent) event);
		} else if (event instanceof AfterRenderContentEvent) {
			processAfterRender((AfterRenderContentEvent) event);
		} else if (event instanceof ErrorTestEvent) {
			processError((ErrorTestEvent) event);
		} else if (event instanceof AbstractComponentEvent<?>) {
			processComponentEvent((AbstractComponentEvent<?>) event);
		}else if (event instanceof ActionEvent) {
			processActionEvent((ActionEvent) event);
		}
	}

	private boolean checkUserPack(User user, Pack pack) {
		Collection<Pack> packs;
		if (null != user) {
			packs = permissionManager.findUserPacks(user, true);
		} else {
			packs = permissionManager.getPublishedPacks();
		}
		for (Pack pack2 : packs) {
			if (pack2.getId().equals(pack.getId()))
				return true;
		}
		return false;
	}

	private void processActionEvent(ActionEvent event) {
		saveActionEvent(event);
		
	}

	private void processAfterRender(AfterRenderContentEvent eventObj) {
		slideManager.fireEvent(new SlideManager.ShowEvent(slideManager.current()));

		saveProcessEvent(eventObj);
	}

	private void processBreakTest(BreakTestEvent eventObj) {
		saveProcessEvent(eventObj);

		this.test = null;
		testProcessing = false;
	}

	private void processComponentEvent(AbstractComponentEvent<?> event) {
		saveComponentEvent(event);

	}

	private void processContinueTest(ContinueTestEvent eventObj) {
		saveProcessEvent(eventObj);

		this.test = eventObj.getTest();
		branchManager.find(this.test.getLastBranch());
		taskManager.find(this.test.getLastTask());
		slideManager.find(this.test.getLastSlide());

		renderSlide();
	}

	private void processError(ErrorTestEvent eventObj) {
		if (Test.DUMMY_TEST != eventObj.getTest()) {
			saveProcessEvent(eventObj);
		}

		// TODO add some error description
		processEventManager.fireEvent(new ErrorNotificationEvent(
				eventObj.getTest(), "Something went wrong"));
	}

	private void processFinishBranch(FinishBranchEvent eventObj) {
		saveProcessEvent(eventObj);

		branchManager.find(eventObj.getBranch());

		// TODO process branch result

		saveBranchOutput();

		processEventManager.fireEvent(new NextBranchEvent(branchManager.current()));
	}

	private void processFinishSlide(FinishSlideEvent eventObj) {
		saveProcessEvent(eventObj);

		slideManager.find(eventObj.getSlide());

		Object slideOutputValue = slideManager.getOutputValue();
		taskManager.addSlideOutputValue(slideManager.current(),
				slideOutputValue);
		branchManager.addSlideOutputValue(slideManager.current(),
				slideOutputValue);

		saveSlideOutput();
		
		slideProcessing = false;
		
		if (autoSlideShow) {
			processSlideFollowing(slideManager.current(), eventObj.getDirection());
		} else {
			processEventManager.fireEvent(new AfterFinishSlideEvent(slideManager.current(), eventObj.getDirection()));
		}
	}

	private void processFinishTask(FinishTaskEvent eventObj) {
		saveProcessEvent(eventObj);

		taskManager.find(eventObj.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		processEventManager.fireEvent(new NextTaskEvent(taskManager.current()));
	}

	private void processFinishTest(FinishTestEvent eventObj) {
		saveProcessEvent(eventObj);
		
		this.test = null;
		testProcessing = false;
	}

	private void processNextBranch(NextBranchEvent eventObj) {
		saveProcessEvent(eventObj);

		branchManager.find(eventObj.getBranch());

		Branch current = branchManager.current();
		if (current != null) {
			BranchMap branchMap = current.getBranchMap();
			String key = branchManager.getNextBranchKey();

			Branch nextBranch = null;
			if (key != null)
				nextBranch = branchMap.get(key);

			if (nextBranch != null) {
				branchManager.setCurrent(nextBranch);
				taskManager.setListParent(branchManager.current());
				slideManager.setListParent(taskManager.current());
				renderSlide();
			} else {
				processEventManager.fireEvent(new FinishTestEvent(this.test));
			}
		} else {
			processEventManager.fireEvent(new ErrorTestEvent(this.test));
		}
	}

	private void processNextSlide(NextSlideEvent eventObj) {
		saveProcessEvent(eventObj);

		slideManager.find(eventObj.getSlide());

		if (slideManager.next() != null) {
			renderSlide();
		} else {
			processEventManager.fireEvent(new FinishTaskEvent(taskManager.current()));
		}
	}

	private void processPriorSlide(PriorSlideEvent eventObj) {
		saveProcessEvent(eventObj);

		slideManager.find(eventObj.getSlide());

		// TODO get prior slide
		/*
		 * if (slideManager.next() != null) { renderSlide(); } else {
		 * processEventManager.fireEvent(new NextTaskEvent(taskManager.current())); }
		 */
	}

	private void processNextTask(NextTaskEvent eventObj) {
		saveProcessEvent(eventObj);

		taskManager.find(eventObj.getTask());

		if (taskManager.next() != null) {
			setSlideManagerParent(taskManager.current());
			renderSlide();
		} else {
			processEventManager.fireEvent(new FinishBranchEvent(branchManager.current()));
		}
	}

	private void setSlideManagerParent(Task task) {
		slideManager.setListParent(task);
		if (task != null && task.isRandomized()) {
			setTaskSlidesRandomOrder(test, task);
		}
	}

	private void setTaskSlidesRandomOrder(Test test, Task task) {
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

	private void processPrepareTest(PrepareTestEvent eventObj) {
		Token token = eventObj.getToken();
		Test test = testManager.getUnattendedTest(token.getUser(),
				token.getPack(), token.isProduction());
		if (test != null) {
			if (eventObj.isStartAllowed()) {
				processTest(test);
			} else {
				processEventManager.fireEvent(new AfterPrepareTestEvent(test));
			}
		} else {
			// TODO set localizable resource
			processEventManager.fireEvent(new ErrorNotificationEvent(
					Test.DUMMY_TEST, "An error occured when starting test."));
		}
	}

	private void processStartTest(StartTestEvent eventObj) {
		saveProcessEvent(eventObj);

		this.test = eventObj.getTest();

		renderSlide();
	}

	public void processTest(Test test) {
		if (!testProcessing) {
			testProcessing = true;
			this.test = test;
			
			pack = test.getPack();
			branchManager.setListParent(pack);
			taskManager.setListParent(branchManager.current());
			setSlideManagerParent(taskManager.current());
			
			slideProcessing = true;
			
			if (test.getStatus().equals(Status.CREATED)) {
				processEventManager.fireEvent(new StartTestEvent(test));
			} else {
				processEventManager.fireEvent(new ContinueTestEvent(test));
			}
		}
	}
	
	public void processSlideFollowing(Slide slide, Direction direction) {
		if (!slideProcessing) {
			slideProcessing = true;
			
			processEventManager.fireEvent((Direction.NEXT.equals(direction)) ? new NextSlideEvent(slide) : new PriorSlideEvent(slide));
		}
	}

	public void processToken(Token token, boolean startAllowed) {
		if (token != null) {

			if (checkUserPack(token.getUser(), token.getPack())) {
				processEventManager.fireEvent(new PrepareTestEvent(token, startAllowed));
			} else {
				// TODO set localizable resource
				processEventManager.fireEvent(new ErrorNotificationEvent(
						Test.DUMMY_TEST, "You do not have permition to process."));
			}
		} else {
			// TODO set localizable resource
			processEventManager.fireEvent(new ErrorNotificationEvent(
					Test.DUMMY_TEST, "Invalid token."));
		}
	}

	private void breakCurrentTest() {
		processEventManager.fireEvent(new BreakTestEvent(this.test));
	}

	private void renderSlide() {
		if (slideManager.getViewport() != null
				&& slideManager.getViewport().getComponent() != null) {
			processEventManager.fireEvent(new RenderContentEvent(slideManager
					.getViewport(), slideManager.getTimers()));
		} else {
			fireTestError();
		}
	}

	private void saveBranchOutput() {
		Branch branch = branchManager.current();
		String data = branchManager.getSerializedData();

		branchManager.updateNextBranchKey();
		String output = branchManager.getNextBranchKey();

		BranchOutput branchOutput = new BranchOutput(this.test, branch);
		branchOutput.setData(data);
		branchOutput.setOutput(output);

		outputManager.addBranchOutput(branchOutput);
	}

	private void saveActionEvent(ActionEvent actionEvent) {
		if (this.test != null) {
			Event event = createEvent(actionEvent);
			if (event != null) {
				updateEvent(event);
				updateEventData(event, actionEvent);
				this.test.addEvent(event);
				updateTest(this.test, actionEvent.getTimestamp());

			}
		}
	}

	private void saveComponentEvent(AbstractComponentEvent<?> componentEvent) {
		if (this.test != null) {
			Event event = createEvent(componentEvent);
			if (event != null) {
				updateEvent(event);
				updateEventData(event, componentEvent);
				this.test.addEvent(event);
				updateTest(this.test, componentEvent.getTimestamp());

			}
		}
	}

	private void saveProcessEvent(AbstractProcessEvent processEvent) {
		if (processEvent instanceof AbstractTestEvent)
			this.test = ((AbstractTestEvent) processEvent).getTest();

		if (this.test != null) {
			Event event = createEvent(processEvent);
			if (event != null) {
				updateEvent(event);
				this.test.addEvent(event);

				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent)
						.getStatus() : null;

				if (status != null)
					updateTest(this.test, processEvent.getTimestamp(), status);
				else
					updateTest(this.test, processEvent.getTimestamp());
			}
		}
	}

	private void saveSlideOutput() {
		Slide slide = slideManager.current();
		String data = slideManager.getSerializedData();
		String output = slideManager.getSerializedOutputValue();

		SlideOutput slideOutput = new SlideOutput(this.test, slide);
		slideOutput.setData(data);
		slideOutput.setOutput(output);

		outputManager.addSlideOutput(slideOutput);
	}

	public void updateEvent(Event event) {
		if (event != null) {
			event.setBranch(branchManager.current());
			event.setTask(taskManager.current());
			event.setSlide(slideManager.current());
		}
	}

	private void updateEventData(Event event, ActionEvent actionEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeActionData(doc, actionEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}

	private void updateEventData(Event event, AbstractComponentEvent<?> componentEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeComponentData(doc.getRootElement(), componentEvent);
		event.setXmlData(XmlUtility.writeString(doc));
	}

	private void updateTest(Test test, Date date) {
		if (test != null) {
			test.setLastAccess(date);
			test.setLastBranch(branchManager.current());
			test.setLastTask(taskManager.current());
			test.setLastSlide(slideManager.current());
			testManager.updateTest(test);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void updateTest(Test test, Date date, Status status) {
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
			}
			updateTest(test, date);
		}
	}
	
	public ProcessEventManager getProcessEventManager() {
		return processEventManager;
	}
	
	public void fireTestError() {
		if (this.test != null) {
			processEventManager.fireEvent(new ErrorTestEvent(this.test));
		} else {
			processEventManager.fireEvent(new ErrorTestEvent(Test.DUMMY_TEST));
		}
	}
	
	public void requestBreakTest() {
		// test is processing
		if (this.test != null) {
			breakCurrentTest();
		}
	}

	public void setAutoSlideShow(boolean value) {
		if (!testProcessing) {
			this.autoSlideShow = value;
		}
	}
}
