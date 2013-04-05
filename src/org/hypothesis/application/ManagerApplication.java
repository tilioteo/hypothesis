package org.hypothesis.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hypothesis.application.manager.ui.MainWindow;
import org.hypothesis.common.application.AbstractBaseApplication;
import org.hypothesis.common.application.ui.LoginWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.core.FieldConstants;
import org.hypothesis.core.UserGroupManager;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Role;
import org.hypothesis.entity.User;

import com.vaadin.Application;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author Kamil Morong - Hypothesis
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * 
 *         Application for managing user accounts
 * 
 */
public class ManagerApplication extends AbstractBaseApplication {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2877132159829330411L;

	private static ThreadLocal<ManagerApplication> currentApplication = new ThreadLocal<ManagerApplication>();

	/**
	 * Returns ManagerApplication application instance
	 * 
	 * @return an instance of the current application
	 */
	public static ManagerApplication getInstance() {
		return currentApplication.get();
	}

	private BeanItem<User> currentUserItem;
	private BeanContainer<Long, User> usersSource;
	private BeanContainer<Long, Group> groupsSource;
	private Table usersTable;

	private Table groupsTable;

	// public List<Pack> allPacks = getPermitionManager().findAllPacks();

	@Override
	public void afterAuthentication(User user) {
		currentUserItem = new BeanItem<User>(user);
	}

	@Override
	public User authenticate(String username, String password) throws Exception {
		User potentialUser = super.authenticate(username, password);
		// only superuser or manager can log into manager
		if (!isUserInRole(potentialUser, UserGroupManager.ROLE_SUPERUSER)
				&& !isUserInRole(potentialUser, UserGroupManager.ROLE_MANAGER)) {
			throw new Exception(ApplicationMessages.get().getString(
					Messages.ERROR_UNSUFFICIENT_RIGHTS_ADMIN));
		}

		return potentialUser;

		/*
		 * setCurrentUser(potentialUser); currentUserItem = new
		 * BeanItem<User>(potentialUser); loadProtectedResources();
		 */
	}

	/**
	 * The method ends the current user's session. It returns to the login
	 * window.
	 */
	public void closeApplication() {
		setCurrentUser(null);
		this.close();
	}

	/**
	 * Returns current authenticated users bean item
	 * 
	 * @return currentUserItem
	 */
	public BeanItem<User> getCurrentUserItem() {
		return currentUserItem;
	}

	public BeanContainer<Long, Group> getGroupsSource() {
		if (groupsSource == null) {
			groupsSource = new BeanContainer<Long, Group>(Group.class);
			getGroupsSource().setBeanIdProperty(FieldConstants.ID);
			List<Group> groups = new ArrayList<Group>();
			if (isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
				groups = getUserGroupManager().findAllGroups();
			} else {
				groups = getUserGroupManager()
						.findOwnerGroups(getCurrentUser());
			}
			getGroupsSource().addAll(groups);
		}
		return groupsSource;
	}

	public Table getGroupsTable() {
		if (groupsTable == null) {
			groupsTable = new Table();
			groupsTable.setContainerDataSource(getGroupsSource());
		}
		return groupsTable;
	}

	public BeanContainer<Long, User> getUsersSource() {
		if (usersSource == null) {
			usersSource = new BeanContainer<Long, User>(User.class);
			usersSource.setBeanIdProperty(FieldConstants.ID);
			List<User> users = new ArrayList<User>();
			if (isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
				users = getUserGroupManager().findAllUsers();
			} else {
				users = getUserGroupManager().findOwnerUsers(getCurrentUser());
			}
			getUsersSource().addAll(users);
		}
		return usersSource;
	}

	public Table getUsersTable() {
		if (usersTable == null) {
			usersTable = new Table();
			usersTable.setContainerDataSource(getUsersSource());
		}
		return usersTable;
	}

	/**
	 * Application initialization
	 */
	@Override
	public void init() {
		super.init();

		Window loginWindow = new LoginWindow<ManagerApplication>(
				new LoginWindow.InstanceHandler<ManagerApplication>() {
					public ManagerApplication getInstance() {
						return ManagerApplication.getInstance();
					}
				}, ApplicationMessages.get().getString(
						Messages.TEXT_MANAGER_LOGIN_TITLE), ApplicationMessages.get()
						.getString(Messages.TEXT_MANAGER_LOGIN_HEADER));

		setMainWindow(loginWindow);
	}

	/**
	 * Tests whether current user is in given role
	 * 
	 * @param rolename
	 * @return true, if current user is in given role, false otherwise
	 */
	public boolean isCurrentUserInRole(Role role) {
		return isUserInRole(getCurrentUser(), role);
	}

	/**
	 * Tests wheter given user is in the given role
	 * 
	 * @param User
	 *            user
	 * @param String
	 *            roleName
	 * @return true, if given user is in given role, false otherwise
	 */
	public boolean isUserInRole(User user, Role role) {
		Set<Role> roles = user.getRoles();
		for (Role role2 : roles) {
			if (role2.getName().equals(role.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A "hidden" class - only authenticated user can call the content.
	 * 
	 * @param String
	 *            login - user's login
	 * @param String
	 *            password - user's password
	 */
	@Override
	public void loadProtectedResources() {
		setMainWindow(new MainWindow(this));
	}

	@Override
	protected void onEndTransaction(Application application,
			Object transactionData) {
		if (application == ManagerApplication.this) {
			currentApplication.set(null);
			currentApplication.remove();
		}
	}

	@Override
	protected void onStartTransaction(Application application,
			Object transactionData) {
		if (application == ManagerApplication.this) {
			currentApplication.set(this);
		}
	}

	/**
	 * Refreshes information about current user
	 * 
	 * @param refreshedUserItem
	 */
	public void refreshCurrentUser(BeanItem<User> refreshedUserItem) {
		/*
		 * for (Object pid : currentUserItem.getItemPropertyIds()) { if
		 * (pid.equals(FieldConstants.ROLES)) { User user =
		 * currentUserItem.getBean(); for (Role role : user.getRoles()) {
		 * user.removeRole(role); } for (Role role :
		 * refreshedUserItem.getBean().getRoles()) { user.addRole(role); } }
		 * else if (!currentUserItem.getItemProperty(pid).isReadOnly()) {
		 * currentUserItem.getItemProperty(pid).setValue(
		 * refreshedUserItem.getItemProperty(pid).getValue()); } }
		 * setCurrentUser(currentUserItem.getBean());
		 */
		currentUserItem = refreshedUserItem;
		setCurrentUser(refreshedUserItem.getBean());
		((MainWindow) getMainWindow()).refreshUserInfoLabel();
	}

	public void refreshUserGroupSources() {
		usersSource = null;
		usersTable.setContainerDataSource(getUsersSource());
		groupsSource = null;
		groupsTable.setContainerDataSource(getGroupsSource());
	}

	/**
	 * Change the main window of the application to another one - called when a
	 * link is pressed
	 * 
	 * @param Window
	 *            newWindow - new window to be switched on
	 */
	public void switchToAnotherWindow(Window newWindow) {
		setMainWindow(newWindow);
	}

	/**
	 * Called when the user closes a window
	 * 
	 * @param event
	 *            - event containing
	 */
	public void windowClose(CloseEvent event) {
		// TODO Auto-generated method stub
		// boolean closed = true;
	}
}
