package org.hypothesis.application.manager.ui;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Role;
import org.hypothesis.entity.User;
import org.hypothesis.entity.UserPermition;
import org.hypothesis.persistence.UserGroupManager;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Window;

public class UserWindow extends Window {
	private static final long serialVersionUID = -5522000731468211992L;

	private UserForm userForm;
	private Set<User> users = new HashSet<User>();
	private boolean isNewUser = false;
	private boolean isSaved = false;
	private User changedCurrentUser = null;
	private boolean isCurrentUserRoleChanged = false;

	public UserWindow() {
		super(ApplicationMessages.get().getString(Messages.TEXT_NEW_USER_TITLE));
		this.isNewUser = true;
		Init();
	}

	public UserWindow(Set<User> users) {
		super();
		if (users.size() == 1) {
			setCaption(ApplicationMessages.get().getString(
					Messages.TEXT_UPDATE_USER_TITLE));
		} else {
			setCaption(ApplicationMessages.get().getString(
					Messages.TEXT_UPDATE_USERS_TITLE));
		}
		this.users = users;
		Init();
	}

	public void CancelButtonClick(Button.ClickEvent event) {
		userForm.discard();
		userForm.setValidationVisible(false);
		this.close();
	}

	private String generateString(String characters, int length) {
		Random rng = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	public int getUsersCount() {
		return users.size();
	}

	private void Init() {
		this.setModal(true);
		this.setWidth(500, Sizeable.UNITS_PIXELS);

		if (users.size() > 1) {
			this.addComponent(new Label(ApplicationMessages.get().getString(
					Messages.TEXT_CHECK_FOR_UPDATE)));
		}

		userForm = new UserForm(users);
		userForm.setButtonClickListeners(this, "SaveButtonClick",
				"CancelButtonClick");
		this.addComponent(userForm);
	}

	public boolean isCurrentUserRoleChanged() {
		return isCurrentUserRoleChanged;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public boolean isSaved() {
		return isSaved;
	}

	@SuppressWarnings("unchecked")
	public void SaveButtonClick(Button.ClickEvent event) {
		try {
			userForm.validate();
		} catch (InvalidValueException e) {
			return;
		}

		try {
			if (users.isEmpty()) {
				User user = new User();
				user.setOwnerId(ManagerApplication.getInstance().getCurrentUser().getId());
				users.add(user);
			}

			if (userForm.getField(FieldConstants.GENERATED_NAME_GROUP) != null
					&& userForm.getField(FieldConstants.GENERATED_NAME_GROUP)
							.isEnabled()
					&& userForm.getField(FieldConstants.GENERATED_NAME_COUNT) != null
					&& userForm.getField(FieldConstants.GENERATED_NAME_COUNT)
							.isEnabled()) {
				users.clear();
				String usernameGroup = (String) userForm.getField(
						FieldConstants.GENERATED_NAME_GROUP).getValue();
				int count = Integer.valueOf((String) userForm.getField(
						FieldConstants.GENERATED_NAME_COUNT).getValue());
				for (int index = 1; index <= count; index++) {
					User user = new User();
					user.setUsername(String.format("%s-%03d-%s", usernameGroup,
							index,
							generateString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 4)));
					user.setPassword(generateString(
							"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
							8));
					user.setOwnerId(ManagerApplication.getInstance().getCurrentUser()
							.getId());
					users.add(user);
				}
			}

			for (User user : users) {
				if (user.equals(ManagerApplication.getInstance().getCurrentUser())) {
					this.changedCurrentUser = user;
				}

				if (userForm.getField(FieldConstants.USERNAME) != null
						&& userForm.getField(FieldConstants.USERNAME)
								.isEnabled()) {
					user.setUsername((String) userForm.getField(
							FieldConstants.USERNAME).getValue());
				}

				if (userForm.getField(FieldConstants.PASSWORD) != null
						&& userForm.getField(FieldConstants.PASSWORD)
								.isEnabled()) {
					user.setPassword((String) userForm.getField(
							FieldConstants.PASSWORD).getValue());
				}

				if (userForm.getField(FieldConstants.ROLES).isEnabled()) {
					Set<Role> userRoles = new HashSet<Role>(user.getRoles());
					for (Role role : userRoles) {
						user.removeRole(role);
					}
					for (Role role : (Set<Role>) userForm.getField(
							FieldConstants.ROLES).getValue()) {
						user.addRole(role);
					}
				}

				if (userForm.getField(FieldConstants.GROUPS).isEnabled()) {
					if (userForm.getField(FieldConstants.GROUPS).getType() != String.class) {
						Set<Group> userGroups = new HashSet<Group>(
								user.getGroups());
						for (Group group : userGroups) {
							user.removeGroup(group);
							if (ManagerApplication.getInstance().getGroupsSource()
									.getItem(group.getId()) != null) {
								ManagerApplication.getInstance().getGroupsSource()
										.getItem(group.getId()).getBean()
										.removeUser(user);
							}
						}
						Set<Long> groupIds = (Set<Long>) userForm.getField(
								FieldConstants.GROUPS).getValue();
						for (Long groupId : groupIds) {
							Group group = ((BeanContainer<Long, Group>) ((TwinColSelect) userForm
									.getField(FieldConstants.GROUPS))
									.getContainerDataSource()).getItem(groupId)
									.getBean();
							user.addGroup(group);
							if (ManagerApplication.getInstance().getGroupsSource()
									.getItem(group.getId()) != null) {
								ManagerApplication.getInstance().getGroupsSource()
										.getItem(group.getId()).getBean()
										.addUser(user);
							}
						}
					}
				}

				if (userForm.getField(FieldConstants.ENABLED).isEnabled()) {
					user.setEnabled((Boolean) userForm.getField(
							FieldConstants.ENABLED).getValue());
				}

				if (userForm.getField(FieldConstants.EXPIRE_DATE).isEnabled()) {
					user.setExpireDate((Date) userForm.getField(
							FieldConstants.EXPIRE_DATE).getValue());
				}

				if (userForm.getField(FieldConstants.NOTE).isEnabled()) {
					user.setNote((String) userForm
							.getField(FieldConstants.NOTE).getValue());
				}

				ManagerApplication.getInstance().getUserGroupManager().addUser(user);

				if (userForm.getField(FieldConstants.ENABLED_PACKS).isEnabled()) {
					ManagerApplication.getInstance().getPermitionManager()
							.deleteUserPermitions(user, true);
					Set<Long> enabledPackIds = (Set<Long>) userForm.getField(
							FieldConstants.ENABLED_PACKS).getValue();
					for (Long enabledPackId : enabledPackIds) {
						Pack pack = ((BeanContainer<Long, Pack>) ((TwinColSelect) userForm
								.getField(FieldConstants.ENABLED_PACKS))
								.getContainerDataSource()).getItem(
								enabledPackId).getBean();
						UserPermition userPermition = new UserPermition(user,
								pack);
						ManagerApplication.getInstance().getPermitionManager()
								.addUserPermition(userPermition);
					}
				}
				if (userForm.getField(FieldConstants.DISABLED_PACKS)
						.isEnabled()) {
					ManagerApplication.getInstance().getPermitionManager()
							.deleteUserPermitions(user, false);
					Set<Long> disabledPackIds = (Set<Long>) userForm.getField(
							FieldConstants.DISABLED_PACKS).getValue();
					for (Long disabledPackId : disabledPackIds) {
						Pack pack = ((BeanContainer<Long, Pack>) ((TwinColSelect) userForm
								.getField(FieldConstants.DISABLED_PACKS))
								.getContainerDataSource()).getItem(
								disabledPackId).getBean();
						UserPermition userPermition = new UserPermition(user,
								pack, false);
						ManagerApplication.getInstance().getPermitionManager()
								.addUserPermition(userPermition);
					}
				}

				ManagerApplication.getInstance().getUsersSource().addBean(user);
				// ManagerApplication.getInstance().getUsersTable().setValue(null);
				// usersTable.select(user.getId());
				// usersTable.setCurrentPageFirstItemId(user.getId());
			}

			isSaved = true;
			this.close();

			if (changedCurrentUser != null) {
				if (userForm.getField(FieldConstants.ROLES).isEnabled()) {
					Set<Role> oldRoles = ManagerApplication.getInstance().getCurrentUser()
							.getRoles();
					Set<Role> newRoles = (Set<Role>) userForm.getField(
							FieldConstants.ROLES).getValue();

					if (!oldRoles.equals(newRoles)) {
						// S/M -> U degradation
						if (!newRoles.contains(UserGroupManager.ROLE_MANAGER)
								&& !newRoles
										.contains(UserGroupManager.ROLE_SUPERUSER)) {
							ManagerApplication.getInstance().closeApplication();
							// TODO: nejake varovani pred odhlasenim?
						}

						// S -> M degradation
						else if (oldRoles
								.contains(UserGroupManager.ROLE_SUPERUSER)
								&& !newRoles
										.contains(UserGroupManager.ROLE_SUPERUSER)) {
							ManagerApplication.getInstance().refreshCurrentUser(
									new BeanItem<User>(changedCurrentUser));
							ManagerApplication.getInstance().refreshUserGroupSources();
						}

						isCurrentUserRoleChanged = true;
					}
				}
			}
		} catch (HibernateException e) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(Messages.ERROR_SAVE_FAILED),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
