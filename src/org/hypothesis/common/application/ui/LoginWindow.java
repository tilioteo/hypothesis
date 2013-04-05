package org.hypothesis.common.application.ui;

import org.hypothesis.common.application.AbstractBaseApplication;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.User;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * User's interface - Login window
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 * 
 *         Application login window, uses login form
 */
public class LoginWindow<T extends AbstractBaseApplication> extends BaseWindow
		implements LoginForm.LoginListener {

	public interface InstanceHandler<T> {
		T getInstance();
	}

	private static final long serialVersionUID = -5254467996260556440L;
	private static final String USERNAME_INPUT_NAME = "juuzrnejm";

	private static final String PASSWORD_INPUT_NAME = "paazfort";

	private InstanceHandler<T> instanceHandler = null;

	/**
	 * Constructor
	 */
	public LoginWindow(InstanceHandler<T> instanceHandler, String title,
			String header) {
		super();
		setCaption(title);
		this.instanceHandler = instanceHandler;
		this.initUI(header);
	}

	/**
	 * The method uploads the graphical components into the window
	 */
	private void initUI(String header) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Label headerLabel = new Label("<h1>" + header + "</h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		addComponent(headerLabel);

		// add login form
		LoginForm loginForm = new LoginForm();
		loginForm.setUsernameCaption(ApplicationMessages.get().getString(
				Messages.TEXT_LABEL_USERNAME));
		loginForm.setUsernameInput(USERNAME_INPUT_NAME);
		loginForm.setPasswordCaption(ApplicationMessages.get().getString(
				Messages.TEXT_LABEL_PASSWORD));
		loginForm.setPasswordInput(PASSWORD_INPUT_NAME);
		loginForm.setLoginButtonCaption(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_LOGIN));

		loginForm.addListener((LoginForm.LoginListener) this);
		addComponent(loginForm);
	}

	/**
	 * User tries log into application
	 */
	public void onLogin(LoginForm.LoginEvent event) {
		try {
			if (instanceHandler != null) {
				T app = instanceHandler.getInstance();
				if (app != null) {
					User user = app.authenticate(
							event.getLoginParameter(USERNAME_INPUT_NAME),
							event.getLoginParameter(PASSWORD_INPUT_NAME));
					app.setCurrentUser(user);
					app.afterAuthentication(user);
					try {
						app.loadProtectedResources();
						open(new ExternalResource(app.getURL()));
					} catch (Throwable t) {
						showNotification(
								ApplicationMessages.get().getString(
										Messages.ERROR_LOAD_RESOURCE),
								t.getMessage(), Notification.TYPE_ERROR_MESSAGE);
					}
				}
			}
		} catch (Exception e) {
			if (instanceHandler != null) {
				T app = instanceHandler.getInstance();
				if (app != null) {
					app.setCurrentUser(null);
				}
				showNotification(
						ApplicationMessages.get()
								.getString(Messages.ERROR_LOGIN_FAILED),
						e.getMessage());
			}
		}
	}
}
