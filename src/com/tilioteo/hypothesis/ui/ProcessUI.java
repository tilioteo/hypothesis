/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Collection;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationEvent;
import org.vaadin.jouni.animator.shared.AnimType;
import org.vaadin.special.ui.ShortcutKey;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.event.AbstractNotificationEvent;
import com.tilioteo.hypothesis.event.AfterFinishSlideEvent;
import com.tilioteo.hypothesis.event.AfterPrepareTestEvent;
import com.tilioteo.hypothesis.event.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.CloseTestEvent;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.FinishTestEvent;
import com.tilioteo.hypothesis.event.NextSlideEvent;
import com.tilioteo.hypothesis.event.PriorSlideEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.event.RenderContentEvent;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.model.ProcessModel;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.servlet.ProcessServlet;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.Window;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
@PreserveOnRefresh
public class ProcessUI extends HUI /*implements CommandScheduler*/ {

	private static Logger log = Logger.getLogger(ProcessUI.class);

	public static final String FULLSCREEN_PARAMETER = "fs";
	public static final String BACK_PARAMETER = "bk";
	public static final String TOKEN_PARAMETER = "token";

	public static final String CLOSE_URL = "/resource/close.html";

	@WebServlet(value = "/process/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ProcessUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends ProcessServlet {
	}

	private ProcessModel processModel;
	private VerticalLayout clearLayout = new VerticalLayout();
	private boolean requestFullscreen = false;
	private boolean requestBack = false;
	private boolean animate = true;
	
	private String lastToken = null;
	
	//private CommandQueue commandQueue = new CommandQueue();
	
	@Override
	protected void init(VaadinRequest request) {
		super.init(request);
		
		ProcessEventBus.get().register(this);
		
		log.debug("ProcessUI initialization");

		processModel = new ProcessModel();
		
		PluginManager.get().registerPlugins();
		

		// TODO try to set token by uri fragment and implement UriFragmentChangeListener
		String token = initParameters(request);

		if (token != null) {
			log.debug(TOKEN_PARAMETER +"="+ token);
			lastToken = token;
			processModel.followToken(token);
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			processModel.fireError(Messages.getString("Message.Error.InvalidAccess"));
		}
	}
	
	@Override
	protected void refresh(VaadinRequest request) {
		super.refresh(request);
		
		String token = initParameters(request);
		
		if (token != null) {
			if (!token.equalsIgnoreCase(lastToken)) {
				processModel.requestBreak();
				
				lastToken = token;
				processModel.followToken(token);
			} else {
				log.debug("ProcessUI refresh");
			}
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			processModel.fireError(Messages.getString("Message.Error.InvalidAccess"));
		}
	}
	
	private String initParameters(VaadinRequest request) {
		String fullScreen = request.getParameter(FULLSCREEN_PARAMETER);
		if (fullScreen != null && !fullScreen.equalsIgnoreCase("false")) {
			requestFullscreen = true;
		}

		String canBack = request.getParameter(BACK_PARAMETER);
		if (null == canBack || !canBack.equalsIgnoreCase("true")) {
			requestBack = false;
		} else {
			requestBack = true;
		}

		return request.getParameter(TOKEN_PARAMETER);
	}

	/*public void scheduleCommand(Command command) {
		if (command != null) {
			try {
				commandQueue.put(command);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/

	@Subscribe
	public void doAfterFinishSlide(final AfterFinishSlideEvent event) {
		clearContent(animate, new Command() {
			@Override
			public void execute() {
				ProcessEventBus.get().post((Direction.NEXT.equals(event.getDirection())) ? new NextSlideEvent() : new PriorSlideEvent());
			}
		});
	}

	@Subscribe
	public void renderContent(RenderContentEvent event) {
		log.debug("renderContent::");
		Component component = event.getComponent();
		if (component != null) {
			setSlideContent(component, event.getTimers(), event.getShortcutKeys());

			ProcessEventBus.get().post(new AfterRenderContentEvent(component));
		} else {
			log.error("Error while rendering slide.");
			processModel.fireTestError();
		}
	}
	
	@Subscribe
	public void showPreparedContent(final AfterPrepareTestEvent afterPrepareTestEvent) {
		log.debug(String.format("showPreparedContent: test id = %s", afterPrepareTestEvent.getTest() != null ? afterPrepareTestEvent.getTest().getId() : "NULL"));
		setContent(new PreparedTestContent(this, new Command() {
			@Override
			public void execute() {
				processModel.processTest(afterPrepareTestEvent.getTest());
			}
		}));
	}

	@Subscribe
	public void showFinishContent(FinishTestEvent event) {
		clearContent(false, null);
		
		setContent(new FinishTestContent(new Command() {
			@Override
			public void execute() {
				ProcessEventBus.get().post(new CloseTestEvent());
			}
		}));
	}

	@Subscribe
	public void showNotification(AbstractNotificationEvent event) {
		if (event instanceof ErrorNotificationEvent) {
			showErrorDialog((ErrorNotificationEvent) event);

		} else {
			Notification notification = event.getNotification();
			notification.show(Page.getCurrent());
		}
	}

	private void showErrorDialog(final ErrorNotificationEvent event) {
		ErrorDialog errorDialog = new ErrorDialog("Error", event.getCaption());
		errorDialog.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				ProcessEventBus.get().post(new CloseTestEvent());
			}
		});
		errorDialog.show(this);
	}

	@Subscribe
	public void requestClose(final CloseTestEvent event) {
		close();
	}

	/**
	 * sets empty vertical layout to remove old content
	 */
	public void clearContent(boolean animate, final Command nextCommand) {
		log.debug("clearContent::");
		removeAllTimers();
		removeAllShortcutKeys();
		
		if (animate) {
			Component content = getContent();
			if (content instanceof ComponentContainer) {
				AnimatorProxy animator = new AnimatorProxy();
				animator.addListener(new AnimatorProxy.AnimationListener() {
					@Override
					public void onAnimation(AnimationEvent event) {
						setContent(clearLayout);
						Command.Executor.execute(nextCommand);
					}
				});
				((ComponentContainer)content).addComponent(animator);
				animator.animate(content, AnimType.FADE_OUT).setDuration(300).setDelay(0);
			}
		} else {
			setContent(clearLayout);
			Command.Executor.execute(nextCommand);
		}
	}

	private void setSlideContent(Component component, Collection<Timer> timers, Collection<ShortcutKey> shortcutKeys) {
		log.debug("setSlideContent::");
		setContent(component);
		
		// add timers
		for (Timer timer : timers) {
			addTimer(timer);
		}
		// add shortcut keys
		for (ShortcutKey shortcutKey : shortcutKeys) {
			addShortcutKey(shortcutKey);
		}
		focus();
	}

	@Override
	public void detach() {
		log.debug("ProcessUI detach");
		
		ProcessEventBus.get().unregister(this);
		processModel.requestBreak();
		processModel.clean();

		super.detach();
	}
	
	@Override
	public void close() {
		log.debug("close::");
		super.close();
		
		if (!requestBack) {
			log.debug("Closing window.");
			String path = VaadinServlet.getCurrent().getServletContext().getContextPath();
			Page.getCurrent().setLocation(path + CLOSE_URL);
			// this is also possible way but not for SWT browser
			//Page.getCurrent().getJavaScript().execute("window.setTimeout(function(){/*window.open('','_self','');*/window.close();},10);");
		} else {
			log.debug("History back.");
			JavaScript javaScript = Page.getCurrent().getJavaScript();
			javaScript.execute("window.history.back();");
		}
	}
	
	public boolean isFullscreen() {
		return requestFullscreen;
	}
	
	public boolean isAnimated() {
		return animate;
	}
}
