/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.io.Serializable;

import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;

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
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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

		usernameField = new TextField(Messages.getString("Caption.Field.Username"));
		form.addComponent(usernameField);

		passwordField = new TextField(Messages.getString("Caption.Field.Password"));
		form.addComponent(passwordField);

		return form;
	}

	private HorizontalLayout buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);

		Button ok = new Button(Messages.getString("Caption.Button.OK"));
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fieldGroup.commit();
					User user = fieldGroup.getItemDataSource().getBean();
					userService.add(user);

					Notification success = new Notification(Messages.getString("Message.Info.ProfileUpdated"));
					success.setDelayMsec(2000);
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());

					bus.post(new MainUIEvent.ProfileUpdatedEvent());
					window.close();
				} catch (CommitException e) {
					Notification.show(Messages.getString("Message.Error.ProfileUpdate"), Type.ERROR_MESSAGE);
				}

			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
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
		window.addCloseShortcut(KeyCode.ESCAPE, null);
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
