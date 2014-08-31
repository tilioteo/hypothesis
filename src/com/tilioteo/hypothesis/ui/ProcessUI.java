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

import com.tilioteo.hypothesis.event.AbstractNotificationEvent;
import com.tilioteo.hypothesis.event.AfterFinishSlideEvent;
import com.tilioteo.hypothesis.event.AfterPrepareTestEvent;
import com.tilioteo.hypothesis.event.CloseTestEvent;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.FinishTestEvent;
import com.tilioteo.hypothesis.event.ProcessEvent;
import com.tilioteo.hypothesis.event.ProcessEventListener;
import com.tilioteo.hypothesis.event.RenderContentEvent;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.model.ProcessModel;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.servlet.ProcessServlet;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
@PreserveOnRefresh
public class ProcessUI extends HUI implements ProcessEventListener,
		DetachListener {

	private static Logger log = Logger.getLogger(ProcessUI.class);

	public static final String FULLSCREEN_PARAMETER = "fs";
	public static final String BACK_PARAMETER = "bk";
	public static final String TOKEN_PARAMETER = "token";
	public static final String CLOSE_URL = "/resource/close.html";
	public static final String ERROR_INVALID_ACCESS = "Invalid access.";

	@WebServlet(value = "/process/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ProcessUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends ProcessServlet {
	}

	private final ProcessModel processModel = new ProcessModel(this);
	private VerticalLayout clearLayout = new VerticalLayout();
	private boolean requestFullscreen = false;
	private boolean requestBack = false;
	private boolean animate = true;
	
	@Override
	protected void init(VaadinRequest request) {
		log.debug("ProcessUI initialization");
		addDetachListener(this);
		
		PluginManager.get().registerPlugins();
		
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

		String token = request.getParameter(TOKEN_PARAMETER);

		if (token != null) {
			log.debug(TOKEN_PARAMETER + token);

			processModel.followToken(token);
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			
			processModel.fireError(ERROR_INVALID_ACCESS);
		}
	}

	@Override
	public void handleEvent(ProcessEvent event) {
		log.debug(String.format("handleEvent: name = %s", event.getName() != null ? event.getName() : "NULL"));
		if (event instanceof AfterPrepareTestEvent) {
			showPreparedContent((AfterPrepareTestEvent) event);
			
		} else if (event instanceof RenderContentEvent) {
			renderContent((RenderContentEvent) event);

		} else if (event instanceof AfterFinishSlideEvent) {
			doAfterFinishSlide((AfterFinishSlideEvent) event);

		} else if (event instanceof FinishTestEvent) {
			showFinishContent((FinishTestEvent) event);

		} else if (event instanceof AbstractNotificationEvent) {
			if (event instanceof ErrorNotificationEvent) {
				showErrorDialog((ErrorNotificationEvent) event);

			} else {
				showNotification((AbstractNotificationEvent) event);
			}

		} else if (event instanceof CloseTestEvent) {
			close();
		} else {
			log.debug("Unknown process event - skip handling");
		}
	}

	private void doAfterFinishSlide(final AfterFinishSlideEvent event) {
		log.debug(String.format("doAfterFinishSlide: slide id = %s", event.getSlide() != null ? event.getSlide().getId() : "NULL"));
		clearContent(animate, new Command() {
			@Override
			public void execute() {
				processModel.processSlideFollowing(event.getSlide(), event.getDirection());
			}
		});
	}

	private void renderContent(RenderContentEvent event) {
		log.debug("renderContent::");
		LayoutComponent content = event.getContent();
		// set slide component to window content
		// Alignment is ignored here
		if (content != null && content.getComponent() != null) {
			Component component = content.getComponent();
			setSlideContent(component, event.getTimers());
			processModel.fireAfterRender(content);
		} else {
			log.error("Error while rendering slide.");
			processModel.fireTestError();
		}
	}
	
	private void showPreparedContent(final AfterPrepareTestEvent afterPrepareTestEvent) {
		log.debug(String.format("showPreparedContent: test id = %s", afterPrepareTestEvent.getTest() != null ? afterPrepareTestEvent.getTest().getId() : "NULL"));
		setContent(new PreparedTestContent(this, new Command() {
			@Override
			public void execute() {
				processModel.processTest(afterPrepareTestEvent.getTest());
			}
		}));
	}

	private void showFinishContent(final FinishTestEvent finishTestEvent) {
		log.debug(String.format("showFinishContent: test id = %s", finishTestEvent.getTest() != null ? finishTestEvent.getTest().getId() : "NULL"));
		clearContent(false, null);
		
		setContent(new FinishTestContent(new Command() {
			@Override
			public void execute() {
				processModel.fireClose(finishTestEvent.getTest());
			}
		}));
	}

	private void showNotification(AbstractNotificationEvent event) {
		log.debug(String.format("showNotification: test id = %s, message = %s", event.getTest() != null ? event.getTest().getId() : "NULL", event.getDescription() != null ? event.getDescription() : "NULL"));
		Notification notification = event.getNotification();
		notification.show(Page.getCurrent());
	}

	private void showErrorDialog(final ErrorNotificationEvent event) {
		log.debug(String.format("showErrorDialog: test id = %s, message = %s", event.getTest() != null ? event.getTest().getId() : "NULL", event.getDescription() != null ? event.getDescription() : "NULL"));
		ErrorDialog errorDialog = new ErrorDialog("Error", event.getCaption());
		errorDialog.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				processModel.fireClose(event.getTest());
			}
		});
		errorDialog.show(this);
	}

	/**
	 * sets empty vertical layout to remove old content
	 */
	public void clearContent(boolean animate, final Command nextCommand) {
		log.debug("clearContent::");
		removeAllTimers();
		if (animate) {
			Component content = getContent();
			if (content instanceof ComponentContainer) {
				AnimatorProxy animator = new AnimatorProxy();
				animator.addListener(new AnimatorProxy.AnimationListener() {
					@Override
					public void onAnimation(AnimationEvent event) {
						setContent(clearLayout);

						if (nextCommand != null) {
							nextCommand.execute();
						}
					}
				});
				((ComponentContainer)content).addComponent(animator);
				animator.animate(content, AnimType.FADE_OUT).setDuration(300).setDelay(0);
			}
		} else {
			setContent(clearLayout);
			
			if (nextCommand != null) {
				nextCommand.execute();
			}
		}
	}

	private void setSlideContent(Component component, Collection<Timer> timers) {
		log.debug("setSlideContent::");
		setContent(component);
		for (Timer timer : timers) {
			addTimer(timer);
		}
	}

	@Override
	public void detach(DetachEvent event) {
		log.debug("ProcessUI detached.");
		
		processModel.requestBreak();
	}

	@Override
	public void close() {
		log.debug("close::");
		super.close();

		if (!requestBack) {
			log.debug("Setting close url.");
			String path = VaadinServlet.getCurrent().getServletContext().getContextPath();
			Page.getCurrent().setLocation(path + CLOSE_URL);
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
