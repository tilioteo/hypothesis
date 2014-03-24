package org.hypothesis.application.manager.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class UserForm extends Form {

	private static final long serialVersionUID = -3618139816212065399L;

	private Button saveButton;
	private Button cancelButton;
	private Set<User> users = new HashSet<User>();

	public UserForm(Set<User> users) {
		this.users = users;
		GridLayout formLayout = new GridLayout();
		formLayout.setMargin(true, false, true, false);
		formLayout.setSpacing(true);
		formLayout.setColumns(2);
		setLayout(formLayout);

		setFormFieldFactory(new UserFieldFactory(users));

		setWriteThrough(false);
		setInvalidCommitted(false);

		((HorizontalLayout) getFooter()).setSpacing(true);
		saveButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_SAVE));
		cancelButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_CANCEL));
		getFooter().addComponent(saveButton);
		getFooter().addComponent(cancelButton);

		setFormFields();
	}

	private void addUnboundField(Object propertyId) {
		addField(propertyId,
				getFormFieldFactory().createField(this, propertyId, this));
	}

	@SuppressWarnings("unchecked")
	private void addUnboundField(Object propertyId, Object value) {
		addUnboundField(propertyId);

		if (value != null) {
			if (propertyId.equals(FieldConstants.GROUPS)) {
				for (Group group : (Set<Group>) value) {
					((AbstractSelect) getField(propertyId)).select(group
							.getId());
				}
			}

			else if (propertyId.equals(FieldConstants.ENABLED_PACKS)) {
				for (Pack pack : (Set<Pack>) value) {
					((AbstractSelect) getField(propertyId))
							.select(pack.getId());
				}
			}

			else if (propertyId.equals(FieldConstants.DISABLED_PACKS)) {
				for (Pack pack : (Set<Pack>) value) {
					((AbstractSelect) getField(propertyId))
							.select(pack.getId());
				}
			}

			else {
				boolean wasReadOnly = getField(propertyId).isReadOnly();
				getField(propertyId).setReadOnly(false);
				getField(propertyId).setValue(value);
				getField(propertyId).setReadOnly(wasReadOnly);
			}
		}
	}

	@Override
	protected void attachField(Object propertyId, final Field field) {
		if (propertyId.equals(FieldConstants.GENERATED_NAME_GROUP)
				|| propertyId.equals(FieldConstants.GENERATED_NAME_COUNT)) {
			return;
		}

		if (users.size() > 1) {
			final CheckBox enabler = new CheckBox(field.getCaption());
			enabler.setImmediate(true);
			enabler.addListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 7183751823085105031L;

				public void valueChange(Property.ValueChangeEvent event) {
					field.setEnabled(enabler.booleanValue());
					((AbstractField) field).setValidationVisible(enabler
							.booleanValue());
				}
			});
			field.setEnabled(false);
			getLayout().addComponent(enabler);
		} else {
			getLayout().addComponent(new Label(field.getCaption()));
		}

		field.setCaption(null);
		if (propertyId.equals(FieldConstants.USERNAME) && users.isEmpty()) {
			getLayout().addComponent(getUsernameGenerator(field));
		} else {
			getLayout().addComponent(field);
		}
	}

	private Component getUsernameGenerator(Field field) {
		VerticalLayout vl = new VerticalLayout();

		vl.addComponent(field);

		// TODO: popisek/navod
		final HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(getField(FieldConstants.GENERATED_NAME_GROUP));
		hl.addComponent(new Label("-"));
		hl.addComponent(getField(FieldConstants.GENERATED_NAME_COUNT));
		hl.addComponent(new Label("-XXXX"));
		hl.setVisible(false);
		vl.addComponent(hl);

		final CheckBox generator = new CheckBox(ApplicationMessages.get().getString(
				Messages.TEXT_GENERATE));
		generator.setImmediate(true);
		generator.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 7183751823085105031L;

			public void valueChange(Property.ValueChangeEvent event) {
				getField(FieldConstants.USERNAME).setEnabled(
						!generator.booleanValue());
				((AbstractField) getField(FieldConstants.USERNAME))
						.setValidationVisible(!generator.booleanValue());
				getField(FieldConstants.USERNAME).setVisible(
						!generator.booleanValue());
				getField(FieldConstants.PASSWORD).setEnabled(
						!generator.booleanValue());
				((AbstractField) getField(FieldConstants.PASSWORD))
						.setValidationVisible(!generator.booleanValue());
				getField(FieldConstants.GENERATED_NAME_GROUP).setEnabled(
						generator.booleanValue());
				((AbstractField) getField(FieldConstants.GENERATED_NAME_GROUP))
						.setValidationVisible(generator.booleanValue());
				getField(FieldConstants.GENERATED_NAME_COUNT).setEnabled(
						generator.booleanValue());
				((AbstractField) getField(FieldConstants.GENERATED_NAME_COUNT))
						.setValidationVisible(generator.booleanValue());
				hl.setVisible(generator.booleanValue());
			}
		});

		vl.addComponent(generator);
		return vl;
	}

	public void setButtonClickListeners(Object target, String saveMethodName,
			String cancelMethodName) {
		saveButton.addListener(Button.ClickEvent.class, target, saveMethodName);
		cancelButton.addListener(Button.ClickEvent.class, target,
				cancelMethodName);
	}

	private void setFormFields() {
		// new user
		if (users.isEmpty()) {
			addUnboundField(FieldConstants.GENERATED_NAME_GROUP);
			addUnboundField(FieldConstants.GENERATED_NAME_COUNT);
			addUnboundField(FieldConstants.USERNAME);
			addUnboundField(FieldConstants.PASSWORD);
			addUnboundField(FieldConstants.ROLES);
			addUnboundField(FieldConstants.GROUPS);
			addUnboundField(FieldConstants.ENABLED);
			addUnboundField(FieldConstants.EXPIRE_DATE);
			addUnboundField(FieldConstants.NOTE);
			addUnboundField(FieldConstants.ENABLED_PACKS);
			addUnboundField(FieldConstants.DISABLED_PACKS);
		}

		// single update
		else if (users.size() == 1) {
			User user = users.iterator().next();
			addUnboundField(FieldConstants.ID, user.getId());
			addUnboundField(FieldConstants.USERNAME, user.getUsername());
			addUnboundField(FieldConstants.PASSWORD, user.getPassword());
			addUnboundField(FieldConstants.ROLES, user.getRoles());
			addUnboundField(FieldConstants.GROUPS, user.getGroups());
			addUnboundField(FieldConstants.ENABLED, user.getEnabled());
			addUnboundField(FieldConstants.EXPIRE_DATE, user.getExpireDate());
			addUnboundField(FieldConstants.NOTE, user.getNote());
			addUnboundField(FieldConstants.ENABLED_PACKS, ManagerApplication.getInstance()
					.getPermissionManager().getUserPacks(user, true, null));
			addUnboundField(
					FieldConstants.DISABLED_PACKS,
					ManagerApplication.getInstance().getPermissionManager()
							.getUserPacks(user, false, null));
		}

		// multiupdate
		else {
			addUnboundField(FieldConstants.ROLES);
			addUnboundField(FieldConstants.GROUPS);
			addUnboundField(FieldConstants.ENABLED);
			addUnboundField(FieldConstants.EXPIRE_DATE);
			addUnboundField(FieldConstants.NOTE);
			addUnboundField(FieldConstants.ENABLED_PACKS);
			addUnboundField(FieldConstants.DISABLED_PACKS);
		}
	}

	@Override
	public void validate() {
		try {
			for (Iterator<?> i = getItemPropertyIds().iterator(); i.hasNext();) {
				Object fieldId = i.next();
				if (getField(fieldId).isEnabled()) {
					getField(fieldId).validate();
				}
			}
			// setValidationVisible(false);
		} catch (InvalidValueException e) {
			// setValidationVisible(true);
			setComponentError(new UserError(e.getMessage()));
			throw e;
		}
	}
}
