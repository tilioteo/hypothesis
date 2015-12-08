/**
 * 
 */
package com.tilioteo.hypothesis.presenter;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.business.ProcessManager;
import com.tilioteo.hypothesis.data.model.SimpleTest;
import com.tilioteo.hypothesis.data.model.Token;
import com.tilioteo.hypothesis.data.service.TokenService;
import com.tilioteo.hypothesis.event.interfaces.ProcessViewEvent.ProcessViewEndEvent;
import com.tilioteo.hypothesis.event.model.AbstractNotificationEvent;
import com.tilioteo.hypothesis.event.model.AfterFinishSlideEvent;
import com.tilioteo.hypothesis.event.model.AfterPrepareTestEvent;
import com.tilioteo.hypothesis.event.model.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.model.CloseTestEvent;
import com.tilioteo.hypothesis.event.model.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.model.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.model.FinishTestEvent;
import com.tilioteo.hypothesis.event.model.NextSlideEvent;
import com.tilioteo.hypothesis.event.model.PriorSlideEvent;
import com.tilioteo.hypothesis.event.model.RenderContentEvent;
import com.tilioteo.hypothesis.eventbus.ProcessEventBus;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.interfaces.Command;
import com.tilioteo.hypothesis.interfaces.UIPresenter;
import com.tilioteo.hypothesis.server.Messages;
import com.tilioteo.hypothesis.slide.ui.Window;
import com.tilioteo.hypothesis.ui.ErrorDialog;
import com.tilioteo.hypothesis.ui.ProcessUI;
import com.tilioteo.hypothesis.ui.TestBeginScreen;
import com.tilioteo.hypothesis.ui.TestEndScreen;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window.CloseEvent;

import net.engio.mbassy.listener.Handler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessUIPresenter implements UIPresenter {

	public static final String FULLSCREEN_PARAMETER = "fs";
	public static final String BACK_PARAMETER = "bk";
	public static final String TOKEN_PARAMETER = "token";

	public static final String CLOSE_URL = "/resource/close.html";

	private static Logger log = Logger.getLogger(ProcessUIPresenter.class);

	private ProcessUI ui;

	private ProcessEventBus bus;

	private boolean requestFullscreen = false;
	private boolean requestBack = false;
	private boolean animate = true;

	private String token = null;
	private String lastToken = null;

	private TokenService tokenService;
	private ProcessManager processManager;

	private SimpleTest preparedTest = null;

	public ProcessUIPresenter(ProcessUI ui) {
		this.ui = ui;

		bus = ProcessEventBus.createInstance(this);

		tokenService = TokenService.newInstance();
	}

	@Override
	public void initialize(VaadinRequest request) {
		log.debug("ProcessUIPresenter initialization");

		processManager = new ProcessManager(bus);

		initializePlugins(request);

		// TODO try to set token by uri fragment and implement
		// UriFragmentChangeListener
		initParameters(request);

		if (token != null) {
			log.debug(TOKEN_PARAMETER + "=" + token);
			lastToken = token;

			followToken(token);

			ui.setLoadingIndicatorVisible(false);
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			lastToken = null;
			fireError(Messages.getString("Message.Error.InvalidAccess"));
		}
	}

	private void initializePlugins(VaadinRequest request) {
		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();

		String configFileName = servletContext.getInitParameter(PluginManager.PLUGIN_CONFIG_LOCATION);

		if (configFileName != null && configFileName.length() > 0) {
			configFileName = servletContext.getRealPath(configFileName);
			File configFile = new File(configFileName);
			PluginManager.get().initializeFromFile(configFile);
		}
	}

	@Override
	public void refresh(VaadinRequest request) {
		initParameters(request);

		if (token != null) {
			if (!token.equalsIgnoreCase(lastToken)) {
				lastToken = token;

				processManager.requestBreakTest();

				followToken(token);
			} else {
				log.debug("ProcessUI refreshed");
			}
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			lastToken = null;
			fireError(Messages.getString("Message.Error.InvalidAccess"));
		}
	}

	@Override
	public void attach() {
		if (bus != null) {
			bus.register(this);
		}
	}

	@Override
	public void detach() {
		log.debug("detaching ProcessUI");

		if (bus != null) {
			bus.unregister(this);
		}

		processManager.requestBreakTest();
		processManager.clean();
	}

	@Override
	public void close() {
		ProcessEventBus.destroyInstance(this);
	}

	private void initParameters(VaadinRequest request) {
		token = request.getParameter(TOKEN_PARAMETER);

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
	}

	private void followToken(String tokenUid) {
		Token token = tokenService.findTokenByUid(tokenUid);
		processManager.setAutoSlideShow(false);
		// TODO maybe in the future send broadcast message to main view
		/*
		 * if (token != null && token.getViewUid() != null) { ProcessUIMessage
		 * message = new ProcessUIMessage(token.getViewUid());
		 * Broadcaster.broadcast(message.toString()); }
		 */

		processManager.processToken(token, false);
	}

	/**
	 * This method will save test event as well
	 */
	private void fireTestError() {
		processManager.fireTestError();
	}

	/**
	 * This method does notification only.
	 * 
	 * @param caption
	 *            Error description
	 */
	private void fireError(String caption) {
		bus.post(new ErrorNotificationEvent(caption));
	}

	private void showErrorDialog(final ErrorNotificationEvent event) {
		ErrorDialog errorDialog = new ErrorDialog(Messages.getString("Caption.Error"), event.getCaption());
		errorDialog.setButtonCaption(Messages.getString("Caption.Button.OK"));
		errorDialog.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				bus.post(new CloseTestEvent());
			}
		});
		ui.showErrorDialog(errorDialog);
	}

	/*
	 * public boolean isFullscreen() { return requestFullscreen; }
	 * 
	 * public boolean isAnimated() { return animate; }
	 */

	@Handler
	public void doAfterFinishSlide(final AfterFinishSlideEvent event) {
		ui.clearContent(animate, new Command() {
			@Override
			public void execute() {
				bus.post((Direction.NEXT.equals(event.getDirection())) ? new NextSlideEvent() : new PriorSlideEvent());
			}
		});
	}

	@Handler
	public void renderContent(RenderContentEvent event) {
		log.debug("renderContent::");
		Component component = event.getComponent();
		if (component != null) {
			ui.setSlideContent(component);

			bus.post(new AfterRenderContentEvent(component));
		} else {
			log.error("Error while rendering slide.");
			fireTestError();
		}
	}

	@Handler
	public void showPreparedContent(final AfterPrepareTestEvent event) {
		log.debug(String.format("showPreparedContent: test id = %s",
				event.getTest() != null ? event.getTest().getId() : "NULL"));

		preparedTest = event.getTest();

		TestBeginScreen screen = new TestBeginScreen(requestFullscreen, 5);
		screen.setInfoLabelCaption(Messages.getString("Message.Info.TestReady"));
		screen.setControlButtonCaption(Messages.getString("Caption.Button.Run"));

		screen.setNextCommand(new Command() {
			@Override
			public void execute() {
				ui.clearContent(animate, new Command() {
					@Override
					public void execute() {
						processManager.processTest(preparedTest);
					}
				});
			}
		});

		ui.setContent(screen);
	}

	@Handler
	public void processViewEnd(ProcessViewEndEvent event) {
		ui.clearContent(animate, null);
	}

	@Handler
	public void showFinishContent(FinishTestEvent event) {
		ui.clearContent(false, null);

		TestEndScreen screen = new TestEndScreen();
		screen.setInfoLabelCaption(Messages.getString("Message.Info.TestFinished"));
		screen.setControlButtonCaption(Messages.getString("Caption.Button.Close"));
		screen.setNextCommand(new Command() {
			@Override
			public void execute() {
				bus.post(new CloseTestEvent());
			}
		});

		ui.setContent(screen);
	}

	@Handler
	public void showNotification(AbstractNotificationEvent event) {
		if (event instanceof ErrorNotificationEvent) {
			showErrorDialog((ErrorNotificationEvent) event);

		} else {
			Notification notification = event.getNotification();
			notification.show(ui.getPage());
		}
	}

	@Handler
	public void requestClose(final CloseTestEvent event) {
		log.debug("close requested");
		if (requestBack) {
			log.debug("closing ProcessUI with history back");
			JavaScript javaScript = ui.getPage().getJavaScript();
			javaScript.execute("window.history.back();");
			ui.requestClose();

		} else {
			String path = VaadinServlet.getCurrent().getServletContext().getContextPath();
			ui.getPage().setLocation(path + CLOSE_URL);
			// this is also possible way but not for SWT browser
			// Page.getCurrent().getJavaScript().execute("window.setTimeout(function(){/*window.open('','_self','');*/window.close();},10);")

			log.debug("closing ProcessUI");
			ui.requestClose();
		}
	}

}
