/**
 * 
 */
package org.hypothesis.application;

import java.util.Map;

import org.hypothesis.application.collector.core.ProcessManager;
import org.hypothesis.application.collector.ui.MainWindow;
import org.hypothesis.common.application.AbstractBaseApplication;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Token;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.TokenManager;
import org.hypothesis.persistence.hibernate.TokenDao;

import com.vaadin.Application;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Application for collecting test data
 * 
 */
public class CollectorApplication extends AbstractBaseApplication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6561582407766397961L;

	private static final String TOKEN_PARAMETER = "token";
	private static final String ERROR_INVALID_TOKEN = "Invalid token";
	private static final String URL_CLOSE_COMMAND = "?closeCollector";

	private static ThreadLocal<CollectorApplication> currentApplication = new ThreadLocal<CollectorApplication>();

	/**
	 * Returns CollectorApplication application instance
	 * 
	 * @return an instance of the current application
	 */
	public static CollectorApplication getInstance() {
		return currentApplication.get();
	}

	private TokenManager tokenManager;

	private ProcessManager processManager;

	/*
	 * this method finds token by unique identificator in database, sets user to
	 * application and start test processing
	 */
	private void followToken(String tokenUid) {
		this.tokenManager = new TokenManager(new TokenDao());
		Token token = tokenManager.findTokenByUid(tokenUid);
		if (token != null) {
			setCurrentUser(token.getUser());
			processPack(token.getPack(), token.getUser(), token.isProduction());
		} else {
			getMainWindow().showError(ERROR_INVALID_TOKEN);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			shutdown();
		}
	}

	/**
	 * Application initialization
	 */
	@SuppressWarnings("serial")
	@Override
	public void init() {
		super.init();
		processManager = new ProcessManager(this);

		Window window = new MainWindow(this);
		setMainWindow(window);

		// add listening for close event
		window.addListener(this);

		/*
		 * register parameter handler to get token parameter from url
		 */
		window.addParameterHandler(new ParameterHandler() {
			public void handleParameters(Map<String, String[]> parameters) {
				if (parameters.containsKey(TOKEN_PARAMETER)) {
					String tokenString = parameters.get(TOKEN_PARAMETER)[0];
					followToken(tokenString);
				}
			}
		});
	}

	@Override
	public void loadProtectedResources() {
		// dummy
	}

	/**
	 * A method that ends the transaction
	 * 
	 * @param Application
	 *            application - the application, which tries to end a
	 *            transaction
	 * @param Object
	 *            transactionData
	 */
	@Override
	protected void onEndTransaction(Application application,
			Object transactionData) {
		if (application == CollectorApplication.this) {
			currentApplication.set(null);
			currentApplication.remove();
		}
	}

	/**
	 * A method that starts the transaction
	 * 
	 * @param Application
	 *            application - the application, which tries to start a
	 *            transaction
	 * @param Object
	 *            transactionData
	 */
	@Override
	protected void onStartTransaction(Application application,
			Object transactionData) {
		if (application == CollectorApplication.this) {
			currentApplication.set(this);
		}
	}

	/*
	 * this method starts test processing
	 */
	private void processPack(Pack pack, User user, boolean production) {
		processManager.processWithPack(pack, user, production);
	}

	/*
	 * this method calls url to close application
	 */
	public void shutdown() {
		// set redirect url
		setLogoutURL(getURL() + URL_CLOSE_COMMAND); // TODO make constant across
													// applications
													// and use this one to
													// generate parameter in
													// jnlp as well

		close();
	}

	/**
	 * Called when the user closes a window
	 * 
	 * @param event
	 *            - event containing
	 */
	public void windowClose(CloseEvent event) {
		processManager.breakCurrentTest();
	}

}
