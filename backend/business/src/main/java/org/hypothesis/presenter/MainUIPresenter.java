/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.SessionManager;
import org.hypothesis.cdi.Main;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.GuestAccessRequestedEvent;
import org.hypothesis.event.interfaces.MainUIEvent.InvalidLoginEvent;
import org.hypothesis.event.interfaces.MainUIEvent.InvalidUserPermissionEvent;
import org.hypothesis.event.interfaces.MainUIEvent.PostViewChangeEvent;
import org.hypothesis.event.interfaces.MainUIEvent.UserLoggedOutEvent;
import org.hypothesis.event.interfaces.MainUIEvent.UserLoginRequestedEvent;
import org.hypothesis.interfaces.LoginPresenter;
import org.hypothesis.interfaces.MainPresenter;
import org.hypothesis.interfaces.UIPresenter;
import org.hypothesis.navigator.HypothesisViewType;
import org.hypothesis.ui.LoginScreen;
import org.hypothesis.ui.MainScreen;
import org.hypothesis.utility.ViewUtility;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.UIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Main
@UIScoped
public class MainUIPresenter extends AbstractUIPresenter implements UIPresenter {

	private UI ui;

	@Inject
	private CDIViewProvider viewProvider;

	@Inject
	private LoginPresenter loginPresenter;

	@Inject
	private MainPresenter mainPresenter;

	@Inject
	private UserService userService;

	@Inject
	private Event<MainUIEvent> mainEvent;

	private MainScreen mainScreen = null;
	private LoginScreen loginScreen = null;

	private String uid;
	// private String pid = null;

	public MainUIPresenter() {
		System.out.println("Construct " + getClass().getName());
	}

	@Override
	public void initialize(VaadinRequest request) {
		super.initialize(request);

		uid = UUID.randomUUID().toString().replaceAll("-", "");
		SessionManager.setMainUID(uid);

		// pid = request.getParameter("pid");

		updateUIContent();
	}

	private MainScreen getMainView() {
		if (null == mainScreen) {
			mainScreen = mainPresenter.createScreen();
			initNavigator(mainScreen);

			// new HypothesisNavigator(mainScreen.getContent());
		}

		return mainScreen;
	}

	private void initNavigator(ComponentContainer container) {
		Navigator navigator = new Navigator(ui, mainScreen.getContent());
		navigator.addProvider(viewProvider);

		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(final ViewChangeEvent event) {
				return ViewUtility.isUserViewAllowed(event.getNewView().getClass());
			}

			@Override
			public void afterViewChange(final ViewChangeEvent event) {
				// Appropriate events get fired after the view is changed.
				mainEvent.fire(new PostViewChangeEvent(event.getViewName()));
			}
		});

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
			ui.removeStyleName("loginview");

			String viewName = getViewName();

			if (StringUtils.isNotEmpty(viewName) && userCanAccessView(user, viewName)) {
				ui.getNavigator().navigateTo(viewName);
			} else {
				if (!User.GUEST.equals(user)) {
					ui.getNavigator().navigateTo(HypothesisViewType.PACKS.getViewName());
				} else {
					ui.getNavigator().navigateTo(HypothesisViewType.PUBLIC.getViewName());
				}
			}
		} else {
			ui.setContent(getLoginScreen());
			ui.addStyleName("loginview");
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
		if (StringUtils.isNotEmpty(fragment) && fragment.startsWith("!")) {
			fragment = fragment.substring(1);
			int l = fragment.lastIndexOf('/');
			if (l > 0) {
				fragment = fragment.substring(0, l);
			}
			l = fragment.indexOf('?');
			if (l >= 0) {
				fragment = fragment.substring(0, l);
			}
		}

		return fragment;
	}

	private void setUser(User user) {
		SessionManager.setLoggedUser(user);
	}

	private boolean userCanLogin(User user) {
		if (user != null && user.getEnabled() != null && user.getEnabled().booleanValue()) {
			Date expired = user.getExpireDate();
			Date now = new Date();
			if (null == expired || expired.after(now)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Do on login trial
	 * 
	 * @param event
	 */
	public void userLoginRequested(@Observes final UserLoginRequestedEvent event) {
		User user = userService.findByUsernamePassword(event.getUserName(), event.getPassword());

		if (user != null) {

			if (userCanLogin(user)) {
				setUser(user);
				updateUIContent();
			} else {
				mainEvent.fire(new InvalidUserPermissionEvent());
			}
		} else {
			mainEvent.fire(new InvalidLoginEvent());
		}
	}

	/**
	 * Do for anonymous guest user
	 * 
	 * @param event
	 */
	public void guestAccessRequested(@Observes final GuestAccessRequestedEvent event) {
		setUser(User.GUEST);

		updateUIContent();
	}

	/**
	 * Do on user logout
	 * 
	 * @param event
	 */
	public void userLoggedOut(@Observes final UserLoggedOutEvent event) {
		// When the user logs out, current VaadinSession gets closed and the
		// page gets reloaded on the login screen. Do notice the this doesn't
		// invalidate the current HttpSession.
		SessionManager.setLoggedUser(null);
		VaadinSession.getCurrent().close();

		Page.getCurrent().reload();
	}

	@Override
	public void close() {
		// nop
	}

	@Override
	public void refresh(VaadinRequest request) {
		// nop
	}

	@Override
	public void setUI(UI ui) {
		this.ui = ui;
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
	}

}
