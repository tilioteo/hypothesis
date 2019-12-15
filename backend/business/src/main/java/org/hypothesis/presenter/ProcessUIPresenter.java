/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import net.engio.mbassy.listener.Handler;
import org.apache.log4j.Logger;
import org.hypothesis.business.ProcessManager;
import org.hypothesis.business.SessionManager;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.event.interfaces.ProcessViewEvent.ProcessViewEndEvent;
import org.hypothesis.event.model.*;
import org.hypothesis.event.model.FinishSlideEvent.Direction;
import org.hypothesis.eventbus.HasProcessEventBus;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.push.Pushable;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.ui.ErrorDialog;
import org.hypothesis.ui.ProcessUI;
import org.hypothesis.ui.TestBeginScreen;
import org.hypothesis.ui.TestEndScreen;
import org.hypothesis.utility.UIMessageUtility;

import java.util.Optional;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class ProcessUIPresenter extends AbstractUIPresenter implements HasProcessEventBus, Broadcaster, Pushable {

    public static final String FULLSCREEN_PARAMETER = "fs";
    public static final String BACK_PARAMETER = "bk";
    public static final String TOKEN_PARAMETER = "token";

    public static final String CLOSE_URL = "/resource/close.html";

    private static final Logger log = Logger.getLogger(ProcessUIPresenter.class);

    private final ProcessUI ui;

    private final ProcessEventBus bus;
    private final boolean animate = true;
    private final TokenService tokenService;
    private boolean requestFullscreen = false;
    private boolean requestBack = false;
    private String token = null;
    private String lastToken = null;
    private String viewUID = null;
    private Long packId = null;
    private Long userId = null;
    private ProcessManager processManager;

    private SimpleTest preparedTest = null;

    public ProcessUIPresenter(ProcessUI ui) {
        this.ui = ui;

        bus = new ProcessEventBus();
        SessionManager.setProcessEventBus(bus);

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

        if (token != null) {
            log.debug(TOKEN_PARAMETER + "=" + token);
            lastToken = token;

            followToken(token);

            ui.setLoadingIndicatorVisible(false);
        } else {
            viewUID = null;
            log.debug(TOKEN_PARAMETER + "=(null)");
            lastToken = null;
            fireError(Messages.getString("Message.Error.InvalidAccess"));
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
        bus.register(this);
    }

    @Override
    public void detach() {
        log.debug("detaching ProcessUI");

        bus.unregister(this);
    }

    private void initParameters(VaadinRequest request) {
        token = request.getParameter(TOKEN_PARAMETER);

        String fullScreen = request.getParameter(FULLSCREEN_PARAMETER);
        if (fullScreen != null && !fullScreen.equalsIgnoreCase("false")) {
            requestFullscreen = true;
        }

        String canBack = request.getParameter(BACK_PARAMETER);
        requestBack = !(null == canBack || !canBack.equalsIgnoreCase("true"));
    }

    private void followToken(String tokenUid) {
        Token token = tokenService.findTokenByUid(tokenUid);
        if (token != null) {
            viewUID = token.getViewUid();
            packId = token.getPack().getId();
            userId = Optional.ofNullable(token.getUser()).map(User::getId).orElse(null);
        } else {
            viewUID = null;
            packId = null;
            userId = null;
        }

        processManager.setAutoSlideShow(false);

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
     * @param caption Error description
     */
    private void fireError(String caption) {
        bus.post(new ErrorNotificationEvent(caption));
    }

    private void showErrorDialog(final ErrorNotificationEvent event) {
        ErrorDialog errorDialog = new ErrorDialog(Messages.getString("Caption.Error"), event.getCaption());
        errorDialog.setButtonCaption(Messages.getString("Caption.Button.OK"));
        errorDialog.addCloseListener(e -> bus.post(new CloseTestEvent()));
        ui.showErrorDialog(errorDialog);
    }

    @Handler
    public void doAfterFinishSlide(final AfterFinishSlideEvent event) {
        ui.clearContent(animate, () -> bus.post((Direction.NEXT.equals(event.getDirection()))
                ? new NextSlideEvent() : new PriorSlideEvent()));
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

        screen.setNextCommand(() -> ui.clearContent(animate,
                () -> processManager.processTest(preparedTest)));

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
        screen.setNextCommand(() -> bus.post(new CloseTestEvent()));

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

        } else {
            String path = VaadinServlet.getCurrent().getServletContext().getContextPath();
            // for SWT browser
            //ui.getPage().setLocation(path + CLOSE_URL);

            // this is also possible way but not for SWT browser
            Page.getCurrent().getJavaScript().execute("window.setTimeout(function(){/*window.open('','_self','');*/window.close();},10);");

            log.debug("closing ProcessUI");
        }
        ui.requestClose();
    }

    @Handler
    public void requestCleanup(final CleanupEvent event) {
        cleanup();
    }

    @Override
    public ProcessEventBus getBus() {
        return bus;
    }

    @Override
    public void close() {
        processManager.requestBreakTest();

        broadcast(UIMessageUtility.createProcessViewClosedMessage(viewUID, packId, userId));
    }

    @Override
    public void cleanup() {
        processManager.clean();
        HibernateUtil.closeCurrent();
    }


}
