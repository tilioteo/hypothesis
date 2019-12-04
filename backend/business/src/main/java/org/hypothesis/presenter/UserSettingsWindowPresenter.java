/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

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
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.server.Messages;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class UserSettingsWindowPresenter implements Serializable, HasMainEventBus {

    private final UserService userService;
    private Window window;
    private User user;

    private BeanFieldGroup<User> fieldGroup;

    @PropertyId("username")
    private TextField usernameField;

    @PropertyId("password")
    private TextField passwordField;

    public UserSettingsWindowPresenter() {
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
        ok.addClickListener(event -> {
            try {
                fieldGroup.commit();
                User user = fieldGroup.getItemDataSource().getBean();
                userService.add(user);

                Notification success = new Notification(Messages.getString("Message.Info.ProfileUpdated"));
                success.setDelayMsec(2000);
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());

                getBus().post(new MainUIEvent.ProfileUpdatedEvent());
                window.close();
            } catch (CommitException e) {
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
        window.addCloseShortcut(KeyCode.ESCAPE, (int[]) null);
        window.setResizable(false);
        window.setClosable(false);
        window.setModal(true);

        buildContent();
    }

    public void showWindow(User user) {
        this.user = user;

        createWindow();

        getBus().post(new MainUIEvent.CloseOpenWindowsEvent());
        UI.getCurrent().addWindow(window);
        window.center();
        window.focus();
    }

}
