package org.hypothesis.application.manager.ui;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.core.FieldConstants;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.GroupPermition;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Window;

public class GroupWindow extends Window {

	private static final long serialVersionUID = 3435100355705244137L;

	private GroupForm groupForm;
	private Set<Group> groups = new HashSet<Group>();
	private boolean isNewGroup = false;
	private boolean isSaved = false;

	public GroupWindow() {
		super(ApplicationMessages.get().getString(Messages.TEXT_NEW_GROUP_TITLE));
		this.isNewGroup = true;
		Init();
	}

	public GroupWindow(Set<Group> groups) {
		super();
		if (groups.size() == 1) {
			setCaption(ApplicationMessages.get().getString(
					Messages.TEXT_UPDATE_GROUP_TITLE));
		} else {
			setCaption(ApplicationMessages.get().getString(
					Messages.TEXT_UPDATE_GROUPS_TITLE));
		}
		this.groups = groups;
		Init();
	}

	public void CancelButtonClick(Button.ClickEvent event) {
		groupForm.discard();
		groupForm.setValidationVisible(false);
		this.close();
	}

	public Group getGroup() {
		return groups.iterator().next();
	}

	private void Init() {
		this.setModal(true);
		this.setWidth(500, Sizeable.UNITS_PIXELS);

		if (groups.size() > 1) {
			this.addComponent(new Label(ApplicationMessages.get().getString(
					Messages.TEXT_CHECK_FOR_UPDATE)));
		}

		groupForm = new GroupForm(groups);
		groupForm.setButtonClickListeners(this, "SaveButtonClick",
				"CancelButtonClick");
		this.addComponent(groupForm);
	}

	public boolean isNewGroup() {
		return isNewGroup;
	}

	public boolean isSaved() {
		return isSaved;
	}

	@SuppressWarnings("unchecked")
	public void SaveButtonClick(Button.ClickEvent event) {
		try {
			groupForm.validate();
		} catch (InvalidValueException e) {
			return;
		}

		try {
			if (groups.isEmpty()) {
				Group group = new Group();
				group.setOwnerId(ManagerApplication.getInstance().getCurrentUser().getId());
				groups.add(group);
			}

			for (Group group : groups) {
				if (groupForm.getField(FieldConstants.NAME) != null
						&& groupForm.getField(FieldConstants.NAME).isEnabled()) {
					group.setName((String) groupForm.getField(
							FieldConstants.NAME).getValue());
				}

				if (groupForm.getField(FieldConstants.USERS).isEnabled()) {
					if (groupForm.getField(FieldConstants.USERS).getType() != String.class) {
						Set<User> groupUsers = new HashSet<User>(
								group.getUsers());
						for (User user : groupUsers) {
							group.removeUser(user);
							if (ManagerApplication.getInstance().getUsersSource()
									.getItem(user.getId()) != null) {
								ManagerApplication.getInstance().getUsersSource()
										.getItem(user.getId()).getBean()
										.removeGroup(group);
							}
						}
						Set<Long> userIds = (Set<Long>) groupForm.getField(
								FieldConstants.USERS).getValue();
						for (Long userId : userIds) {
							User user = ((BeanContainer<Long, User>) ((TwinColSelect) groupForm
									.getField(FieldConstants.USERS))
									.getContainerDataSource()).getItem(userId)
									.getBean();
							group.addUser(user);
							if (ManagerApplication.getInstance().getUsersSource()
									.getItem(user.getId()) != null) {
								ManagerApplication.getInstance().getUsersSource()
										.getItem(user.getId()).getBean()
										.addGroup(group);
							}
						}
					}
				}

				if (groupForm.getField(FieldConstants.NOTE).isEnabled()) {
					group.setNote((String) groupForm.getField(
							FieldConstants.NOTE).getValue());
				}

				ManagerApplication.getInstance().getUserGroupManager().addGroup(group);

				if (groupForm.getField(FieldConstants.AVAILABLE_PACKS)
						.isEnabled()) {
					ManagerApplication.getInstance().getPermitionManager()
							.deleteGroupPermitions(group);
					Set<Long> availablePackIds = (Set<Long>) groupForm
							.getField(FieldConstants.AVAILABLE_PACKS)
							.getValue();
					for (Long availablePackId : availablePackIds) {
						Pack pack = ((BeanContainer<Long, Pack>) ((TwinColSelect) groupForm
								.getField(FieldConstants.AVAILABLE_PACKS))
								.getContainerDataSource()).getItem(
								availablePackId).getBean();
						GroupPermition groupPermition = new GroupPermition(
								group, pack);
						ManagerApplication.getInstance().getPermitionManager()
								.addGroupPermition(groupPermition);
					}
				}
			}

			isSaved = true;
			this.close();
		} catch (HibernateException e) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(Messages.ERROR_SAVE_FAILED),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
