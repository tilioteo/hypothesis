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
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GroupForm extends Form {

	private static final long serialVersionUID = 4525394667393091173L;

	private Button saveButton;
	private Button cancelButton;
	private Set<Group> groups = new HashSet<Group>();

	public GroupForm(Set<Group> groups) {
		this.groups = groups;
		GridLayout formLayout = new GridLayout();
		formLayout.setMargin(true, false, true, false);
		formLayout.setSpacing(true);
		formLayout.setColumns(2);
		setLayout(formLayout);

		setFormFieldFactory(new GroupFieldFactory(groups));

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
		Field field = getFormFieldFactory().createField(this, propertyId, this);
		addField(propertyId, field);
	}

	@SuppressWarnings("unchecked")
	private void addUnboundField(Object propertyId, Object value) {
		addUnboundField(propertyId);

		if (value != null) {
			if (propertyId.equals(FieldConstants.USERS)) {
				for (User user : (Set<User>) value) {
					((AbstractSelect) getField(propertyId))
							.select(user.getId());
				}
			}

			else if (propertyId.equals(FieldConstants.AVAILABLE_PACKS)) {
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
		if (groups.size() > 1) {
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
		getLayout().addComponent(field);
	}

	public void setButtonClickListeners(Object target, String saveMethodName,
			String cancelMethodName) {
		saveButton.addListener(Button.ClickEvent.class, target, saveMethodName);
		cancelButton.addListener(Button.ClickEvent.class, target,
				cancelMethodName);
	}

	private void setFormFields() {
		// new group
		if (groups.isEmpty()) {
			addUnboundField(FieldConstants.NAME);
			addUnboundField(FieldConstants.USERS);
			addUnboundField(FieldConstants.NOTE);
			addUnboundField(FieldConstants.AVAILABLE_PACKS);
		}

		// single update
		else if (groups.size() == 1) {
			Group group = groups.iterator().next();
			addUnboundField(FieldConstants.ID, group.getId());
			addUnboundField(FieldConstants.NAME, group.getName());
			addUnboundField(FieldConstants.USERS, group.getUsers());
			addUnboundField(FieldConstants.NOTE, group.getNote());
			addUnboundField(FieldConstants.AVAILABLE_PACKS, ManagerApplication
					.getInstance().getPermitionManager().getGroupPacks(group));
		}

		// multiupdate
		else {
			addUnboundField(FieldConstants.USERS);
			addUnboundField(FieldConstants.NOTE);
			addUnboundField(FieldConstants.AVAILABLE_PACKS);
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
