/**
 * 
 */
package org.hypothesis.common.application;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hypothesis.common.application.ui.BaseWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.PermissionManager;
import org.hypothesis.persistence.TestManager;
import org.hypothesis.persistence.UserGroupManager;
import org.hypothesis.persistence.hibernate.GroupDao;
import org.hypothesis.persistence.hibernate.SlideOutputDao;
import org.hypothesis.persistence.hibernate.TestDao;
import org.hypothesis.persistence.hibernate.UserDao;

import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base application class
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractBaseApplication extends AbstractHibernateApplication implements
		CloseListener, HttpServletRequestListener {

	public static final String LANGUAGE_PARAMETER = "lang";
	public static final String ROOT = "/";
	public static final int LANGUAGE_COOKIE_AGE = 3600 * 24 * 365 * 10; // 10
																		// years

	private User currentUser = null;
	private UserGroupManager userGroupManager = null;
	private PermissionManager permissionManager = null;
	private TestManager testManager = null;

	private HttpServletResponse response;
	private BaseWindow mainWindow = null;

	public void afterAuthentication(User loggedUser) {
		// override
	}

	/**
	 * A class for user's authentication
	 * 
	 * @param String
	 *            login - user's login
	 * @param String
	 *            password - user's password
	 * @return User that can be logged in
	 * @throws Exception
	 */
	public User authenticate(String username, String password) throws Exception {
		User potentialUser = userGroupManager.findUserByUsernamePassword(
				username, password);

		// user with given password not found
		if (potentialUser == null) {
			throw new Exception(ApplicationMessages.get().getString(
					Messages.ERROR_BAD_LOGIN));
		}

		// check expiration date
		Date expireDate = potentialUser.getExpireDate();
		Date currentDate = new Date();
		if (expireDate != null && expireDate.compareTo(currentDate) < 0) {
			potentialUser.setEnabled(false);
			userGroupManager.addUser(potentialUser);
		}

		// user is not enabled
		if (!potentialUser.getEnabled()) {
			throw new Exception(ApplicationMessages.get().getString(
					Messages.ERROR_NOT_ENABLED_USER));
		}

		return potentialUser;
	}

	private Cookie createLanguageCookie(String language) {
		Cookie cookie = new Cookie(LANGUAGE_PARAMETER, language);
		// Use a fixed path
		cookie.setPath(ROOT);
		cookie.setMaxAge(LANGUAGE_COOKIE_AGE); // One hour
		return cookie;
	}

	/**
	 * Returns current authenticated user
	 * 
	 * @return User currentUser - current user
	 */
	public final User getCurrentUser() {
		return currentUser;
	}

	@Override
	public final BaseWindow getMainWindow() {
		return mainWindow;
	}

	/**
	 * Returns permissionManager instance
	 * 
	 * @return permissionManager
	 */
	public final PermissionManager getPermissionManager() {
		return permissionManager;
	}

	/**
	 * Returns userGroupManager instance
	 * 
	 * @return userGroupManager
	 */
	public final UserGroupManager getUserGroupManager() {
		return userGroupManager;
	}
	
	public final TestManager getTestManager() {
		return testManager;
	}

	@Override
	public void init() {
		super.init();
		ApplicationMessages.init(this);

		if (!ApplicationsContext.getInstance(this).isInitialized()) {
			mainWindow.showError(
					ApplicationMessages.get().getString(Messages.TEXT_ERROR), String
							.format(ApplicationMessages.get().getString(
									Messages.ERROR_APP_INITIALIZATION_FMT),
									ApplicationsContext.getInstance(this)
											.getError()));
		} else {
			ApplicationConfig appConfig = ApplicationsContext.getInstance(this)
					.getConfig();
			ApplicationSecurity.init(appConfig.getSecretKey(), appConfig.getCipherMethod());
		}

		userGroupManager = new UserGroupManager(new UserDao(),
				new GroupDao());
		permissionManager = PermissionManager.newInstance();
		
		testManager = new TestManager(new TestDao(), new SlideOutputDao());
	}

	public abstract void loadProtectedResources();

	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// dummy
	}

	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		this.response = response;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(LANGUAGE_PARAMETER)) {
					String lang = cookies[i].getValue();
					setLanguage(lang);
				}
			}
		}
	}

	public final void setCurrentUser(User user) {
		this.currentUser = user;
	}

	protected void setLanguage(String language) {
		Locale locale = new Locale(language);
		if (locale != null
				&& !locale.getLanguage().equals(this.getLocale().getLanguage())) {
			this.setLocale(locale);
			// TODO how to force client refresh?
			// if (this.mainWindow != null)
			// this.mainWindow.requestRepaintAll();
			Cookie cookie = createLanguageCookie(language);
			response.addCookie(cookie);
		}

	}

	private void setMainBaseWindow(BaseWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	public void setMainWindow(Window mainWindow) {
		assert (mainWindow instanceof BaseWindow);

		if (mainWindow instanceof BaseWindow) {
			setMainBaseWindow((BaseWindow) mainWindow);

			super.setMainWindow(mainWindow);
			this.mainWindow.addListener(this);
			this.mainWindow.addParameterHandler(new ParameterHandler() {
				public void handleParameters(Map<String, String[]> parameters) {
					if (parameters.containsKey(LANGUAGE_PARAMETER)) {
						String lang = parameters.get(LANGUAGE_PARAMETER)[0];
						setLanguage(lang);
					}
				}
			});
		}
	}

}
