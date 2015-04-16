package com.tilioteo.hypothesis.ui.view;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.data.EmptyValidator;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.InvalidLoginEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.InvalidUserPermissionEvent;
import com.tilioteo.hypothesis.event.HypothesisEventBus;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout {
	
	private TextField username;
	private PasswordField password;

	public LoginView() {
		setSizeFull();
		
		HypothesisEventBus.register(this);

		Component loginForm = buildLoginForm();
		addComponent(loginForm);
		setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
	}

	private Component buildLoginForm() {
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");

		loginPanel.addComponent(buildLabels());
		loginPanel.addComponent(buildFields());
		//loginPanel.addComponent(new CheckBox("Remember me", true));
		loginPanel.addComponent(buildPublicAccessButton());
		return loginPanel;
	}

	private Component buildPublicAccessButton() {
		Button button = new Button("Click here to continue as guest.");
		button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		button.setIcon(FontAwesome.ARROW_CIRCLE_RIGHT);
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				HypothesisEventBus.post(new HypothesisEvent.GuestAccessRequestedEvent());
			}
		});
		return button;
	}

	private Component buildFields() {
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");

		username = new TextField("Username");
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		username.addValidator(new EmptyValidator("Fill in username"));
		username.setValidationVisible(false);
		username.setImmediate(true);

		password = new PasswordField("Password");
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		password.addValidator(new EmptyValidator("Fill in password"));
		password.setValidationVisible(false);
		password.setImmediate(true);

		final Button signin = new Button("Sign In");
		signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signin.setClickShortcut(KeyCode.ENTER);
		signin.focus();

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
					HypothesisEventBus.post(new HypothesisEvent.UserLoginRequestedEvent(username.getValue(), password.getValue()));
				}
			}
		});
		return fields;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label welcome = new Label("Welcome");
		welcome.setSizeUndefined();
		welcome.addStyleName(ValoTheme.LABEL_H4);
		welcome.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(welcome);

		Label title = new Label("Hypothesis platform");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		return labels;
	}

	private void clearFields() {
		username.setValidationVisible(false);
		username.setValue("");
		
		password.setValidationVisible(false);
		password.setValue("");
		
	}
	
	private void showError(String message) {
		Notification notification = new Notification(message,Type.ERROR_MESSAGE);
		notification.setDelayMsec(1000);
		notification.show(Page.getCurrent());
	}

	@Subscribe
	public void invalidLogin(InvalidLoginEvent event) {
		clearFields();
		showError("Invalid username or password.");
	}
	
	@Subscribe
	public void invalidUserPermission(InvalidUserPermissionEvent event) {
		clearFields();
		showError("User access denied.");
	}

}
