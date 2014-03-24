/**
 * 
 */
package org.hypothesis.common.i18n;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Locale strings for English
 */
public class Messages_en extends Messages {

	static final Object[][] contents_en = {
			{ TEXT_MANAGER_APP_TITLE, "Hypothesis manager" },
			{ TEXT_APP_TITLE, "Hypothesis" },
			{ TEXT_APP_DESCRIPTION, "Hypothesis testing" },
			{ TEXT_BUTTON_CHANGE_USER_DATA, "Change personal data" },
			{ TEXT_BUTTON_MANAGE_GROUPS, "Manage groups" },
			{ TEXT_BUTTON_MANAGE_USERS, "Manage users" },
			{ TEXT_BUTTON_MANAGE_PERMITIONS, "Manage permitions" },
			{ TEXT_BUTTON_LOGOUT, "Logout" },
			{ PATTERN_DATE_FORMAT, "yyyy.MM.dd" },
			{ TEXT_LOGGED_USER, "logged user:" },
			{ TEXT_BUTTON_SAVE, "Save" },
			{ TEXT_BUTTON_CANCEL, "Cancel" },
			{ TEXT_BUTTON_ADD, "Add" },
			{ TEXT_BUTTON_UPDATE, "Update" },
			{ TEXT_BUTTON_DELETE, "Delete" },
			{ TEXT_BUTTON_START, "Run the test" },
			{ TEXT_SELECTED, "Selected" },
			{ TEXT_ALL, "All" },
			{ TEXT_BUTTON_EXPORT, "Export" },
			{ ERROR_SAVE_FAILED, "Save failed" },
			{ ERROR_DELETE_FAILED, "Delete failed." },
			{ TEXT_YES, "Yes" },
			{ TEXT_NO, "No" },
			{ TEXT_ID, "ID" },
			{ TEXT_NAME, "Name" },
			{ TEXT_USER, "User" },
			{ TEXT_ROLES, "Roles" },
			{ TEXT_GROUPS, "Groups" },
			{ TEXT_ACTIVE, "Active" },
			{ TEXT_EXPIRE_DATE, "Expire date" },
			{ TEXT_NOTE, "Note" },
			{ TEXT_TEST, "Test" },
			{ TEXT_ENABLED_PACKS, "Enabled tests" },
			{ TEXT_ENABLED_GROUPS, "Enabled groups" },
			{ TEXT_DISABLED_USERS, "Disabled users" },
			{ TEXT_NAME_REQUIRED, "Please enter name." },
			{ TEXT_GENERATED_GROUP_REQUIRED,
					"Please enter group for name generator." },
			{ TEXT_GENERATED_COUNT_REQUIRED,
					"Please enter users count for name generator." },
			{ TEXT_PASSWORD_REQUIRED, "Please enter password." },
			{ TEXT_NAME_LENGTH_REQUIRED_FMT,
					"Name must be %d-%d characters length." },
			{ TEXT_GENERATED_GROUP_LENGTH_REQUIRED_FMT,
					"Generated group name must be %d-%d characters length." },
			{ TEXT_GENERATED_COUNT_INTEGER,
					"Generated users count must be number between 1 and 999." },
			{ TEXT_PASSWORD_LENGTH_REQUIRED_FMT,
					"Password must be %d-%d characters length." },
			{ TEXT_NOT_OWN_ANY_USERS, "You don't own any users." },
			{ TEXT_NOT_OWN_ANY_GROUPS, "You don't own any groups." },
			{ TEXT_GROUP_NAME_EXISTS, "Group name exists yet." },
			{ TEXT_USER_NAME_EXISTS, "User name exists yet." },
			{ TEXT_ROLE_REQUIRED, "Please select role." },
			{ TEXT_GROUP_REQUIRED, "Please add user to some of your groups." },
			{ TEXT_ERROR, "Error" },
			{ ERROR_APP_INITIALIZATION_FMT,
					"Application initialization failed. %s" },
			{ TEXT_CHECK_FOR_UPDATE,
					"Choose properties which you want to bulk update." },
			{ TEXT_TEST_FINISHED,
					"Test was finished, push button to close window" },
			{ TEXT_BUTTON_CLOSE_TEST, "Close" },

			{ ERROR_BAD_LOGIN, "Bad user or password!" },
			{ ERROR_NOT_ENABLED_USER, "Your account is disabled." },
			{ ERROR_UNSUFFICIENT_RIGHTS_ADMIN,
					"You do not have rights to access administration." },

			{ TEXT_LABEL_USERNAME, "Username" },
			{ TEXT_LABEL_PASSWORD, "Password" },
			{ TEXT_BUTTON_LOGIN, "Login" },
			{ TEXT_MANAGER_LOGIN_TITLE, "Hypothesis manager - Login" },
			{ TEXT_LOGIN_TITLE, "Hypothesis - Login" },
			{ TEXT_MANAGER_LOGIN_HEADER, "Hypothesis - user management" },
			{ TEXT_LOGIN_HEADER, "Hypothesis - testing" },
			{ ERROR_LOGIN_FAILED, "Login failed." },
			{ ERROR_LOAD_RESOURCE, "Loading resource causes an error." },

			{ TEXT_NEW_GROUP_TITLE, "New group" },
			{ TEXT_UPDATE_GROUP_TITLE, "Update group" },
			{ TEXT_UPDATE_GROUPS_TITLE, "Update groups" },
			{ INFO_GROUP_CREATED, "Group sucessfully created." },

			{ TEXT_NEW_USER_TITLE, "New user" },
			{ TEXT_UPDATE_USER_TITLE, "Update user" },
			{ TEXT_UPDATE_USERS_TITLE, "Update users" },
			{ TEXT_DISABLED_PACKS, "Disabled tests" },
			{ INFO_USER_CREATED, "User sucessfully created." },
			{ INFO_USERS_CREATED, "Users sucessfully created." },

			{ TEXT_EDIT_GROUPS_TITLE, "Manage groups" },
			{ TEXT_ADD_GROUP_TITLE, "Add group" },
			{ INFO_GROUP_SAVED, "Group sucessfully saved." },
			{ INFO_GROUP_DELETED, "Group sucessfully deleted." },
			{ INFO_GROUPS_DELETED, "Groups sucessfully deleted." },
			{ TEXT_TOTAL_USERS_FMT, "total users: %d" },
			{ TEXT_TOTAL_PACKS_FMT, "total tests: %d" },
			{ TEXT_NO_GROUPS_SELECTED, "There are no groups selected." },

			{ TEXT_EDIT_PERMISSIONS_TITLE, "Change permitions" },

			{ TEXT_EDIT_PERSONAL_DATA_TITLE, "Change personal data" },
			{ TEXT_EDIT_PERSONAL_DATA_INFO,
					"For personal data change fill new values and press \"Save\" button." },
			{ INFO_PERSONAL_DATA_SAVED, "Personal data sucessfully saved." },

			{ TEXT_EDIT_USERS_TITLE, "Manage users" },
			{ TEXT_EDIT_SELECTED_USER_TITLE, "Update selected user" },
			{ TEXT_EDIT_SELECTED_USERS_TITLE, "Update selected users" },
			{ TEXT_ADD_USER_TITLE, "Add user" },
			{ WARN_CREATE_GROUP_FIRST, "To add user, create group first" },
			{ INFO_USER_SAVED, "User sucessfully saved." },
			{ INFO_USER_DELETED, "User sucessfully deleted." },
			{ INFO_USERS_DELETED, "Users sucessfully deleted." },
			{ WARN_USER_ROLE_CHANGED, "Warning: Your role has been changed." },
			{ WARN_AT_LEAST_ONE_SU, "There must be at least one SUPERUSER." },
			{ WARN_ONLY_SU_CAN_DELETE_SU,
					"Only SUPERUSER can delete other SUPERUSER." },
			{ TEXT_TOTAL_GROUPS_FMT, "total groups: %d" },
			{ TEXT_NO_USERS_SELECTED, "There are no users selected." },
			{ TEXT_GENERATE, "generate according to pattern group-001-XXXX" },

			{
					TEXT_BASE_INFO,
					"Welcome to the Hypothesis application. Start by choosing any of the enabled tests below..." },
			{ TEXT_TEST_INFO, "Information about the test..." },

			{ TEXT_EXPORT_USERS_SHEET_NAME, "Hypothesis users" },
			{ TEXT_EXPORT_USERS_FILE_NAME, "hypothesis-users.xls" },
			{ TEXT_EXPORT_GROUPS_SHEET_NAME, "Hypothesis groups" },
			{ TEXT_EXPORT_GROUPS_FILE_NAME, "hypothesis-groups.xls" },
			{ ERROR_EXPORT_CANNOT_CREATE_FILE, "Failed to create export file." },
			{ ERROR_EXPORT_ROWS_LIMIT_EXCEEDED,
					"Excel table rows count limit exceeded." },
			{ ERROR_EXPORT_CANNOT_WRITE_TO_FILE, "Cannot write to export file." },

			{ TEXT_DELETE_CONFIRM, "Please, confirm delete" },
			{ TEXT_DELETE_SELECTED_USERS,
					"Do you really want to delete selected users?" },
			{ TEXT_DELETE_ALL_USERS, "Do you really want to delete all users?" },
			{ TEXT_DELETE_SELECTED_GROUPS,
					"Do you really want to delete selected groups?" },
			{ TEXT_DELETE_ALL_GROUPS,
					"Do you really want to delete all groups?" }, };

	@Override
	public Object[][] getContents() {
		return contents_en;
	}

}
