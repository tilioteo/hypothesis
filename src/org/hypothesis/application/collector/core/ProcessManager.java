/**
 * 
 */
package org.hypothesis.application.collector.core;

import java.util.Date;
import java.util.Set;

import org.dom4j.Document;
import org.hypothesis.application.CollectorApplication;
import org.hypothesis.application.collector.BranchMap;
import org.hypothesis.application.collector.events.AbstractComponentEvent;
import org.hypothesis.application.collector.events.AbstractProcessEvent;
import org.hypothesis.application.collector.events.AbstractRunningEvent;
import org.hypothesis.application.collector.events.AbstractTestEvent;
import org.hypothesis.application.collector.events.AfterRenderContentEvent;
import org.hypothesis.application.collector.events.BreakTestEvent;
import org.hypothesis.application.collector.events.CloseTestEvent;
import org.hypothesis.application.collector.events.ContinueTestEvent;
import org.hypothesis.application.collector.events.ErrorNotificationEvent;
import org.hypothesis.application.collector.events.ErrorTestEvent;
import org.hypothesis.application.collector.events.FinishBranchEvent;
import org.hypothesis.application.collector.events.FinishSlideEvent;
import org.hypothesis.application.collector.events.FinishTaskEvent;
import org.hypothesis.application.collector.events.FinishTestEvent;
import org.hypothesis.application.collector.events.HasStatus;
import org.hypothesis.application.collector.events.NextBranchEvent;
import org.hypothesis.application.collector.events.NextSlideEvent;
import org.hypothesis.application.collector.events.NextTaskEvent;
import org.hypothesis.application.collector.events.PrepareTestEvent;
import org.hypothesis.application.collector.events.ProcessEvent;
import org.hypothesis.application.collector.events.ProcessEventListener;
import org.hypothesis.application.collector.events.ProcessEventManager;
import org.hypothesis.application.collector.events.ProcessEvents;
import org.hypothesis.application.collector.events.RenderContentEvent;
import org.hypothesis.application.collector.events.StartTestEvent;
import org.hypothesis.application.collector.xml.SlideXmlFactory;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.BranchOutput;
import org.hypothesis.entity.Event;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.SlideOutput;
import org.hypothesis.entity.Test;
import org.hypothesis.entity.User;
import org.hypothesis.entity.Test.Status;
import org.hypothesis.persistence.OutputManager;
import org.hypothesis.persistence.PermitionManager;
import org.hypothesis.persistence.TestManager;
import org.hypothesis.persistence.hibernate.BranchOutputDao;
import org.hypothesis.persistence.hibernate.GroupPermitionDao;
import org.hypothesis.persistence.hibernate.SlideOutputDao;
import org.hypothesis.persistence.hibernate.TestDao;
import org.hypothesis.persistence.hibernate.UserPermitionDao;

import com.vaadin.Application;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessManager implements ProcessEventListener {

	private Application application;

	private Pack pack = null;

	private ProcessEventManager eventManager;
	private BranchManager branchManager;
	private TaskManager taskManager;
	private Test test = null;

	private SlideManager slideManager;
	private TestManager testManager;
	private PermitionManager permitionManager;
	private OutputManager outputManager;

	private boolean canClose = true;

	public ProcessManager(Application application) {
		this.application = application;

		eventManager = ProcessEventManager.get(this.application);
		branchManager = new BranchManager();
		taskManager = new TaskManager();

		eventManager.addListener(this, PrepareTestEvent.class,
				AbstractRunningEvent.class, AfterRenderContentEvent.class,
				AbstractComponentEvent.class);

		slideManager = new SlideManager(eventManager);
		testManager = new TestManager(new TestDao());
		permitionManager = new PermitionManager(new UserPermitionDao(),
				new GroupPermitionDao(), testManager);
		outputManager = new OutputManager(new SlideOutputDao(),
				new BranchOutputDao());
	}

	private Event createEvent(AbstractProcessEvent event) {
		if (event != null && event.getName() != null) {
			ProcessEvent processEvent = ProcessEvents.get(event.getName());
			if (processEvent != null) {
				return new Event(processEvent.getId(), event.getName(),
						event.getDatetime());
			}
		}
		return null;
	}

	public void handleEvent(AbstractProcessEvent event) {
		if (event instanceof PrepareTestEvent) {
			processPrepareTest((PrepareTestEvent) event);
		} else if (event instanceof StartTestEvent) {
			processStartTest((StartTestEvent) event);
		} else if (event instanceof ContinueTestEvent) {
			processContinueTest((ContinueTestEvent) event);
		} else if (event instanceof NextSlideEvent) {
			processNextSlide((NextSlideEvent) event);
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
			eventManager.fireEvent(new CloseTestEvent(eventObj.getTest()));
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
		eventManager.fireEvent(new ErrorNotificationEvent(
				"Something went wrong"));
	}

	private void processFinishBranch(FinishBranchEvent eventObj) {
		saveProcessEvent(eventObj);

		branchManager.find(eventObj.getBranch());

		// TODO process branch result

		saveBranchOutput();

		eventManager.fireEvent(new NextBranchEvent(branchManager.current()));
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

		eventManager.fireEvent(new NextSlideEvent(slideManager.current()));
	}

	private void processFinishTask(FinishTaskEvent eventObj) {
		saveProcessEvent(eventObj);

		taskManager.find(eventObj.getTask());

		// Object taskOutputValue = taskManager.getOutputValue();
		// branchManager.addTaskOutputValue(taskManager.current(),
		// taskOutputValue);

		eventManager.fireEvent(new NextTaskEvent(taskManager.current()));
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
				taskManager.setQueueOwner(branchManager.current());
				slideManager.setQueueOwner(taskManager.current());
				renderSlide();
			} else {
				eventManager.fireEvent(new FinishTestEvent(test));
			}
		} else {
			eventManager.fireEvent(new ErrorTestEvent(test));
		}
	}

	private void processNextSlide(NextSlideEvent eventObj) {
		saveProcessEvent(eventObj);

		slideManager.find(eventObj.getSlide());

		if (slideManager.next() != null) {
			renderSlide();
		} else {
			eventManager.fireEvent(new NextTaskEvent(taskManager.current()));
		}
	}

	private void processNextTask(NextTaskEvent eventObj) {
		saveProcessEvent(eventObj);

		taskManager.find(eventObj.getTask());

		if (taskManager.next() != null) {
			slideManager.setQueueOwner(taskManager.current());
			renderSlide();
		} else {
			eventManager
					.fireEvent(new NextBranchEvent(branchManager.current()));
		}
	}

	private void processPrepareTest(PrepareTestEvent eventObj) {
		Test test = testManager.getUnattendTest(eventObj.getUser(),
				eventObj.getPack(), eventObj.isProduction());
		if (test != null) {
			processTest(test);
		} else {
			// TODO set localizable resource
			eventManager.fireEvent(new ErrorNotificationEvent(
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
		branchManager.setQueueOwner(pack);
		taskManager.setQueueOwner(branchManager.current());
		slideManager.setQueueOwner(taskManager.current());
		if (test.getStatus().equals(Status.CREATED)) {
			eventManager.fireEvent(new StartTestEvent(test));
		} else {
			eventManager.fireEvent(new ContinueTestEvent(test));
		}
	}

	public void processWithPack(Pack pack, User user, boolean production) {
		if (checkUserPack(user, pack)) {
			eventManager
					.fireEvent(new PrepareTestEvent(pack, user, production));
		} else {
			// TODO set localizable resource
			eventManager.fireEvent(new ErrorNotificationEvent(
					"You do not have permition to process."));
		}
	}

	private void renderSlide() {
		if (slideManager.getViewport() != null
				&& slideManager.getViewport().getComponent() != null) {
			eventManager.fireEvent(new RenderContentEvent(slideManager
					.getViewport()));
		} else {
			eventManager.fireEvent(new ErrorTestEvent(test));
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
				updateTest(test, componentEvent.getDatetime());

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
					updateTest(test, processEvent.getDatetime(), status);
				else
					updateTest(test, processEvent.getDatetime());
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
		if (application instanceof CollectorApplication) {
			((CollectorApplication) application).shutdown();
		}
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
		event.setData(Utility.writeString(doc));
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
