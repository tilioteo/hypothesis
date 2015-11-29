/**
 * 
 */
package com.tilioteo.hypothesis.presenter;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.model.User;
import com.tilioteo.hypothesis.data.service.UserService;
import com.tilioteo.hypothesis.event.interfaces.MainUIEvent;
import com.tilioteo.hypothesis.eventbus.MainEventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class UserSettingsWindowPresenter implements Serializable {

	private Window window;

	private UserService userService;
	private User user;

	private MainEventBus bus;

	private BeanFieldGroup<User> fieldGroup;

	@PropertyId("username")
	private TextField usernameField;

	@PropertyId("password")
	private TextField passwordField;

	public UserSettingsWindowPresenter(MainEventBus bus) {
		super();

		this.bus = bus;

		userService = UserService.newInstance();
	}

	private void buildContent() {
		VerticalLayout content = new VerticalLayout();
		window.setContent(content);

		content.addComponent(buildForm());
		content.addComponent(buildFooter());

		fieldGroup = new BeanFieldGroup<User>(User.class);
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(user);

	}

	private FormLayout buildForm() {
		FormLayout form = new FormLayout();

		usernameField = new TextField("Username");
		form.addComponent(usernameField);

		passwordField = new TextField("Password");
		form.addComponent(passwordField);

		return form;
	}

	private HorizontalLayout buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);

		Button ok = new Button("OK");
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fieldGroup.commit();
					User user = fieldGroup.getItemDataSource().getBean();
					userService.add(user);

					Notification success = new Notification("Profile updated successfully");
					success.setDelayMsec(2000);
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());

					bus.post(new MainUIEvent.ProfileUpdatedEvent());
					window.close();
				} catch (CommitException e) {
					Notification.show("Error while updating profile", Type.ERROR_MESSAGE);
				}

			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button("Storno");
		cancel.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				fieldGroup.discard();
				window.close();
			}
		});
		footer.addComponent(cancel);

		return footer;
	}

	private void createWindow() {
		window = new Window();
		// window.addCloseListener(this);
		window.setCloseShortcut(KeyCode.ESCAPE, null);
		window.setResizable(false);
		window.setClosable(false);
		// window.setWidth(50, Unit.PERCENTAGE);
		// window.setHeight(80.0f, Unit.PERCENTAGE);
		window.setModal(true);

		buildContent();
	}

	public void showWindow(User user) {
		this.user = user;

		createWindow();

		bus.post(new MainUIEvent.CloseOpenWindowsEvent());
		UI.getCurrent().addWindow(window);
		window.center();
		window.focus();
	}

}
