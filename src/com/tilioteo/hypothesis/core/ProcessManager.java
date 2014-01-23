/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Date;
import java.util.Set;

import org.dom4j.Document;

import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.BranchMap;
import com.tilioteo.hypothesis.entity.BranchOutput;
import com.tilioteo.hypothesis.entity.Event;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.SlideOutput;
import com.tilioteo.hypothesis.entity.Test;
import com.tilioteo.hypothesis.entity.Test.Status;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.AbstractProcessEvent;
import com.tilioteo.hypothesis.event.AbstractRunningEvent;
import com.tilioteo.hypothesis.event.AbstractTestEvent;
import com.tilioteo.hypothesis.event.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.BreakTestEvent;
import com.tilioteo.hypothesis.event.CloseTestEvent;
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
import com.tilioteo.hypothesis.event.ProcessEventListener;
import com.tilioteo.hypothesis.event.ProcessEventManager;
import com.tilioteo.hypothesis.event.ProcessEventType;
import com.tilioteo.hypothesis.event.ProcessEventTypes;
import com.tilioteo.hypothesis.event.RenderContentEvent;
import com.tilioteo.hypothesis.event.StartTestEvent;
import com.tilioteo.hypothesis.persistence.OutputManager;
import com.tilioteo.hypothesis.persistence.PermitionManager;
import com.tilioteo.hypothesis.persistence.TestManager;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessManager implements ProcessEventListener {

	private UI ui;

	private Pack pack = null;

	private ProcessEventManager processEventManager;
	private BranchManager branchManager;
	private TaskManager taskManager;
	private Test test = null;

	private SlideManager slideManager;
	private TestManager testManager;
	private PermitionManager permitionManager;
	private OutputManager outputManager;

	private boolean canClose = true;

	public ProcessManager(UI ui) {
		this.ui = ui;

		processEventManager = ProcessEventManager.get(this.ui);
		branchManager = new BranchManager();
		taskManager = new TaskManager();

		processEventManager.addListener(this, PrepareTestEvent.class,
				AbstractRunningEvent.class, AfterRenderContentEvent.class,
				AbstractComponentEvent.class);

		slideManager = new SlideManager(processEventManager);
		permitionManager = PermitionManager.newInstance();
		testManager = permitionManager.getTestManager();

		outputManager = OutputManager.newInstance();
	}

	private Event createEvent(ProcessEvent event) {
		if (event != null && event.getName() != null) {
			ProcessEventType processEvent = ProcessEventTypes.get(event.getName());
			if (processEvent != null) {
				return new Event(processEvent.getId(), event.getName(),
						event.getTimestamp());
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
		} else if (event instanceof CloseTestEvent) {
			processCloseTest((CloseTestEvent) event);
		} else if (event instanceof AfterRenderContentEvent) {
			processAfterRender((AfterRenderContentEvent) event);
		} else if (event instanceof ErrorTestEvent) {
			processError((ErrorTestEvent) event);
		} else if (event instanceof AbstractComponentEvent<?>) {
			processComponentEvent((AbstractComponentEvent<?>) event);
		}
	}

	private boolean checkUserPack(User user, Pack pack) {
		Set<Pack> packs = permitionManager.findUserPacks(user, true);
		for (Pack pack2 : packs) {
			if (pack2.getId().equals(pack.getId()))
				return true;
		}
		return false;
	}

	private void processAfterRender(AfterRenderContentEvent eventObj) {
		slideManager.fireEvent(new SlideManager.ShowEvent(slideManager
				.current()));

		saveProcessEvent(eventObj);
	}

	private void processBreakTest(BreakTestEvent eventObj) {
		saveProcessEvent(eventObj);

		// TODO ?

		setCloseRequest();
	}

	private void processCloseTest(CloseTestEvent eventObj) {
		// closing application is not allowed before finish test event
		// processing is done
		// TODO !!! this is very dangerous code which can cause stack overflow
		// for event queue
		// make it better way

		if (this.canClose) {
			setCloseRequest();
		} else {
			// call again after while
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			processEventManager.fireEvent(new CloseTestEvent(eventObj.getTest()));
		}
	}

	private void processComponentEvent(AbstractComponentEvent<?> event) {
		saveComponentEvent(event);

	}

	private void processContinueTest(ContinueTestEvent eventObj) {
		saveProcessEvent(eventObj);

		test = eventObj.getTest();
		branchManager.find(test.getLastBranch());
		taskManager.find(test.getLastTask());
		slideManager.find(test.getLastSlide());

		renderSlide();
	}

	private void processError(ErrorTestEvent eventObj) {
		saveProcessEvent(eventObj);

		// TODO ?
		processEventManager.fireEvent(new ErrorNotificationEvent(
				"Something went wrong"));
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

		processEventManager
				.fireEvent((Direction.NEXT.equals(eventObj.getDirection())) ? new NextSlideEvent(
						slideManager.current()) : new PriorSlideEvent(
						slideManager.current()));
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
		// canClose has to be false

		saveProcessEvent(eventObj);

		// TODO ?

		// enable closing application
		this.canClose = true;
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
				processEventManager.fireEvent(new FinishTestEvent(test));
			}
		} else {
			processEventManager.fireEvent(new ErrorTestEvent(test));
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
			slideManager.setListParent(taskManager.current());
			renderSlide();
		} else {
			processEventManager.fireEvent(new FinishBranchEvent(branchManager
					.current()));
		}
	}

	private void processPrepareTest(PrepareTestEvent eventObj) {
		Test test = testManager.getUnattendTest(eventObj.getUser(),
				eventObj.getPack(), eventObj.isProduction());
		if (test != null) {
			processTest(test);
		} else {
			// TODO set localizable resource
			processEventManager.fireEvent(new ErrorNotificationEvent(
					"An error occured when starting test."));
		}
	}

	private void processStartTest(StartTestEvent eventObj) {
		saveProcessEvent(eventObj);

		renderSlide();
	}

	private void processTest(Test test) {
		// up there the collector application can be correctly closed after test
		// finish
		canClose = false;

		pack = test.getPack();
		branchManager.setListParent(pack);
		taskManager.setListParent(branchManager.current());
		slideManager.setListParent(taskManager.current());
		if (test.getStatus().equals(Status.CREATED)) {
			processEventManager.fireEvent(new StartTestEvent(test));
		} else {
			processEventManager.fireEvent(new ContinueTestEvent(test));
		}
	}

	public void processWithPack(Pack pack, User user, boolean production) {
		if (checkUserPack(user, pack)) {
			processEventManager
					.fireEvent(new PrepareTestEvent(pack, user, production));
		} else {
			// TODO set localizable resource
			processEventManager.fireEvent(new ErrorNotificationEvent(
					"You do not have permition to process."));
		}
	}

	public void breakCurrentTest() {
		processEventManager.fireEvent(new BreakTestEvent(test));
	}

	private void renderSlide() {
		if (slideManager.getViewport() != null
				&& slideManager.getViewport().getComponent() != null) {
			processEventManager.fireEvent(new RenderContentEvent(slideManager
					.getViewport()));
		} else {
			processEventManager.fireEvent(new ErrorTestEvent(test));
		}
	}

	private void saveBranchOutput() {
		Branch branch = branchManager.current();
		String data = branchManager.getSerializedData();

		branchManager.updateNextBranchKey();
		String output = branchManager.getNextBranchKey();

		BranchOutput branchOutput = new BranchOutput(test, branch);
		branchOutput.setData(data);
		branchOutput.setOutput(output);

		outputManager.addBranchOutput(branchOutput);
	}

	private void saveComponentEvent(AbstractComponentEvent<?> componentEvent) {
		if (test != null) {
			Event event = createEvent(componentEvent);
			if (event != null) {
				updateEvent(event);
				updateEventData(event, componentEvent);
				test.addEvent(event);
				updateTest(test, componentEvent.getTimestamp());

			}
		}
	}

	private void saveProcessEvent(AbstractProcessEvent processEvent) {
		if (processEvent instanceof AbstractTestEvent)
			test = ((AbstractTestEvent) processEvent).getTest();

		if (test != null) {
			Event event = createEvent(processEvent);
			if (event != null) {
				updateEvent(event);
				test.addEvent(event);

				Status status = processEvent instanceof HasStatus ? ((HasStatus) processEvent)
						.getStatus() : null;

				if (status != null)
					updateTest(test, processEvent.getTimestamp(), status);
				else
					updateTest(test, processEvent.getTimestamp());
			}
		}
	}

	private void saveSlideOutput() {
		Slide slide = slideManager.current();
		String data = slideManager.getSerializedData();
		String output = slideManager.getSerializedOutputValue();

		SlideOutput slideOutput = new SlideOutput(test, slide);
		slideOutput.setData(data);
		slideOutput.setOutput(output);

		outputManager.addSlideOutput(slideOutput);
	}

	private void setCloseRequest() {
		/*if (application instanceof CollectorApplication) {
			((CollectorApplication) application).shutdown();
		}*/
	}

	public void updateEvent(Event event) {
		if (event != null) {
			event.setBranch(branchManager.current());
			event.setTask(taskManager.current());
			event.setSlide(slideManager.current());
		}
	}

	private void updateEventData(Event event,
			AbstractComponentEvent<?> componentEvent) {
		Document doc = SlideXmlFactory.createEventDataXml();
		SlideFactory.writeComponentData(doc, componentEvent);
		event.setData(XmlUtility.writeString(doc));
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

}
