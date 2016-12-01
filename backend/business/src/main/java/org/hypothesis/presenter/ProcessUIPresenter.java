/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;
import org.hypothesis.business.ProcessManager;
import org.hypothesis.cdi.Process;
import org.hypothesis.data.interfaces.TokenService;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Token;
import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.event.interfaces.ProcessViewEvent.ProcessViewEndEvent;
import org.hypothesis.event.model.*;
import org.hypothesis.event.model.FinishSlideEvent.Direction;
import org.hypothesis.interfaces.Detachable;
import org.hypothesis.interfaces.UIPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.ErrorDialog;
import org.hypothesis.ui.ProcessUI;
import org.hypothesis.ui.TestBeginScreen;
import org.hypothesis.ui.TestEndScreen;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Process
public class ProcessUIPresenter extends AbstractUIPresenter implements UIPresenter, Detachable {

	public static final String FULLSCREEN_PARAMETER = "fs";
	public static final String BACK_PARAMETER = "bk";
	public static final String TOKEN_PARAMETER = "token";

	public static final String CLOSE_URL = "/resource/close.html";

	private static final Logger log = Logger.getLogger(ProcessUIPresenter.class);

	private ProcessUI ui;

	private boolean requestFullscreen = false;
	private boolean requestBack = false;
	private final boolean animate = true;

	private String tokenString = null;
	private String lastTokenString = null;

	@Inject
	private TokenService tokenService;

	@Inject
	private Event<ProcessEvent> procEvent;

	@Inject
	private ProcessManager processManager;

	private SimpleTest preparedTest = null;

	public ProcessUIPresenter() {
		System.out.println("Construct " + getClass().getName());
	}

	@Override
	public void initialize(VaadinRequest request) {
		log.debug("ProcessUIPresenter initialization");

		super.initialize(request);

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
	public void detach() {
		log.debug("detaching ProcessUI");

		processManager.requestBreakTest();
		processManager.clean();
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
		procEvent.fire(new ErrorNotificationEvent(caption));
	}

	private void showErrorDialog(final ErrorNotificationEvent event) {
		ErrorDialog errorDialog = new ErrorDialog(Messages.getString("Caption.Error"), event.getCaption());
		errorDialog.setButtonCaption(Messages.getString("Caption.Button.OK"));
		errorDialog.addCloseListener(e -> procEvent.fire(new CloseTestEvent()));
		ui.showErrorDialog(errorDialog);
	}

	/**
	 * Do after finish slide
	 * 
	 * @param event
	 */
	public void doAfterFinishSlide(@Observes final AfterFinishSlideEvent event) {
		ui.clearContent(animate, () -> procEvent
				.fire(Direction.NEXT.equals(event.getDirection()) ? new NextSlideEvent() : new PriorSlideEvent()));
	}

	/**
	 * Do on render
	 * 
	 * @param event
	 */
	public void renderContent(@Observes RenderContentEvent event) {
		log.debug("renderContent::");
		Component component = event.getComponent();
		if (component != null) {
			ui.setSlideContent(component);

			procEvent.fire(new AfterRenderContentEvent(component));
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
	public void showPreparedContent(@Observes final AfterPrepareTestEvent event) {
		log.debug(String.format("showPreparedContent: test id = %s",
				event.getTest() != null ? event.getTest().getId() : "NULL"));

		preparedTest = event.getTest();

		TestBeginScreen screen = new TestBeginScreen(requestFullscreen, 5);
		screen.setInfoLabelCaption(Messages.getString("Message.Info.TestReady"));
		screen.setControlButtonCaption(Messages.getString("Caption.Button.Run"));

		screen.setNextCommand(() -> ui.clearContent(animate, () -> processManager.processTest(preparedTest)));

		ui.setContent(screen);
	}

	/**
	 * Clear view
	 * 
	 * @param event
	 */
	public void processViewEnd(@Observes ProcessViewEndEvent event) {
		ui.clearContent(animate, null);
	}

	/**
	 * Show finish info
	 * 
	 * @param event
	 */
	public void showFinishContent(@Observes FinishTestEvent event) {
		ui.clearContent(false, null);

		TestEndScreen screen = new TestEndScreen();
		screen.setInfoLabelCaption(Messages.getString("Message.Info.TestFinished"));
		screen.setControlButtonCaption(Messages.getString("Caption.Button.Close"));
		screen.setNextCommand(() -> procEvent.fire(new CloseTestEvent()));

		ui.setContent(screen);
	}

	/**
	 * Show general notification
	 * 
	 * @param event
	 */
	public void showNotification(@Observes AbstractNotificationEvent event) {
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
	public void requestClose(@Observes final CloseTestEvent event) {
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
	public void setUI(UI ui) {
		if (ui instanceof ProcessUI) {
			this.ui = (ProcessUI) ui;
		}
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
	}

	@Override
	public void close() {
		// nop
	}
}
