package org.hypothesis.application.manager.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.UserGroupManager;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

/**
 * The class of group field factory
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class GroupFieldFactory extends DefaultFieldFactory {
	private static final long serialVersionUID = 465259291272826501L;

	private Set<Group> groups;

	public GroupFieldFactory(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		if (propertyId.equals(FieldConstants.ID)) {
			TextField field = new TextField(ApplicationMessages.get().getString(
					Messages.TEXT_ID));
			field.setReadOnly(true);
			return field;
		}

		else if (propertyId.equals(FieldConstants.NAME)) {
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
			field.addValidator(groupNameValidator());

			return field;
		}

		else if (propertyId.equals(FieldConstants.NOTE)) {
			TextField field = new TextField(ApplicationMessages.get().getString(
					Messages.TEXT_NOTE));
			field.setNullRepresentation("");
			return field;
		}

		else if (propertyId.equals(FieldConstants.USERS)) {
			TwinColSelect field = new TwinColSelect(ApplicationMessages.get()
					.getString(Messages.TEXT_USER));
			field.setMultiSelect(true);
			field.setImmediate(true);
			field.setRows(7);
			Collection<User> users;
			if (ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				users = ManagerApplication.getInstance().getUserGroupManager()
						.findAllUsers();
			} else {
				users = ManagerApplication.getInstance().getUserGroupManager()
						.findOwnerUsers(ManagerApplication.getInstance().getCurrentUser());
				field.setRequired(true);
				field.setRequiredError(ApplicationMessages.get().getString(
						Messages.TEXT_GROUP_REQUIRED));
			}
			if (users.isEmpty()) {
				TextField noUsersField = new TextField();
				noUsersField.setValue(ApplicationMessages.get().getString(
						Messages.TEXT_NOT_OWN_ANY_USERS));
				noUsersField.setReadOnly(true);
				return noUsersField;
			} else {
				BeanContainer<Long, User> usersSource = new BeanContainer<Long, User>(
						User.class);
				usersSource.setBeanIdProperty(FieldConstants.ID);
				usersSource.addAll(users);
				field.setContainerDataSource(usersSource);
				field.setItemCaptionPropertyId(FieldConstants.USERNAME);
				usersSource.sort(new Object[] { FieldConstants.USERNAME },
						new boolean[] { true });
				field.addListener(Property.ValueChangeEvent.class, this,
						"usersChange");
				return field;
			}
		}

		else if (propertyId.equals(FieldConstants.AVAILABLE_PACKS)) {
			TwinColSelect field = new TwinColSelect(ApplicationMessages.get()
					.getString(Messages.TEXT_ENABLED_PACKS));
			field.setMultiSelect(true);
			field.setImmediate(true);
			field.setRows(7);
			BeanContainer<Long, Pack> packsSource = new BeanContainer<Long, Pack>(
					Pack.class);
			packsSource.setBeanIdProperty(FieldConstants.ID);
			Collection<Pack> packs;
			if (ManagerApplication.getInstance().isCurrentUserInRole(
					UserGroupManager.ROLE_SUPERUSER)) {
				packs = ManagerApplication.getInstance().getPermitionManager()
						.findAllPacks();
			} else {
				packs = ManagerApplication
						.getInstance()
						.getPermitionManager()
						.findUserPacks2(ManagerApplication.getInstance().getCurrentUser(),
								false);
			}
			packsSource.addAll(packs);
			field.setContainerDataSource(packsSource);
			// field.setItemCaptionPropertyId(FieldConstants.NAME);
			for (Iterator<?> i = packsSource.getItemIds().iterator(); i
					.hasNext();) {
				Object id = i.next();
				String caption = String.format("%s (%d)", packsSource
						.getContainerProperty(id, FieldConstants.NAME)
						.getValue(),
						packsSource.getContainerProperty(id, FieldConstants.ID)
								.getValue());
				field.setItemCaption(id, caption);
			}
			packsSource.sort(new Object[] { FieldConstants.NAME },
					new boolean[] { true });
			field.addListener(Property.ValueChangeEvent.class, this,
					"packsChange");
			return field;
		}

		return super.createField(item, propertyId, uiContext);
	}

	public Validator groupNameValidator() {
		Validator validator = new Validator() {
			private static final long serialVersionUID = 2184520831123243334L;

			public boolean isValid(Object value) {
				if (groups.isEmpty()) {
					return !ManagerApplication.getInstance().getUserGroupManager()
							.groupNameExists(null, (String) value);
				} else {
					return !ManagerApplication
							.getInstance()
							.getUserGroupManager()
							.groupNameExists(groups.iterator().next().getId(),
									(String) value);
				}
			}

			public void validate(Object value) throws InvalidValueException {
				if (!isValid(value)) {
					throw new InvalidValueException(ApplicationMessages.get()
							.getString(Messages.TEXT_GROUP_NAME_EXISTS));
				}
			}
		};

		return validator;
	}

	@SuppressWarnings("unchecked")
	public void packsChange(Property.ValueChangeEvent event) {
		((BeanContainer<Long, Pack>) ((AbstractSelect) event.getProperty())
				.getContainerDataSource()).sort(
				new Object[] { FieldConstants.NAME }, new boolean[] { true });
	}

	@SuppressWarnings("unchecked")
	public void usersChange(Property.ValueChangeEvent event) {
		((BeanContainer<Long, User>) ((AbstractSelect) event.getProperty())
				.getContainerDataSource()).sort(
				new Object[] { FieldConstants.USERNAME },
				new boolean[] { true });
	}
}
