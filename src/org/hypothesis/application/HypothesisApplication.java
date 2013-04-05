/**
 * 
 */
package org.hypothesis.application;

import org.hypothesis.application.hypothesis.ui.MainWindow;
import org.hypothesis.common.application.AbstractBaseApplication;
import org.hypothesis.common.application.ui.LoginWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Application for user to select test
 * 
 */
public class HypothesisApplication extends AbstractBaseApplication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4571250368333738706L;

	private static ThreadLocal<HypothesisApplication> currentApplication = new ThreadLocal<HypothesisApplication>();

	/**
	 * Returns ManagerApplication application instance
	 * 
	 * @return an instance of the current application
	 */
	public static HypothesisApplication getInstance() {
		return currentApplication.get();
	}

	@Override
	public void init() {
		super.init();

		Window loginWindow = new LoginWindow<HypothesisApplication>(
				new LoginWindow.InstanceHandler<HypothesisApplication>() {
					public HypothesisApplication getInstance() {
						return HypothesisApplication.getInstance();
					}
				}, ApplicationMessages.get().getString(Messages.TEXT_LOGIN_TITLE),
				ApplicationMessages.get().getString(Messages.TEXT_LOGIN_HEADER));

		setTheme("chameleon-vaadin");

		// dočasné pro pokusy s úvodní obrazovkou
		setMainWindow(loginWindow);
		// setMainWindow(new MainWindow(this));
		// getMainWindow().addListener(this);
		// getMainWindow().setCaption("HypothesisApplication");
	}

	/**
	 * A "hidden" class - only authenticated user can call the content.
	 * 
	 * @param String
	 *            login - user's login
	 * @param String
	 *            password - user's password
	 */
	@Override
	public void loadProtectedResources() {
		setMainWindow(new MainWindow(this));
	}

	/**
	 * A method that starts the transaction
	 * 
	 * @param Application
	 *            application - the application, that tries to start a
	 *            transaction
	 * @param Object
	 *            transactionData
	 */
	@Override
	protected void onEndTransaction(Application application,
			Object transactionData) {
		if (application == HypothesisApplication.this) {
			currentApplication.set(null);
			currentApplication.remove();
		}
	}

	/**
	 * A method that ends the transaction
	 * 
	 * @param Application
	 *            application - the application, that tries to end a transaction
	 * @param Object
	 *            transactionData
	 */
	@Override
	protected void onStartTransaction(Application application,
			Object transactionData) {
		if (application == HypothesisApplication.this) {
			currentApplication.set(this);
		}
	}

	/**
	 * A class for user's authentication
	 * 
	 * @param String
	 *            login - user's login
	 * @param String
	 *            password - user's password
	 */
	/*
	 * protected User authenticate(String username, String password) throws
	 * Exception {
	 * 
	 * //ApplicationSecurity.setSessionObject(getSession(), "user", user);
	 * loadProtectedResources(); }
	 */

	/**
	 * Called when the user closes a window
	 * 
	 * @param event
	 *            - event containing
	 */
	public void windowClose(CloseEvent event) {
		// TODO Auto-generated method stub
		// boolean closed = true;
	}

}
