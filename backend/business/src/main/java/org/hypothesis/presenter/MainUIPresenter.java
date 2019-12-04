/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.SessionManager;
import org.hypothesis.business.UserSessionManager;
import org.hypothesis.business.VNAddressPositionManager;
import org.hypothesis.business.data.SessionData;
import org.hypothesis.business.data.UserSessionData;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent.*;
import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.LoginPresenter;
import org.hypothesis.interfaces.MainPresenter;
import org.hypothesis.navigator.HypothesisNavigator;
import org.hypothesis.navigator.HypothesisViewType;
import org.hypothesis.push.Pushable;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.ui.LoginScreen;
import org.hypothesis.ui.MainScreen;
import org.hypothesis.ui.MainUI;
import org.hypothesis.utility.UIMessageUtility;

import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hypothesis.data.model.User.GUEST;
import static org.hypothesis.ui.MainUIStyles.LOGIN_VIEW;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class MainUIPresenter extends AbstractUIPresenter implements HasMainEventBus, Broadcaster, Pushable {

    private final MainUI ui;
    private final MainEventBus bus;
    private final LoginPresenter loginPresenter;
    private final MainPresenter mainPresenter;
    private MainScreen mainScreen = null;
    private LoginScreen loginScreen = null;
    private String uid;
    private UserService userService;

    private Properties positionProperties;

    public MainUIPresenter(MainUI ui) {
        this.ui = ui;

        bus = new MainEventBus();
        SessionManager.setMainEventBus(bus);

        loginPresenter = new LoginPresenterImpl();
        mainPresenter = new MainPresenterImpl();
    }

    @Override
    public void initialize(VaadinRequest request) {
        super.initialize(request);

        uid = UUID.randomUUID().toString().replaceAll("-", "");
        SessionManager.setMainUID(uid);

        userService = UserService.newInstance();

        updateUIContent();
    }

    private MainScreen getMainView() {
        if (null == mainScreen) {
            mainScreen = mainPresenter.createScreen();
            new HypothesisNavigator(mainScreen.getContent());
        }

        return mainScreen;
    }

    private LoginScreen getLoginScreen() {
        if (null == loginScreen) {
            loginScreen = loginPresenter.createScreen();
        }

        loginScreen.refresh();
        return loginScreen;
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateUIContent() {
        User user = SessionManager.getLoggedUser();

        if (user != null) {
            // Authenticated user
            ui.setContent(getMainView());
            ui.removeStyleName(LOGIN_VIEW);

            String viewName = getViewName();

            if (isNotEmpty(viewName) && userCanAccessView(user, viewName)) {
                ui.getNavigator().navigateTo(viewName);
            } else {
                if (!User.GUEST.equals(user)) {
                    ui.getNavigator().navigateTo(HypothesisViewType.PACKS.getViewName());
                    // VN specific - removed public packs
                    // } else {
                    // ui.getNavigator().navigateTo(HypothesisViewType.PUBLIC.getViewName());
                }
            }
        } else {
            ui.setContent(getLoginScreen());
            ui.addStyleName(LOGIN_VIEW);
        }
    }

    private boolean userCanAccessView(User user, String viewName) {
        HypothesisViewType viewType = HypothesisViewType.getByViewName(viewName);
        if (viewType != null) {
            return viewType.isAllowed(user.getRoles());
        }

        return false;
    }

    private String getViewName() {
        String fragment = Page.getCurrent().getUriFragment();
        if (isNotEmpty(fragment) && fragment.startsWith("!")) {
            fragment = fragment.substring(1);
            int l = fragment.lastIndexOf("/");
            if (l > 0) {
                fragment = fragment.substring(0, l);
            }
            l = fragment.indexOf("?");
            if (l >= 0) {
                fragment = fragment.substring(0, l);
            }
        }

        return fragment;
    }

    private void setUser(User user) {
        positionProperties = VNAddressPositionManager.getProperties();
        SessionManager.setLoggedUser(user);

        UserSessionData userSessionData = UserSessionManager.ensureUserSessionData(user, uid);
        SessionData sessionData = userSessionData.getSessionData(uid);
        sessionData.setAddress(Page.getCurrent().getWebBrowser().getAddress());
        sessionData.setPosition(getPosition(sessionData.getAddress()));

        broadcast(UIMessageUtility.createRefreshUserTestStateMessage(user.getId()));
    }

    private String getPosition(String address) {
        if (address != null) {
            return Optional.of(address)//
                    .map(positionProperties::getProperty)//
                    .filter(StringUtils::isNotBlank)//
                    .orElse("<N/A>");
        }
        return null;
    }

    private boolean userCanLogin(User user) {
        if (user != null) {
            if (user.getEnabled() != null && user.getEnabled()) {
                Date expired = user.getExpireDate();
                Date now = new Date();
                return null == expired || expired.after(now);
            }
        }

        return false;
    }

    @Handler
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        User user = userService.findByUsernamePassword(event.getUserName(), event.getPassword());

        if (user != null) {

            if (userCanLogin(user)) {
                setUser(user);
                updateUIContent();
            } else {
                bus.post(new InvalidUserPermissionEvent());
            }
        } else {
            bus.post(new InvalidLoginEvent());
        }
    }

    @Handler
    public void guestAccessRequested(final GuestAccessRequestedEvent event) {
        setUser(GUEST);

        updateUIContent();
    }

    @Handler
    public void userLoggedOut(final UserLoggedOutEvent event) {
        logout();
    }

    private void logout() {
        User user = SessionManager.getLoggedUser();

        if (user != null) {
            UserSessionManager.purgeUserSessionData(user, uid);
        }

        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        SessionManager.setLoggedUser(null);
        VaadinSession.getCurrent().close();

        Page.getCurrent().reload();

        broadcast(UIMessageUtility.createRefreshUserTestStateMessage(user.getId()));
        broadcast(UIMessageUtility.createRefreshUserPacksViewMessage(user.getId()));
    }

    @Override
    public void refresh(VaadinRequest request) {
        // nop
    }

    @Override
    public void attach() {
        bus.register(this);
    }

    @Override
    public void detach() {
        bus.unregister(this);
    }

    @Override
    public MainEventBus getBus() {
        return bus;
    }

    @Override
    public void close() {
        logout();
        cleanup();
    }

    @Override
    public void cleanup() {
        HibernateUtil.closeCurrent();
    }

}
