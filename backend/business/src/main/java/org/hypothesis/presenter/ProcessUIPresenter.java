/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import org.apache.log4j.Logger;
import org.hypothesis.business.ProcessManager;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.event.interfaces.ProcessViewEvent.ProcessViewEndEvent;
import org.hypothesis.event.model.AbstractNotificationEvent;
import org.hypothesis.event.model.AfterFinishSlideEvent;
import org.hypothesis.event.model.AfterPrepareTestEvent;
import org.hypothesis.event.model.AfterRenderContentEvent;
import org.hypothesis.event.model.CloseTestEvent;
import org.hypothesis.event.model.ErrorNotificationEvent;
import org.hypothesis.event.model.FinishSlideEvent.Direction;
import org.hypothesis.event.model.FinishTestEvent;
import org.hypothesis.event.model.NextSlideEvent;
import org.hypothesis.event.model.PriorSlideEvent;
import org.hypothesis.event.model.RenderContentEvent;
import org.hypothesis.eventbus.HasProcessEventBus;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.interfaces.Command;
import org.hypothesis.server.Messages;
import org.hypothesis.slide.ui.Window;
import org.hypothesis.ui.ErrorDialog;
import org.hypothesis.ui.ProcessUI;
import org.hypothesis.ui.TestBeginScreen;
import org.hypothesis.ui.TestEndScreen;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window.CloseEvent;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ProcessUIPresenter extends AbstractUIPresenter implements HasProcessEventBus {

	public static final String FULLSCREEN_PARAMETER = "fs";
	public static final String BACK_PARAMETER = "bk";
	public static final String TOKEN_PARAMETER = "token";

	public static final String CLOSE_URL = "/resource/close.html";

	private static final Logger log = Logger.getLogger(ProcessUIPresenter.class);

	private final ProcessUI ui;

	private final ProcessEventBus bus;

	private boolean requestFullscreen = false;
	private boolean requestBack = false;
	private final boolean animate = true;

	private String tokenString = null;
	private String lastTokenString = null;

	private final TokenService tokenService;
	private ProcessManager processManager;

	private SimpleTest preparedTest = null;

	/**
	 * Construct with bus
	 * 
	 * @param ui
	 */
	public ProcessUIPresenter(ProcessUI ui) {
		this.ui = ui;

		bus = ProcessEventBus.createInstance(this);

		tokenService = TokenService.newInstance();
	}

	@Override
	public void initialize(VaadinRequest request) {
		log.debug("ProcessUIPresenter initialization");

		super.initialize(request);

		processManager = new ProcessManager(bus);

		// TODO try to set token by uri fragment and implement
		// UriFragmentChangeListener
		initParameters(request);

		if (tokenString != null) {
			log.debug(TOKEN_PARAMETER + "=" + tokenString);
			lastTokenString = tokenString;

			followToken(tokenString);

			ui.setLoadingIndicatorVisible(false);
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			lastTokenString = null;
			fireError(Messages.getString("Message.Error.InvalidAccess"));
		}
	}

	@Override
	public void refresh(VaadinRequest request) {
		initParameters(request);

		if (tokenString != null) {
			if (!tokenString.equalsIgnoreCase(lastTokenString)) {
				lastTokenString = tokenString;

				processManager.requestBreakTest();

				followToken(tokenString);
			} else {
				log.debug("ProcessUI refreshed");
			}
		} else {
			log.debug(TOKEN_PARAMETER + "=(null)");
			lastTokenString = null;
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
		tokenString = request.getParameter(TOKEN_PARAMETER);

		String fullScreen = request.getParameter(FULLSCREEN_PARAMETER);
		if (fullScreen != null && !"false".equalsIgnoreCase(fullScreen)) {
			requestFullscreen = true;
		}

		String canBack = request.getParameter(BACK_PARAMETER);
		if (null == canBack || !"true".equalsIgnoreCase(canBack)) {
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

	/**
	 * Do after finish slide
	 * 
	 * @param event
	 */
	@Handler
	public void doAfterFinishSlide(final AfterFinishSlideEvent event) {
		ui.clearContent(animate, new Command() {
			@Override
			public void execute() {
				bus.post(Direction.NEXT.equals(event.getDirection()) ? new NextSlideEvent() : new PriorSlideEvent());
			}
		});
	}

	/**
	 * Do on render
	 * 
	 * @param event
	 */
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

	/**
	 * Do after test prepared
	 * 
	 * @param event
	 */
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

	/**
	 * Clear view
	 * 
	 * @param event
	 */
	@Handler
	public void processViewEnd(ProcessViewEndEvent event) {
		ui.clearContent(animate, null);
	}

	/**
	 * Show finish info
	 * 
	 * @param event
	 */
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

	/**
	 * Show general notification
	 * 
	 * @param event
	 */
	@Handler
	public void showNotification(AbstractNotificationEvent event) {
		if (event instanceof ErrorNotificationEvent) {
			showErrorDialog((ErrorNotificationEvent) event);

		} else {
			Notification notification = event.getNotification();
			notification.show(ui.getPage());
		}
	}

	/**
	 * Do on test closing
	 * 
	 * @param event
	 */
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

	@Override
	public ProcessEventBus getProcessEventBus() {
		return bus;
	}

}
