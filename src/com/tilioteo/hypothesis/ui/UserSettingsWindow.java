package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.persistence.UserManager;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class UserSettingsWindow extends Window {
	
	private final BeanFieldGroup<User> fieldGroup;
	private UserManager userManager;

	@PropertyId("username")
    private TextField usernameField;
    @PropertyId("password")
    private TextField passwordField;

	public UserSettingsWindow(User user) {
        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);

        VerticalLayout content = new VerticalLayout();
        setContent(content);
        
        content.addComponent(buildForm());
        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<User>(User.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(user);
        
        userManager = UserManager.newInstance();
	}

	private Component buildForm() {
		FormLayout form = new FormLayout();
		
		usernameField = new TextField("Username");
		form.addComponent(usernameField);
	    
		passwordField = new TextField("Password");
	    form.addComponent(passwordField);
	    
	    return form;
	}

	private Component buildFooter() {
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
                    userManager.add(user);

                    Notification success = new Notification(
                            "Profile updated successfully");
                    success.setDelayMsec(2000);
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    MainEventBus.get().post(new HypothesisEvent.ProfileUpdatedEvent());
                    close();
                } catch (CommitException e) {
                    Notification.show("Error while updating profile",
                            Type.ERROR_MESSAGE);
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
				close();
			}
        });
        footer.addComponent(cancel);
        
        return footer;
	}
	
	public static void open(final User user) {
        MainEventBus.get().post(new HypothesisEvent.CloseOpenWindowsEvent());
		Window window = new UserSettingsWindow(user);
		UI.getCurrent().addWindow(window);
		window.focus();
	}

}
