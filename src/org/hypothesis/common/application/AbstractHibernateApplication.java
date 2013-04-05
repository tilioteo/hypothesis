/**
 * 
 */
package org.hypothesis.common.application;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Application which use hibernate
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractHibernateApplication extends Application {

	/**
	 * We are using session-per-request pattern with Hibernate. By using
	 * Vaadin's transaction listener we can easily ensure that session is closed
	 * on each request without polluting our program code with extra logic.
	 */
	private void attachVaadinTransactionListener() {
		getContext().addTransactionListener(new TransactionListener() {

			public void transactionEnd(Application application,
					Object transactionData) {
				// Transaction listener gets fired for all (Http) sessions
				// of Vaadin applications, checking to be this one.
				onEndTransaction(application, transactionData);
			}

			public void transactionStart(Application application,
					Object transactionData) {
				onStartTransaction(application, transactionData);
			}
		});
	}

	protected Collection<Application> getApplications() {
		return ((WebApplicationContext) getContext()).getApplications();
	}

	protected WebBrowser getBrowser() {
		return ((WebApplicationContext) getContext()).getBrowser();
	}

	protected HttpSession getSession() {
		return ((WebApplicationContext) getContext()).getHttpSession();
	}

	@Override
	public void init() {
		attachVaadinTransactionListener();
	}

	/**
	 * A method that fires on end of the transaction
	 * 
	 * @param Application
	 *            application - the application, that tries to end a transaction
	 * @param Object
	 *            transactionData
	 */
	protected abstract void onEndTransaction(Application application,
			Object transactionData);

	/**
	 * A method that fires on start of the transaction
	 * 
	 * @param Application
	 *            application - the application, that tries to start a
	 *            transaction
	 * @param Object
	 *            transactionData
	 */
	protected abstract void onStartTransaction(Application application,
			Object transactionData);

}
