package org.hypothesis.application.hypothesis.ui;

import org.hypothesis.application.HypothesisApplication;
import org.hypothesis.common.application.ui.AbstractMainWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

/**
 * The class represents the main Hypothesis application window
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class MainWindow extends AbstractMainWindow<HypothesisApplication> implements
		ClickListener, Property.ValueChangeListener {
	private static final long serialVersionUID = 5165777893793175500L;

	private VerticalLayout content;
	// private HorizontalLayout menu;
	private Button stylerButton;
	private Button sessionButton;

	/*
	 * private Button logoutButton; private Button homepageButton; private
	 * Button settingsButton; private Button manageGroupsButton; private Button
	 * manageUsersButton; private Button managePermitionsButton;
	 */

	/**
	 * Constructor
	 * 
	 * @param application
	 *            - HypothesisApplication application object
	 */
	public MainWindow(HypothesisApplication application) {
		super(application);
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == stylerButton) {
			if (getApplication().getTheme() != "theme1") {
				getApplication().setTheme("theme1");
			} else {
				getApplication().setTheme("chameleon-vaadin");
			}
		}

		else if (source == sessionButton) {
			Window sessWindow = new Window();
			// sessWindow.setModal(true);
			sessWindow.setWidth(400, UNITS_PIXELS);
			sessWindow.setCaption("nastavení session");
			VerticalLayout vl = new VerticalLayout();
			sessWindow.addComponent(vl);

			Slider slider = new Slider("Zvolte dobu expirace (5 - 600 sekund)");
			slider.setWidth("100%");
			slider.setMin(5);
			slider.setMax(600);
			slider.setImmediate(true);
			slider.addListener(this);
			vl.addComponent(slider);

			addWindow(sessWindow);
		}
	}

	@Override
	protected Component createContent() {
		// main layout
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		// mainLayout.setSizeFull();

		HorizontalLayout hl = new HorizontalLayout();
		mainLayout.addComponent(hl);
		mainLayout.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		stylerButton = new Button("změnit styl");
		stylerButton.addListener((ClickListener) this);
		stylerButton.setStyleName(BaseTheme.BUTTON_LINK);
		hl.addComponent(stylerButton);
		hl.addComponent(new Label(" | "));
		sessionButton = new Button("nastavit session");
		sessionButton.addListener((ClickListener) this);
		sessionButton.setStyleName(BaseTheme.BUTTON_LINK);
		hl.addComponent(sessionButton);

		// heading
		Label headerLabel = new Label("<h1>"
				+ ApplicationMessages.get().getString(Messages.TEXT_APP_DESCRIPTION)
				+ "</h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(headerLabel);
		mainLayout.setComponentAlignment(headerLabel, Alignment.MIDDLE_CENTER);
		// user info
		// Component userInfoLabel = userInfoLabel();
		// header.addComponent(userInfoLabel);
		// create menu
		// setMenu();
		// header.addComponent(menu);

		// base info
		Label infoLabel = new Label(ApplicationMessages.get().getString(
				Messages.TEXT_BASE_INFO));
		mainLayout.addComponent(infoLabel);

		// main content
		content = new VerticalLayout();
		content.setMargin(true);

		content.addComponent(getListPacks());

		mainLayout.addComponent(content);

		return mainLayout;
	}

	/**
	 * The method uploads the graphical components into the window
	 */

	/**
	 * User information label
	 */
	/*
	 * private HorizontalLayout userInfoLabel() { HorizontalLayout labelLayout =
	 * new HorizontalLayout(); labelLayout.setSpacing(true);
	 * 
	 * Label usernameLabel = new Label();
	 * usernameLabel.setPropertyDataSource(application
	 * .getCurrentUserItem().getItemProperty("username")); Label rolesLabel =
	 * new Label();
	 * rolesLabel.setPropertyDataSource(application.getCurrentUserItem
	 * ().getItemProperty("roles"));
	 * 
	 * labelLayout.addComponent(new
	 * Label(ApplicationMessages.get().getMessage(Messages.TEXT_LOGGED_USER)));
	 * labelLayout.addComponent(usernameLabel);
	 * labelLayout.addComponent(rolesLabel);
	 * 
	 * return labelLayout; }
	 */

	/**
	 * Sets menu
	 */
	/*
	 * private void setMenu() { menu = new HorizontalLayout();
	 * menu.setSpacing(true);
	 * 
	 * homepageButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.TEXT_ENABLED_TESTS));
	 * settingsButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.TEXT_BUTTON_CHANGE_USER_DATA
	 * )); menu.addComponent(homepageButton); menu.addComponent(settingsButton);
	 * if (application.isCurrentUserInRole(UserGroupManager.ROLE_MANAGER) ||
	 * application.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
	 * manageGroupsButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.TEXT_BUTTON_MANAGE_GROUPS));
	 * manageUsersButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.TEXT_BUTTON_MANAGE_USERS));
	 * managePermitionsButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.
	 * TEXT_BUTTON_MANAGE_PERMITIONS)); menu.addComponent(manageGroupsButton);
	 * menu.addComponent(manageUsersButton);
	 * //menu.addComponent(managePermitionsButton); } logoutButton = new
	 * Button(ApplicationMessages.get().getMessage(Messages.TEXT_BUTTON_LOGOUT));
	 * menu.addComponent(logoutButton);
	 * 
	 * // buttons listeners logoutButton.addListener((ClickListener) this);
	 * homepageButton.addListener((ClickListener) this);
	 * settingsButton.addListener((ClickListener) this);
	 * manageGroupsButton.addListener((ClickListener) this);
	 * manageUsersButton.addListener((ClickListener) this);
	 * managePermitionsButton.addListener((ClickListener) this); }
	 */

	/**
	 * Called when a Button has been clicked
	 * 
	 * @param event
	 *            - an event containing information about the click
	 */
	/*
	 * public void buttonClick(ClickEvent event) { final Button source =
	 * event.getButton();
	 * 
	 * if (source == logoutButton) { application.closeApplication(); } else if
	 * (source == homepageButton) { setMainContent(getListTests()); } else if
	 * (source == settingsButton) { setMainContent(getEditPersonalData()); }
	 * else if (source == manageGroupsButton) { setMainContent(getEditGroups());
	 * } else if (source == manageUsersButton) { setMainContent(getEditUsers());
	 * } else if (source == managePermitionsButton) {
	 * setMainContent(getEditPermitions()); } }
	 */

	/**
	 * Includable pages getters
	 */
	public ListPacks getListPacks() {
		return new ListPacks(getApp());
	}

	//
	// public EditPersonalData getEditPersonalData()
	// {
	// // TODO: osetrit vymazani formulare a muze se pouzit zakomentovana cast
	// // (pomoci vhodneho listeneru?)
	// /*if (editPersonalData == null) {
	// editPersonalData = new EditPersonalData(application);
	// }
	// return editPersonalData;*/
	// return new EditPersonalData(application);
	// }
	//
	// public EditGroups getEditGroups()
	// {
	// /*if (editGroups == null) {
	// editGroups = new EditGroups(this);
	// }
	// return editGroups;*/
	// return new EditGroups(application);
	// }
	//
	// public EditUsers getEditUsers()
	// {
	// /*if (editUsers == null) {
	// editUsers = new EditUsers(this);
	// }
	// return editUsers;*/
	// return new EditUsers(application);
	// }
	//
	// public AddGroup getAddGroup()
	// {
	// // TODO: osetrit vymazani formulare a muze se pouzit zakomentovana cast
	// // (pomoci vhodneho listeneru?)
	// /*if (addGroup == null) {
	// addGroup = new AddGroup(application);
	// }
	// return addGroup;*/
	// return new AddGroup(application);
	// }
	//
	// public AddUser getAddUser()
	// {
	// // TODO: osetrit vymazani formulare a muze se pouzit zakomentovana cast
	// // (pomoci vhodneho listeneru?)
	// /*if (addUser == null) {
	// addUser = new AddUser(application);
	// }
	// return addUser;*/
	// return new AddUser(application);
	// }
	//
	// public EditPermitions getEditPermitions()
	// {
	// /*if (editPermitions == null) {
	// editPermitions = new EditPermitions(application);
	// }
	// return editPermitions;*/
	// return new EditPermitions(application);
	// }

	@Override
	protected void init() {
		// TODO change caption
		setCaption(ApplicationMessages.get().getString(Messages.TEXT_APP_TITLE));
	}

	public void valueChange(ValueChangeEvent event) {
		// -1 = never timeout
		int timeoutSeconds = ((Double) event.getProperty().getValue())
				.intValue();
		((WebApplicationContext) getApplication().getContext())
				.getHttpSession().setMaxInactiveInterval(timeoutSeconds);
		showNotification("Expirace session změněna", timeoutSeconds + " sekund");
	}

}
