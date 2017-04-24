/**
 * 
 */
package org.hypothesis.application.junit;

import com.vaadin.Application;
import com.vaadin.ui.Button.ClickEvent;
import org.hypothesis.application.collector.BranchMap;
import org.hypothesis.application.collector.core.BranchManager;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.TaskManager;
import org.hypothesis.application.collector.events.*;
import org.hypothesis.application.collector.events.FinishSlideEvent.Direction;
import org.hypothesis.application.collector.ui.MainWindow;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Test;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TestApplication extends Application implements ProcessEventListener  {

	private ProcessEventManager eventManager; //= new ProcessEventManager();
	private BranchManager branchManager; //= new BranchManager();
	private TaskManager taskManager; //= new TaskManager();
	private SlideManager slideManager;
	
	private Pack pack;
	private Test test; //= new Test(null, null);
	
	private MainWindow mainWindow; //= new MainWindow(null);
	
	@Override
	public void init() {
		eventManager = ProcessEventManager.get(this);
		branchManager = new BranchManager();
		taskManager = new TaskManager();
		slideManager = new SlideManager(eventManager);
		
		pack = new Pack();
		test = new Test(null, null);
		
		mainWindow = new MainWindow(null);
		setMainWindow(mainWindow);

		eventManager.addListener(mainWindow,
				RenderContentEvent.class,
				AbstractNotificationEvent.class);
		
		eventManager.addListener(this,
				PrepareTestEvent.class,
				AbstractRunningEvent.class,
				AbstractContentEvent.class,
				AbstractNotificationEvent.class,
				AbstractComponentEvent.class);


		// process test
		eventManager.fireEvent(new PrepareTestEvent(pack, null, false));
		//ProcessEventManager.fireEvent(new StartTestEvent(test));
		
		
		//setCurrentUser((User) ApplicationSecurity.getSessionObject(getSession(), "user"));

		/*
		VerticalLayout layout = new VerticalLayout();
		//GridLayout layout = new GridLayout(1,3);
		//layout.setHeight("100%");
		//layout.setWidth("100%");
		layout.setSizeFull();
		layout.setStyleName(Reindeer.LAYOUT_BLUE);
		layout.setSpacing(true);
		
		Panel panel1 = new Panel();
		
		panel1.setHeight("100%");
		panel1.setWidth("70%");
		GridLayout grid = new GridLayout(1, 1);
		//VerticalLayout vl = new VerticalLayout();
		grid.setSizeFull();
		//vl.setSizeFull();
		panel1.setContent(grid);
		//panel1.setContent(vl);
		Label label1 = new Label("Toto je nějaký vtipný text");
		label1.setSizeUndefined();
		grid.addComponent(label1);
		//vl.addComponent(label1);
		grid.setComponentAlignment(label1, Alignment.MIDDLE_CENTER);
		//vl.setComponentAlignment(label1, Alignment.MIDDLE_CENTER);
		
		Label label2 = new Label("A toto je ještě vtipnější");
		label2.setSizeUndefined();
		grid.addComponent(label2);
		//vl.addComponent(label2);
		grid.setComponentAlignment(label2, Alignment.MIDDLE_RIGHT);
		//vl.setComponentAlignment(label2, Alignment.MIDDLE_RIGHT);
		
		Panel panel2 = new Panel();
		panel2.setStyleName(Reindeer.PANEL_LIGHT);
		panel2.setHeight("100%");
		
		Panel panel3 = new Panel();
		panel3.setHeight("100%");
		
		//getMainWindow().setContent(layout);
		layout.addComponent(panel1);
		layout.setComponentAlignment(panel1, Alignment.MIDDLE_CENTER);
		//layout.addComponent(panel1, 0, 0);
		layout.addComponent(panel2);
		//layout.addComponent(panel2, 0, 1);
		layout.addComponent(panel3);
		//layout.addComponent(panel3, 0, 2);
		
		layout.setExpandRatio(panel1, 0.3f);
		layout.setExpandRatio(panel2, 0.5f);
		layout.setExpandRatio(panel3, 0.2f);
		
		Image image = new Image();
		image.setSource(new ExternalResource("http://morong.cz/tmp/dily/dily002.jpg"));
		
		image.addListener(new LoadListener() {
			
			public void loaded(LoadEvent event) {
				mainWindow.showNotification("loaded");
			}
		});
		panel2.addComponent(image);
		
		ButtonPanel buttonPanel = new ButtonPanel(new String[] {"asd", "fgh", "jkl", "zxc"});
		//NativeButton button = new NativeButton("Don't press!");
		//button.setHeight("50px");
		//button.setWidth("200px");
		buttonPanel.setButtonClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				mainWindow.showNotification("clicked " + event.getButton().getData());
			}
		});
		//panel3.addComponent(buttonPanel);
		AbstractOrderedLayout layout2 = (AbstractOrderedLayout)panel3.getContent();
		//layout2.setComponentAlignment(buttonPanel, Alignment.MIDDLE_CENTER);
		
		TimerLabel timerLabel = new TimerLabel(5, Direction.Down);
		panel3.addComponent(timerLabel);
		layout2.setComponentAlignment(timerLabel, Alignment.MIDDLE_CENTER);
		//counterLabel.setWidth("200px");
		//counterLabel.setHeight("50px");
		timerLabel.setSizeUndefined();
		
		timerLabel.addListener(new TimerLabel.FinishListener() {
			public void finished(FinishEvent event) {
				mainWindow.showNotification("finished (" + event.getStopped() + ")");
			}
		});
		//counterLabel.setVisible(false);
		timerLabel.start();
		//OptionGroup group = new OptionGroup();
		
		//getMainWindow().addListener(this);
		Component content = layout;
		*/
		
		// SlideFactoryTest
		/*SlideFactoryTest slideFactoryTest = new SlideFactoryTest();
		Component content = slideFactoryTest.testCreateSlideControls();
		
		
		if (content != null) {
			if (content instanceof ComponentContainer) {
				mainWindow.setContent((ComponentContainer)content);
			} else {
				mainWindow.addComponent(content);
			}
		}
		
		setMainWindow(mainWindow);*/
	}
	
	private void renderSlide() {
		if (slideManager.getViewport() != null &&
				slideManager.getViewport().getComponent() != null) {
			eventManager.fireEvent(new RenderContentEvent(slideManager.getViewport()));
		} else {
			eventManager.fireEvent(new ErrorTestEvent(test));
		}
	}

	public void handleEvent(AbstractProcessEvent event) {
		if (event instanceof PrepareTestEvent) {
			
			branchManager.setCurrent(BranchFactoryTest.testCreateBranchTree());
			taskManager.setQueueOwner(branchManager.current());
			slideManager.setQueueOwner(taskManager.current());

			eventManager.fireEvent(new StartTestEvent(test));
			
		} else if (event instanceof StartTestEvent) {
			// process start test
			slideManager.current();
			renderSlide();
			
		} else if (event instanceof ContinueTestEvent) {

		} else if (event instanceof NextSlideEvent) {
			// process next slide
			slideManager.find(((NextSlideEvent)event).getSlide());
			
			if (slideManager.next() != null) {
				renderSlide();
			} else {
				eventManager.fireEvent(new FinishTaskEvent(taskManager.current()));
			}

		} else if (event instanceof FinishSlideEvent) {
			//mainWindow.setContent(null);
			// process finish slide
			slideManager.find(((FinishSlideEvent)event).getSlide());
			
			Object slideOutputValue = slideManager.getOutputValue();
			taskManager.addSlideOutputValue(slideManager.current(), slideOutputValue);
			branchManager.addSlideOutputValue(slideManager.current(), slideOutputValue);
			
			eventManager.fireEvent((Direction.NEXT.equals(((FinishSlideEvent)event).getDirection())) ? new NextSlideEvent(
					slideManager.current()) : new PriorSlideEvent(slideManager.current()));
			
		} else if (event instanceof NextTaskEvent) {
			// process next task
			taskManager.find(((NextTaskEvent)event).getTask());
			
			if (taskManager.next() != null) {
				slideManager.setQueueOwner(taskManager.current());
				renderSlide();
			} else {
				eventManager.fireEvent(new FinishBranchEvent(branchManager.current()));
			}
			
		} else if (event instanceof FinishTaskEvent) {
			// process finish task
			taskManager.find(((FinishTaskEvent)event).getTask());
			
			//Object taskOutputValue = taskManager.getOutputValue();
			//branchManager.addTaskOutputValue(taskManager.current(), taskOutputValue);
			
			eventManager.fireEvent(new NextTaskEvent(taskManager.current()));
			
		} else if (event instanceof NextBranchEvent) {
			// process next branch
			branchManager.find(((NextBranchEvent)event).getBranch());
			
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

		} else if (event instanceof FinishBranchEvent) {
			// process finish branch
			branchManager.find(((FinishBranchEvent)event).getBranch());
			
			// TODO process branch result
			branchManager.updateNextBranchKey();
			
			eventManager.fireEvent(new NextBranchEvent(branchManager.current()));

		} else if (event instanceof BreakTestEvent) {

		} else if (event instanceof FinishTestEvent) {
			// process finish test
			
			mainWindow.setContent(finishScreen());
			//close();
			//mainWindow.open(new ExternalResource(getURL()+"?restartApplication"));

		/*} else if (event instanceof RenderContentEvent) {
			setMainwindowContent(null);

			LayoutComponent content = ((RenderContentEvent)event).getContent();
			// set slide component to window content
			// Alignment is ignored here
			if (content != null && content.getComponent() != null) {
				Component component = content.getComponent();
				setMainwindowContent(component);
				ProcessEventManager.fireEvent(new AfterRenderContentEvent(content));
			} else {
				ProcessEventManager.fireEvent(new ErrorTestEvent());
			}*/
		} else if (event instanceof AfterRenderContentEvent) {

		} else if (event instanceof ErrorTestEvent) {
			// process error
			eventManager.fireEvent(new ErrorNotificationEvent("Something went wrong"));

		/*} else if (event instanceof AbstractNotificationEvent) {
			mainWindow.showNotification(((AbstractNotificationEvent)event).getNotification());
		*/
		} else if (event instanceof AbstractComponentEvent<?>) {

		}
	}
	
	private ComponentContainer finishScreen() {
		VerticalLayout layout = new VerticalLayout();
		Panel panel = new Panel();
		Label label = new Label("Konec - zvonec, pro nový pokus stiskněte tlačítko");
		//Link link = new Link("stiskněte zde", new ExternalResource(getURL()+"?restartApplication"));
		Button button = new Button("Zkusit znovu", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				eventManager.fireEvent(new PrepareTestEvent(pack, null, false));
			}
		});
		panel.addComponent(label);
		panel.addComponent(button);
		layout.addComponent(panel);
		return layout;
	}
	
	/*public void close() {
		removeWindow(mainWindow);
		super.close();
		
	}*/

}
