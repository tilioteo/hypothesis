package org.hypothesis.application.manager.ui;

import org.hypothesis.application.ManagerApplication;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

/**
 * The class of user field factory
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
class PermitionFieldFactory extends DefaultFieldFactory {
	private static final long serialVersionUID = 8222193587141256634L;

	/*
	 * private final ManagerApplication manager; private User user; private TwinColSelect
	 * enabledTestsField; private TwinColSelect disabledTestsField;
	 */

	public PermitionFieldFactory(final ManagerApplication managerApplication) {
		// this.manager = manager;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		/*
		 * user = ((BeanItem<User>) item).getBean();
		 * 
		 * if (FieldConstants.USERNAME.equals(propertyId)) { TextField field =
		 * new TextField(ApplicationMessages.get().getMessage(Messages.TEXT_NAME));
		 * field.setNullRepresentation(""); field.setRequired(true);
		 * field.setRequiredError
		 * (ApplicationMessages.get().getMessage(Messages.TEXT_NAME_REQUIRED));
		 * field.addValidator(new StringLengthValidator(
		 * String.format(ApplicationMessages
		 * .get().getMessage(Messages.TEXT_NAME_LENGTH_REQUIRED_FMT), 4, 30), 4,
		 * 30, false)); field.addValidator(getUsernameValidator()); return
		 * field; }
		 * 
		 * else if (FieldConstants.PASSWORD.equals(propertyId)) { TextField
		 * field = new
		 * TextField(ApplicationMessages.get().getMessage(Messages.TEXT_PASSWORD));
		 * field.setNullRepresentation(""); field.setSecret(true);
		 * field.setRequired(true);
		 * field.setRequiredError(ApplicationMessages.get().getMessage
		 * (Messages.TEXT_PASSWORD_REQUIRED)); field.addValidator(new
		 * StringLengthValidator(
		 * String.format(ApplicationMessages.get().getMessage(Messages
		 * .TEXT_PASSWORD_LENGTH_REQUIRED_FMT), 4, 30, false)); return field; }
		 * 
		 * else if (FieldConstants.NOTE.equals(propertyId)) { TextField field =
		 * new TextField(ApplicationMessages.get().getMessage(Messages.TEXT_NOTE));
		 * field.setNullRepresentation(""); return field; }
		 * 
		 * else if (FieldConstants.ENABLED.equals(propertyId)) { CheckBox field
		 * = new CheckBox(ApplicationMessages.get().getMessage(Messages.TEXT_ACTIVE));
		 * return field; }
		 * 
		 * else if (FieldConstants.EXPIRE_DATE.equals(propertyId)) {
		 * PopupDateField field = new
		 * PopupDateField(ApplicationMessages.get().getMessage
		 * (Messages.TEXT_EXPIRE_DATE));
		 * field.setResolution(PopupDateField.RESOLUTION_DAY);
		 * field.setDateFormat
		 * (ApplicationMessages.get().getMessage(Messages.PATTERN_DATE_FORMAT)); return
		 * field; }
		 * 
		 * else if (FieldConstants.ROLES.equals(propertyId)) { OptionGroup field
		 * = new OptionGroup(ApplicationMessages.get().getMessage(Messages.TEXT_ROLES));
		 * field.setMultiSelect(true);
		 * 
		 * List<Role> roles = manager.getUserGroupManager().findAllRoles();
		 * BeanItemContainer<Role> rolesSource = new
		 * BeanItemContainer<Role>(roles);
		 * field.setContainerDataSource(rolesSource);
		 * field.setItemCaptionPropertyId(FieldConstants.NAME); if
		 * (!manager.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
		 * field.setItemEnabled(UserGroupManager.ROLE_SUPERUSER, false); }
		 * 
		 * field.setRequired(true);
		 * field.setRequiredError(ApplicationMessages.get().getMessage
		 * (Messages.TEXT_ROLE_REQUIRED)); if (user.getId() != null &&
		 * manager.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
		 * field.addValidator(getRoleValidator()); } return field; }
		 * 
		 * else if (FieldConstants.GROUPS.equals(propertyId)) { TwinColSelect
		 * field = new
		 * TwinColSelect(ApplicationMessages.get().getMessage(Messages.TEXT_GROUPS));
		 * field.setMultiSelect(true);
		 * 
		 * Collection<Group> groups; if
		 * (manager.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
		 * groups = manager.getUserGroupManager().findAllGroups(); } else { //
		 * TODO: nacist skupiny primo z databaze??? groups =
		 * manager.getCurrentUser().getGroups(); field.setRequired(true);
		 * field.setRequiredError
		 * (ApplicationMessages.get().getMessage(Messages.TEXT_GROUP_REQUIRED)); } if
		 * (groups.isEmpty()) { return null; } BeanItemContainer<Group>
		 * groupsSource = new BeanItemContainer<Group>(groups);
		 * field.setContainerDataSource(groupsSource);
		 * field.setItemCaptionPropertyId(FieldConstants.NAME);
		 * 
		 * return field; }
		 * 
		 * else if (FieldConstants.ENABLED_TESTS.equals(propertyId)) {
		 * enabledTestsField = new
		 * TwinColSelect(ApplicationMessages.get().getMessage(Messages
		 * .TEXT_ENABLED_TESTS)); enabledTestsField.setMultiSelect(true);
		 * 
		 * List<Test> tests = manager.getPermitionManager().findAllTests();
		 * BeanItemContainer<Test> enabledTestsSource = new
		 * BeanItemContainer<Test>(tests);
		 * enabledTestsField.setContainerDataSource(enabledTestsSource);
		 * enabledTestsField.setItemCaptionPropertyId(FieldConstants.NAME);
		 * enabledTestsField.setImmediate(true);
		 * enabledTestsField.addListener(this);
		 * 
		 * return enabledTestsField; }
		 * 
		 * else if (FieldConstants.DISABLED_TESTS.equals(propertyId)) {
		 * disabledTestsField = new
		 * TwinColSelect(ApplicationMessages.get().getMessage(Messages
		 * .TEXT_DISABLED_TESTS)); disabledTestsField.setMultiSelect(true);
		 * 
		 * List<Test> tests = manager.getPermitionManager().findAllTests();
		 * BeanItemContainer<Test> enabledTestsSource = new
		 * BeanItemContainer<Test>(tests);
		 * disabledTestsField.setContainerDataSource(enabledTestsSource);
		 * disabledTestsField.setItemCaptionPropertyId(FieldConstants.NAME);
		 * disabledTestsField.setImmediate(true);
		 * disabledTestsField.addListener(this);
		 * 
		 * return disabledTestsField; }
		 */

		return super.createField(item, propertyId, uiContext);
	}

}
