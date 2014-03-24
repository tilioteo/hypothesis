package org.hypothesis.application.manager.ui;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.application.ui.AbstractMainWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * The class represents the main ManagerApplication window
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class MainWindow extends AbstractMainWindow<ManagerApplication> {
	private static final long serialVersionUID = -3544212836020286383L;

	private Panel content;
	private HorizontalLayout menu;
	private Component userInfoLabel;

	// includable pages
	private ListPacks listPacks;
	private EditPersonalData editPersonalData;
	private EditGroups editGroups;
	private EditUsers editUsers;
	private EditPermissions editPermitions;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            - application
	 */
	public MainWindow(final ManagerApplication application) {
		super(application);
	}

	@Override
	protected void afterInit() {
		listPacks = new ListPacks();
		content.setContent(listPacks);
	}

	@Override
	protected Component createContent() {
		// main layout
		VerticalSplitPanel mainLayout = new VerticalSplitPanel();
		mainLayout.setMargin(true);
		mainLayout.setSizeFull();
		mainLayout.setSplitPosition(160, Sizeable.UNITS_PIXELS);
		mainLayout.setLocked(true);

		// page header
		VerticalLayout header = new VerticalLayout();
		header.setSpacing(true);
		header.setMargin(true);
		// user info
		userInfoLabel = userInfoLabel();
		header.addComponent(userInfoLabel);
		header.setComponentAlignment(userInfoLabel, Alignment.TOP_RIGHT);
		// heading
		Label headerLabel = new Label("<h1>"
				+ ApplicationMessages.get().getString(Messages.TEXT_APP_DESCRIPTION)
				+ "</h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		header.addComponent(headerLabel);
		// create menu
		setMenu();
		header.addComponent(menu);

		// main content
		content = new Panel();

		mainLayout.setFirstComponent(header);
		mainLayout.setSecondComponent(content);

		return mainLayout;
	}

	public void homepageButtonClick(ClickEvent event) {
		if (listPacks == null) {
			listPacks = new ListPacks();
		}
		content.setContent(listPacks);
	}

	@Override
	protected void init() {
		setCaption(ApplicationMessages.get().getString(Messages.TEXT_MANAGER_APP_TITLE));
	}

	public void logoutButtonClick(ClickEvent event) {
		this.getApp().closeApplication();
	}

	public void manageGroupsButtonClick(ClickEvent event) {
		if (editGroups == null) {
			editGroups = new EditGroups();
		}
		content.setContent(editGroups);
	}

	public void managePermitionsButtonClick(ClickEvent event) {
		if (editPermitions == null) {
			editPermitions = new EditPermissions(this.getApp());
		}
		content.setContent(editPermitions);
	}

	public void manageUsersButtonClick(ClickEvent event) {
		if (editUsers == null) {
			editUsers = new EditUsers();
		}
		content.setContent(editUsers);
	}

	public void refreshUserInfoLabel() {
		AbstractOrderedLayout parent = (AbstractOrderedLayout) userInfoLabel
				.getParent();
		Component newUserInfoLabel = userInfoLabel();
		parent.replaceComponent(userInfoLabel, newUserInfoLabel);
		userInfoLabel = newUserInfoLabel;
		parent.setComponentAlignment(userInfoLabel, Alignment.TOP_RIGHT);
	}

	/**
	 * Sets main window content
	 */
	protected void setMainContent(Component c) {
		content.removeAllComponents();
		content.addComponent(c);
	}

	/**
	 * Sets menu
	 */
	private void setMenu() {
		menu = new HorizontalLayout();
		menu.setSpacing(true);

		Button homepageButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_ENABLED_PACKS));
		Button settingsButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_CHANGE_USER_DATA));
		Button manageGroupsButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_MANAGE_GROUPS));
		Button manageUsersButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_MANAGE_USERS));
		Button managePermitionsButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_MANAGE_PERMITIONS));
		Button logoutButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_LOGOUT));

		homepageButton.addListener(ClickEvent.class, this,
				"homepageButtonClick");
		settingsButton.addListener(ClickEvent.class, this,
				"settingsButtonClick");
		manageGroupsButton.addListener(ClickEvent.class, this,
				"manageGroupsButtonClick");
		manageUsersButton.addListener(ClickEvent.class, this,
				"manageUsersButtonClick");
		managePermitionsButton.addListener(ClickEvent.class, this,
				"managePermitionsButtonClick");
		logoutButton.addListener(ClickEvent.class, this, "logoutButtonClick");

		menu.addComponent(homepageButton);
		menu.addComponent(settingsButton);
		menu.addComponent(manageGroupsButton);
		menu.addComponent(manageUsersButton);
		// menu.addComponent(managePermitionsButton);
		menu.addComponent(logoutButton);
	}

	public void settingsButtonClick(ClickEvent event) {
		if (editPersonalData == null) {
			editPersonalData = new EditPersonalData();
		}
		content.setContent(editPersonalData);
	}

	/**
	 * User information label
	 */
	private HorizontalLayout userInfoLabel() {
		HorizontalLayout labelLayout = new HorizontalLayout();
		labelLayout.setSpacing(true);

		Label usernameLabel = new Label();
		usernameLabel.setPropertyDataSource(getApp().getCurrentUserItem()
				.getItemProperty("username"));
		Label rolesLabel = new Label();
		rolesLabel.setPropertyDataSource(getApp().getCurrentUserItem()
				.getItemProperty("roles"));

		labelLayout.addComponent(new Label(ApplicationMessages.get().getString(
				Messages.TEXT_LOGGED_USER)));
		labelLayout.addComponent(usernameLabel);
		labelLayout.addComponent(rolesLabel);

		return labelLayout;
	}
}
