/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.UserSettingsWindowPresenter;
import org.hypothesis.server.Messages;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalUIScoped
public class UserSettingsWindowPresenterImpl implements Serializable, UserSettingsWindowPresenter {

	private Window window;

	@Inject
	private UserService userService;
	private User user;

	@Inject
	private Event<MainUIEvent> mainEvent;

	private BeanFieldGroup<User> fieldGroup;

	@PropertyId("username")
	private TextField usernameField;

	@PropertyId("password")
	private TextField passwordField;

	private void buildContent() {
		VerticalLayout content = new VerticalLayout();
		window.setContent(content);

		content.addComponent(buildForm());
		content.addComponent(buildFooter());

		fieldGroup = new BeanFieldGroup<>(User.class);
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
		ok.addClickListener(e -> {
            try {
                fieldGroup.commit();
                userService.add(fieldGroup.getItemDataSource().getBean());

                Notification success = new Notification(Messages.getString("Message.Info.ProfileUpdated"));
                success.setDelayMsec(2000);
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());

                mainEvent.fire(new MainUIEvent.ProfileUpdatedEvent());
                window.close();
            } catch (CommitException ex) {
                Notification.show(Messages.getString("Message.Error.ProfileUpdate"), Type.ERROR_MESSAGE);
            }

        });
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
		cancel.addClickListener(e -> {
            fieldGroup.discard();
            window.close();
        });
		footer.addComponent(cancel);

		return footer;
	}

	private void createWindow() {
		window = new Window();
		window.addCloseShortcut(KeyCode.ESCAPE, null);
		window.setResizable(false);
		window.setClosable(false);
		window.setModal(true);

		buildContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.presenter.UserSettingsWindowPresenter#showWindow(org.
	 * hypothesis.data.model.User)
	 */
	@Override
	public void showWindow(User user) {
		this.user = user;

		createWindow();

		mainEvent.fire(new MainUIEvent.CloseOpenWindowsEvent());
		UI.getCurrent().addWindow(window);
		window.center();
		window.focus();
	}

}
