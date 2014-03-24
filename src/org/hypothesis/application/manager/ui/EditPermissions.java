package org.hypothesis.application.manager.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.GroupPermission;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.UserPermission;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

/**
 * The class represents permissions management view
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class EditPermissions extends VerticalLayout implements ClickListener,
		ValueChangeListener, ColumnGenerator {
	private static final long serialVersionUID = -7656364809373712179L;

	private final ManagerApplication managerApplication;
	private Table permissionsTable;
	private Form permissionForm;
	private Button saveButton;
	private Button deleteButton;
	public static final Object[] COLUMNS_ORDER = new String[] {
			FieldConstants.PACK, FieldConstants.ENABLED_GROUPS,
			FieldConstants.ENABLED_USERS, FieldConstants.DISABLED_USERS };
	public static final String[] COLUMN_HEADERS = new String[] {
			ApplicationMessages.get().getString(Messages.TEXT_TEST),
			ApplicationMessages.get().getString(Messages.TEXT_ENABLED_GROUPS),
			ApplicationMessages.get().getString(Messages.TEXT_ENABLED_PACKS),
			ApplicationMessages.get().getString(Messages.TEXT_DISABLED_USERS) };
	public static final List<String> VISIBLE_FIELDS = Arrays
			.asList(new String[] { FieldConstants.PACK,
					FieldConstants.ENABLED_GROUPS,
					FieldConstants.ENABLED_USERS, FieldConstants.DISABLED_USERS });

	/**
	 * Constructor
	 * 
	 * @param managerApplication
	 *            - application instance
	 */
	public EditPermissions(final ManagerApplication managerApplication) {
		this.managerApplication = managerApplication;

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(
						Messages.TEXT_EDIT_PERMISSIONS_TITLE) + "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		// main layout
		HorizontalLayout content = new HorizontalLayout();
		content.setWidth("100%");
		content.setSpacing(true);
		addComponent(content);

		// table of groups
		setPermissionsTable();
		content.addComponent(permissionsTable);

		// right part
		VerticalLayout rightContent = new VerticalLayout();
		rightContent.setWidth("100%");
		content.addComponent(rightContent);

		content.setExpandRatio(permissionsTable, 6.0f);
		content.setExpandRatio(rightContent, 4.0f);

		// group form, invisible by default
		setPermissionForm();
		permissionForm.setVisible(false);
		rightContent.addComponent(permissionForm);
	}

	/**
	 * Called when a Button has been clicked
	 * 
	 * @param event
	 *            - an event containing information about the click
	 */
	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		// save selected permission
		if (source == saveButton) {
			/*
			 * try { groupForm.commit();
			 * 
			 * BeanItem<Group> groupItem = (BeanItem<Group>)
			 * groupForm.getItemDataSource(); Group group = groupItem.getBean();
			 * 
			 * try { // add group managerApplication.getUserGroupManager().addGroup(group);
			 * 
			 * // add test permissions
			 * managerApplication.getPermissionManager().deleteGroupPermissions(group);
			 * Set<Test> enabledTests = (Set<Test>)
			 * groupForm.getField(FieldConstants.AVAILABLE_TESTS).getValue();
			 * for (Test enabledTest : enabledTests) { GroupPermition
			 * groupPermission = new GroupPermission();
			 * groupPermission.setGroup(group);
			 * groupPermission.setTest(enabledTest);
			 * managerApplication.getPermissionManager().addGroupPermission(groupPermission);
			 * }
			 * 
			 * groupsTable.sort();
			 * //groupsTable.setCurrentPageFirstItemId(group);
			 * 
			 * // managerApplication remove himself from edited group if
			 * (!managerApplication.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER) &&
			 * !group.getUsers().contains(managerApplication.getCurrentUser())) {
			 * groupsTable.removeItem(groupsTable.getValue());
			 * groupForm.setVisible(false); }
			 * 
			 * getWindow().showNotification(ApplicationMessages.get().getMessage(Messages
			 * .INFO_GROUP_SAVED)); } catch (HibernateException e) {
			 * getWindow().
			 * showNotification(ApplicationMessages.get().getMessage(Messages
			 * .ERROR_SAVE_FAILED), Notification.TYPE_WARNING_MESSAGE); } }
			 * catch (InvalidValueException e) { // TODO: zobrazit nejake
			 * varovani? }
			 */
		}

		// delete selected group
		else if (source == deleteButton) {
			/*
			 * Group group = (Group) groupsTable.getValue();
			 * 
			 * try { // TODO: vymazat nejak permissions automaticky pomoci
			 * kaskady, // aby se nemuselo delit na dve transakce
			 * managerApplication.getPermissionManager().deleteGroupPermissions(group);
			 * managerApplication.getUserGroupManager().deleteGroup(group);
			 * groupsTable.removeItem(groupsTable.getValue());
			 * groupForm.setVisible(false);
			 * getWindow().showNotification(ApplicationMessages
			 * .get().getMessage(Messages.INFO_GROUP_DELETED)); } catch
			 * (HibernateException e) {
			 * getWindow().showNotification(ApplicationMessages.
			 * get().getMessage(Messages.ERROR_DELETE_FAILED),
			 * Notification.TYPE_WARNING_MESSAGE); }
			 */
		}
	}

	/**
	 * Called by Table when a cell in a generated column needs to be generated.
	 * 
	 * @param source
	 *            - the source Table
	 * @param itemId
	 *            - the itemId (aka rowId) for the of the cell to be generated
	 * @param columnId
	 *            - the id for the generated column (as specified in
	 *            addGeneratedColumn)
	 */
	public Component generateCell(Table source, Object itemId, Object columnId) {
		// Group group = (Group) itemId;

		/*
		 * if (columnId.equals(FieldConstants.USERS)) { Set<User> users =
		 * group.getUsers(); String description = ""; for (User user : users) {
		 * description += user.getUsername() + "<br />"; } Label usersLabel =
		 * new Label(); usersLabel.setDescription(description); if (users.size()
		 * == 0) { usersLabel.setValue(""); } else if (users.size() < 5) {
		 * usersLabel.setValue(users.toString().substring(1,
		 * users.toString().length() - 1)); } else {
		 * usersLabel.setValue(String.format
		 * (ApplicationMessages.get().getMessage(Messages.TEXT_TOTAL_USERS_FMT),
		 * users.size())); } return usersLabel; }
		 * 
		 * else if (columnId.equals(FieldConstants.AVAILABLE_TESTS)) { Set<Test>
		 * tests = managerApplication.getPermissionManager().getGroupTests(group); String
		 * description = new Date().toString(); for (Test test : tests) {
		 * description += test.getName() + " - " + test.getDescription() +
		 * "<br />"; } Label testsLabel = new Label();
		 * testsLabel.setDescription(description); if (tests.size() == 0) {
		 * testsLabel.setValue(""); } else if (tests.size() < 5) {
		 * testsLabel.setValue(tests.toString().substring(1,
		 * tests.toString().length() - 1)); } else {
		 * testsLabel.setValue(String.format
		 * (ApplicationMessages.get().getMessage(Messages.TEXT_TOTAL_USERS_FMT),
		 * tests.size())); } return testsLabel; }
		 */

		return null;
	}

	/**
	 * Set the permission form
	 */
	public void setPermissionForm() {
		permissionForm = new Form();
		permissionForm.setWriteThrough(false);
		permissionForm.setInvalidCommitted(false);
		permissionForm.setFormFieldFactory(new PermitionFieldFactory(managerApplication));

		((HorizontalLayout) permissionForm.getFooter()).setSpacing(true);
		saveButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_SAVE));
		deleteButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_DELETE));
		saveButton.addListener((ClickListener) this);
		deleteButton.addListener((ClickListener) this);
		permissionForm.getFooter().addComponent(saveButton);
		permissionForm.getFooter().addComponent(deleteButton);
	}

	/**
	 * Set the table of permissions
	 */
	public void setPermissionsTable() {
		permissionsTable = new Table();
		permissionsTable.setSelectable(true);
		permissionsTable.setImmediate(true);
		permissionsTable.setWidth("100%");
		permissionsTable.setNullSelectionAllowed(false);
		permissionsTable.setColumnCollapsingAllowed(true);
		permissionsTable.addListener(this);

		// generated columns
		/*
		 * permitionsTable.addGeneratedColumn(FieldConstants.ENABLED_GROUPS,
		 * this);
		 * permitionsTable.addGeneratedColumn(FieldConstants.ENABLED_USERS,
		 * this);
		 * permitionsTable.addGeneratedColumn(FieldConstants.DISABLED_USERS,
		 * this);
		 */

		permissionsTable.addContainerProperty(FieldConstants.PACK, String.class,
				null);
		permissionsTable.addContainerProperty(FieldConstants.ENABLED_GROUPS,
				String.class, null);
		permissionsTable.addContainerProperty(FieldConstants.ENABLED_USERS,
				String.class, null);
		permissionsTable.addContainerProperty(FieldConstants.DISABLED_USERS,
				String.class, null);

		List<Pack> packs = managerApplication.getPermissionManager().findAllPacks();
		for (Pack pack : packs) {
			Set<GroupPermission> groupPermitions = managerApplication.getPermissionManager()
					.getPackGroupPermissions(pack);
			Set<UserPermission> enabledUserPermitions = managerApplication
					.getPermissionManager().getPackUserPermissions(pack, true);
			Set<UserPermission> disabledUserPermitions = managerApplication
					.getPermissionManager().getPackUserPermissions(pack, false);

			String gpString = "";
			for (GroupPermission gp : groupPermitions) {
				gpString += gp.getGroup().getName() + ", ";
			}

			String eupString = "";
			for (UserPermission eup : enabledUserPermitions) {
				eupString += eup.getUser().getUsername() + ", ";
			}

			String dupString = "";
			for (UserPermission dup : disabledUserPermitions) {
				dupString += dup.getUser().getUsername() + ", ";
			}

			Object[] item = new Object[] { pack.getName(), gpString, eupString,
					dupString };
			permissionsTable.addItem(item, pack);
		}

		/*
		 * // prepare data source Collection<Group> groups = null; if
		 * (managerApplication.isCurrentUserInRole(UserGroupManager.ROLE_SUPERUSER)) {
		 * groups = managerApplication.getUserGroupManager().findAllGroups(); } else if
		 * (managerApplication.isCurrentUserInRole(UserGroupManager.ROLE_MANAGER)) { Long
		 * userId = managerApplication.getCurrentUser().getId(); groups =
		 * managerApplication.getUserGroupManager().findUser(userId).getGroups(); }
		 */

		// set data source
		/*
		 * BeanItemContainer groupsSource = new BeanItemContainer(Group.class);
		 * for (Group group : groups) { groupsSource.addBean(group); }
		 * groupsTable.setContainerDataSource(groupsSource);
		 */
		// permitionsTable.setVisibleColumns(COLUMNS_ORDER);
		// permitionsTable.setColumnHeaders(COLUMN_HEADERS);

		permissionsTable.setSortContainerPropertyId(FieldConstants.PACK);
	}

	/**
	 * Called when a row in the table of permissions has been selected
	 * 
	 * @param event
	 *            - an event containing information about the selected value
	 */
	public void valueChange(ValueChangeEvent event) {
		if (event.getProperty() == permissionsTable) {
			if (permissionsTable.getValue() != null) {
				/*
				 * Group group = (Group) permitionsTable.getValue();
				 * 
				 * groupForm.setVisible(true); Item item =
				 * groupsTable.getItem(group);
				 * item.addItemProperty(FieldConstants.AVAILABLE_TESTS, new
				 * TwinColSelect());
				 * item.getItemProperty(FieldConstants.AVAILABLE_TESTS
				 * ).setValue(
				 * managerApplication.getPermitionManager().getGroupTests(group));
				 * groupForm.setItemDataSource(item);
				 * groupForm.setVisibleItemProperties(VISIBLE_FIELDS);
				 */
			}
		}
	}

}
