package org.hypothesis.application.manager.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Role;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.UserGroupManager;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

/**
 * The class of user field factory
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class UserFieldFactory extends DefaultFieldFactory {
	private static final long serialVersionUID = 465259291272826501L;

	private Set<User> users;

	private TwinColSelect enabledPacksField;

	private TwinColSelect disabledPacksField;

	public UserFieldFactory(Set<User> users) {
		this.users = users;
	}

	public UserFieldFactory(User user) {
		users = new HashSet<User>();
		users.add(user);
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		if (propertyId.equals(FieldConstants.ID)) {
			TextField field = new TextField(ApplicationMessages.get().getString(
					Messages.TEXT_ID));
			field.setReadOnly(true);
			return field;
		}

		else if (propertyId.equals(FieldConstants.USERNAME)) {
			TextField field = new TextField(ApplicationMessages.get().getString(
					Messages.TEXT_NAME));
			field.setMaxLength(30);
			field.setNullRepresentation("");
			field.setRequired(true);
			field.setRequiredError(ApplicationMessages.get().getString(
					Messages.TEXT_NAME_REQUIRED));
			field.addValidator(new StringLengthValidator(String.format(
					ApplicationMessages.get().getString(
							Messages.TEXT_NAME_LENGTH_REQUIRED_FMT), 4, 30), 4,
					30, false));
			field.addValidator(usernameValidator());
			return field;
		}

		else if (propertyId.equals(FieldConstants.GENERATED_NAME_GROUP)) {
			TextField field = new TextField();
			field.setMaxLength(30);
			field.setNullRepresentation("");
			field.setWidth(120, Sizeable.UNITS_PIXELS);
			field.setRequired(true);
			field.setRequiredError(ApplicationMessages.get().getString(
					Messages.TEXT_GENERATED_GROUP_REQUIRED));
			// TODO: kontrola na unikatnost skupiny
			field.addValidator(new StringLengthValidator(String.format(
					ApplicationMessages.get().getString(
							Messages.TEXT_NAME_LENGTH_REQUIRED_FMT), 4, 30), 4,
					30, false));
			field.setEnabled(false);
			return field;
		}

		else if (propertyId.equals(FieldConstants.GENERATED_NAME_COUNT)) {
			TextField field = new TextField();
			field.setMaxLength(3);
			field.setNullRepresentation("");
			field.setWidth(40, Sizeable.UNITS_PIXELS);
			field.setRequired(true);
			field.setRequiredError(ApplicationMessages.get().getString(
					Messages.TEXT_GENERATED_COUNT_REQUIRED));
			field.addValidator(new RegexpValidator("[0-9]{1,3}", ApplicationMessages
					.get().getString(Messages.TEXT_GENERATED_COUNT_INTEGER)));
			field.setEnabled(false);
			return field;
		}

		else if (propertyId.equals(FieldConstants.PASSWORD)) {
			PasswordField field = new PasswordField(ApplicationMessages.get()
					.getString(Messages.TEXT_LABEL_PASSWORD));
			field.setMaxLength(30);
			field.setNullRepresentation("");
			field.setRequired(true);
			field.setRequiredError(ApplicationMessages.get().getString(
					Messages.TEXT_PASSWORD_REQUIRED));
			field.addValidator(new StringLengthValidator(
					String.format(
							ApplicationMessages.get().getString(
									Messages.TEXT_PASSWORD_LENGTH_REQUIRED_FMT),
							4, 30), 4, 30, false));
			return field;
		}

		else if (propertyId.equals(FieldConstants.ROLES)) {
			OptionGroup field = new OptionGroup(ApplicationMessages.get().getString(
					Messages.TEXT_ROLES));
			field.setMultiSelect(true);
			BeanItemContainer<Role> rolesSource = new BeanItemContainer<Role>(
					Role.class);
			rolesSource.addAll(ManagerApplication.getInstance().getUserGroupManager()
					.findAllRoles());
			rolesSource.sort(new Object[] { FieldConstants.ID },
					new boolean[] { true });
			field.setContainerDataSource(rolesSource);
			field.setItemCaptionPropertyId(FieldConstants.NAME);
			if (!ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				field.select(UserGroupManager.ROLE_USER);
				field.setReadOnly(true);
			}
			field.setRequired(true);
			field.setRequiredError(ApplicationMessages.get().getString(
					Messages.TEXT_ROLE_REQUIRED));
			if (!users.isEmpty()
					&& ManagerApplication.getInstance().isCurrentUserInRole(
							UserGroupManager.ROLE_SUPERUSER)) {
				field.addValidator(getRoleValidator());
			}
			return field;
		}

		else if (propertyId.equals(FieldConstants.GROUPS)) {
			TwinColSelect field = new TwinColSelect(ApplicationMessages.get()
					.getString(Messages.TEXT_GROUPS));
			field.setMultiSelect(true);
			field.setImmediate(true);
			field.setRows(7);
			Collection<Group> groups;
			if (ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				groups = ManagerApplication.getInstance().getUserGroupManager()
						.findAllGroups();
			} else {
				groups = ManagerApplication
						.getInstance()
						.getUserGroupManager()
						.findOwnerGroups(ManagerApplication.getInstance().getCurrentUser());
				field.setRequired(true);
				field.setRequiredError(ApplicationMessages.get().getString(
						Messages.TEXT_GROUP_REQUIRED));
			}
			if (groups.isEmpty()) {
				TextField noGroupsField = new TextField();
				noGroupsField.setValue(ApplicationMessages.get().getString(
						Messages.TEXT_NOT_OWN_ANY_GROUPS));
				noGroupsField.setReadOnly(true);
				return noGroupsField;
			} else {
				BeanContainer<Long, Group> groupsSource = new BeanContainer<Long, Group>(
						Group.class);
				groupsSource.setBeanIdProperty(FieldConstants.ID);
				groupsSource.addAll(groups);
				field.setContainerDataSource(groupsSource);
				field.setItemCaptionPropertyId(FieldConstants.NAME);
				groupsSource.sort(new Object[] { FieldConstants.NAME },
						new boolean[] { true });
				field.addListener(Property.ValueChangeEvent.class, this,
						"groupsChange");
				return field;
			}
		}

		else if (propertyId.equals(FieldConstants.ENABLED)) {
			CheckBox field = new CheckBox(ApplicationMessages.get().getString(
					Messages.TEXT_ACTIVE));
			return field;
		}

		else if (propertyId.equals(FieldConstants.EXPIRE_DATE)) {
			PopupDateField field = new PopupDateField(ApplicationMessages.get()
					.getString(Messages.TEXT_EXPIRE_DATE));
			field.setResolution(DateField.RESOLUTION_DAY);
			field.setDateFormat(ApplicationMessages.get().getString(
					Messages.PATTERN_DATE_FORMAT));
			return field;
		}

		else if (propertyId.equals(FieldConstants.NOTE)) {
			TextField field = new TextField(ApplicationMessages.get().getString(
					Messages.TEXT_NOTE));
			field.setNullRepresentation("");
			return field;
		}

		else if (propertyId.equals(FieldConstants.ENABLED_PACKS)) {
			enabledPacksField = new TwinColSelect(ApplicationMessages.get().getString(
					Messages.TEXT_ENABLED_PACKS));
			enabledPacksField.setMultiSelect(true);
			enabledPacksField.setImmediate(true);
			enabledPacksField.setRows(7);
			BeanContainer<Long, Pack> enabledPacksSource = new BeanContainer<Long, Pack>(
					Pack.class);
			Collection<Pack> packs;
			if (ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				packs = ManagerApplication.getInstance().getPermissionManager()
						.findAllPacks();
			} else {
				packs = ManagerApplication
						.getInstance()
						.getPermissionManager()
						.findUserPacks2(ManagerApplication.getInstance().getCurrentUser(),
								false);
			}
			enabledPacksSource.setBeanIdProperty(FieldConstants.ID);
			enabledPacksSource.addAll(packs);
			enabledPacksField.setContainerDataSource(enabledPacksSource);
			enabledPacksField.setItemCaptionPropertyId(FieldConstants.NAME);
			enabledPacksSource.sort(new Object[] { FieldConstants.NAME },
					new boolean[] { true });
			enabledPacksField.addListener(Property.ValueChangeEvent.class,
					this, "enabledPacksChange");
			return enabledPacksField;
		}

		else if (propertyId.equals(FieldConstants.DISABLED_PACKS)) {
			disabledPacksField = new TwinColSelect(ApplicationMessages.get().getString(
					Messages.TEXT_DISABLED_PACKS));
			disabledPacksField.setMultiSelect(true);
			disabledPacksField.setImmediate(true);
			disabledPacksField.setRows(7);
			BeanContainer<Long, Pack> disabledPacksSource = new BeanContainer<Long, Pack>(
					Pack.class);
			Collection<Pack> packs;
			if (ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				packs = ManagerApplication.getInstance().getPermissionManager()
						.findAllPacks();
			} else {
				packs = ManagerApplication
						.getInstance()
						.getPermissionManager()
						.findUserPacks2(ManagerApplication.getInstance().getCurrentUser(),
								false);
			}
			disabledPacksSource.setBeanIdProperty(FieldConstants.ID);
			disabledPacksSource.addAll(packs);
			disabledPacksField.setContainerDataSource(disabledPacksSource);
			disabledPacksField.setItemCaptionPropertyId(FieldConstants.NAME);
			disabledPacksSource.sort(new Object[] { FieldConstants.NAME },
					new boolean[] { true });
			disabledPacksField.addListener(Property.ValueChangeEvent.class,
					this, "disabledPacksChange");
			return disabledPacksField;
		}

		return super.createField(item, propertyId, uiContext);
	}

	@SuppressWarnings("unchecked")
	public void disabledPacksChange(Property.ValueChangeEvent event) {
		if (enabledPacksField != null) {
			for (Long id : (Set<Long>) event.getProperty().getValue()) {
				enabledPacksField.unselect(id);
			}
			((BeanContainer<Long, Pack>) disabledPacksField
					.getContainerDataSource()).sort(
					new Object[] { FieldConstants.NAME },
					new boolean[] { true });
		}
	}

	@SuppressWarnings("unchecked")
	public void enabledPacksChange(Property.ValueChangeEvent event) {
		if (disabledPacksField != null) {
			for (Long id : (Set<Long>) event.getProperty().getValue()) {
				disabledPacksField.unselect(id);
			}
			((BeanContainer<Long, Pack>) enabledPacksField
					.getContainerDataSource()).sort(
					new Object[] { FieldConstants.NAME },
					new boolean[] { true });
		}
	}

	public Validator getRoleValidator() {
		Validator validator = new Validator() {
			private static final long serialVersionUID = 5175794424220548942L;

			@SuppressWarnings("unchecked")
			public boolean isValid(Object value) {
				if (!users.contains(ManagerApplication.getInstance().getCurrentUser())) {
					return true;
				} else {
					if (((Set<Role>) value)
							.contains(UserGroupManager.ROLE_SUPERUSER)) {
						return true;
					} else {
						return ManagerApplication
								.getInstance()
								.getUserGroupManager()
								.anotherSuperuserExists(
										ManagerApplication.getInstance().getCurrentUser()
												.getId());
					}
				}
			}

			public void validate(Object value) throws InvalidValueException {
				if (!isValid(value)) {
					throw new InvalidValueException(ApplicationMessages.get()
							.getString(Messages.WARN_AT_LEAST_ONE_SU));
				}
			}
		};

		return validator;
	}

	@SuppressWarnings("unchecked")
	public void groupsChange(Property.ValueChangeEvent event) {
		((BeanContainer<Long, Group>) ((AbstractSelect) event.getProperty())
				.getContainerDataSource()).sort(
				new Object[] { FieldConstants.NAME }, new boolean[] { true });
	}

	public Validator usernameValidator() {
		Validator validator = new Validator() {
			private static final long serialVersionUID = -5612892495819788062L;

			public boolean isValid(Object value) {
				if (users.isEmpty()) {
					return !ManagerApplication.getInstance().getUserGroupManager()
							.usernameExists(null, (String) value);
				} else {
					return !ManagerApplication
							.getInstance()
							.getUserGroupManager()
							.usernameExists(users.iterator().next().getId(),
									(String) value);
				}
			}

			public void validate(Object value) throws InvalidValueException {
				if (!isValid(value)) {
					throw new InvalidValueException(ApplicationMessages.get()
							.getString(Messages.TEXT_USER_NAME_EXISTS));
				}
			}
		};

		return validator;
	}
}
