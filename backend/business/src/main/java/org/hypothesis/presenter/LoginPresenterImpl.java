/**
 * 
 */
package org.hypothesis.presenter;

import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.InvalidLoginEvent;
import org.hypothesis.event.interfaces.MainUIEvent.InvalidUserPermissionEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.LoginPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.LoginScreen;
import org.vaadin.special.data.EmptyValidator;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class LoginPresenterImpl implements LoginPresenter {

	private TextField username;
	private PasswordField password;

	private MainEventBus bus;

	public LoginPresenterImpl(MainEventBus bus) {
		this.bus = bus;
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
	public Component buildLoginForm() {
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");

		loginPanel.addComponent(buildLanguageLinks());

		loginPanel.addComponent(buildLabels());
		loginPanel.addComponent(buildFields());
		// loginPanel.addComponent(new CheckBox("Remember me", true));
		loginPanel.addComponent(buildPublicAccessButton());
		return loginPanel;
	}

	private Component buildLanguageLinks() {
		CssLayout links = new CssLayout();
		links.addStyleName("v-component-group");

		Link english = new Link(null, new ExternalResource("?lang=en"));
		english.setIcon(new ThemeResource("img/en.png"));
		links.addComponent(english);

		Link czech = new Link(null, new ExternalResource("?lang=cs"));
		czech.setIcon(new ThemeResource("img/cs.png"));
		links.addComponent(czech);

		return links;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label welcome = new Label(Messages.getString("Caption.Login.Welcome"));
		welcome.setSizeUndefined();
		welcome.addStyleName(ValoTheme.LABEL_H4);
		welcome.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(welcome);

		Label title = new Label(Messages.getString("Caption.Login.HypothesisPlatform"));
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		return labels;
	}

	private Component buildFields() {
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");

		username = new TextField(Messages.getString("Caption.Login.Username"));
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		username.addValidator(new EmptyValidator(Messages.getString("Caption.Login.EmptyUserName")));
		username.setValidationVisible(false);
		username.setImmediate(true);
		username.focus();

		password = new PasswordField(Messages.getString("Caption.Login.Password"));
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		password.addValidator(new EmptyValidator(Messages.getString("Caption.Login.EmptyPassword")));
		password.setValidationVisible(false);
		password.setImmediate(true);

		final Button signin = new Button(Messages.getString("Caption.Login.Button.Login"));
		signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signin.setClickShortcut(KeyCode.ENTER);

		fields.addComponents(username, password, signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

		signin.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				boolean valid = true;
				try {
					username.validate();
				} catch (InvalidValueException e) {
					valid = false;
				}
				try {
					password.validate();
				} catch (InvalidValueException e) {
					valid = false;
				}

				if (!valid) {
					username.setValidationVisible(true);
					password.setValidationVisible(true);
				} else {
					bus.post(new MainUIEvent.UserLoginRequestedEvent(username.getValue(), password.getValue()));
				}
			}
		});
		return fields;
	}

	private Component buildPublicAccessButton() {
		Button button = new Button(Messages.getString("Caption.Login.Button.Guest"));
		button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		button.setIcon(FontAwesome.ARROW_CIRCLE_RIGHT);
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				bus.post(new MainUIEvent.GuestAccessRequestedEvent());
			}
		});
		return button;
	}

	private void clearFields() {
		username.setValidationVisible(false);
		username.setValue("");

		password.setValidationVisible(false);
		password.setValue("");

	}

	private void showError(String message) {
		Notification notification = new Notification(message, Type.ERROR_MESSAGE);
		notification.setDelayMsec(1000);
		notification.show(Page.getCurrent());
	}

	@Handler
	public void invalidLogin(InvalidLoginEvent event) {
		clearFields();
		showError(Messages.getString("Message.Error.InvalidLogin"));
	}

	@Handler
	public void invalidUserPermission(InvalidUserPermissionEvent event) {
		clearFields();
		showError(Messages.getString("Message.Error.AccessDenied"));
	}

	@Override
	public void refreshLoginForm() {
		password.clear();
	}

	public LoginScreen createScreen() {
		return new LoginScreen(this);
	}

}
