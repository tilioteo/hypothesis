/**
 * 
 */
package org.hypothesis.common.i18n;

import java.util.ListResourceBundle;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Message constants for i18n locales
 */
public class Messages extends ListResourceBundle {

	// general
	public static final String TEXT_MANAGER_APP_TITLE = generateId();
	public static final String TEXT_APP_TITLE = generateId();
	public static final String TEXT_APP_DESCRIPTION = generateId();
	public static final String TEXT_BUTTON_CHANGE_USER_DATA = generateId();
	public static final String TEXT_BUTTON_MANAGE_GROUPS = generateId();
	public static final String TEXT_BUTTON_MANAGE_USERS = generateId();
	public static final String TEXT_BUTTON_MANAGE_PERMITIONS = generateId();
	public static final String TEXT_BUTTON_LOGOUT = generateId();
	public static final String PATTERN_DATE_FORMAT = generateId();
	public static final String TEXT_LOGGED_USER = generateId();
	public static final String TEXT_BUTTON_SAVE = generateId();
	public static final String TEXT_BUTTON_CANCEL = generateId();
	public static final String TEXT_BUTTON_ADD = generateId();
	public static final String TEXT_BUTTON_UPDATE = generateId();
	public static final String TEXT_BUTTON_DELETE = generateId();
	public static final String TEXT_BUTTON_START = generateId();
	public static final String TEXT_SELECTED = generateId();
	public static final String TEXT_ALL = generateId();
	public static final String TEXT_BUTTON_EXPORT = generateId();
	public static final String ERROR_SAVE_FAILED = generateId();
	public static final String ERROR_DELETE_FAILED = generateId();
	public static final String TEXT_YES = generateId();
	public static final String TEXT_NO = generateId();
	public static final String TEXT_ID = generateId();
	public static final String TEXT_NAME = generateId();
	public static final String TEXT_USER = generateId();
	public static final String TEXT_ROLES = generateId();
	public static final String TEXT_GROUPS = generateId();
	public static final String TEXT_ACTIVE = generateId();
	public static final String TEXT_EXPIRE_DATE = generateId();
	public static final String TEXT_NOTE = generateId();
	public static final String TEXT_TEST = generateId();
	public static final String TEXT_ENABLED_PACKS = generateId();
	public static final String TEXT_ENABLED_GROUPS = generateId();
	public static final String TEXT_DISABLED_USERS = generateId();
	public static final String TEXT_NAME_REQUIRED = generateId();
	public static final String TEXT_GENERATED_GROUP_REQUIRED = generateId();
	public static final String TEXT_GENERATED_COUNT_REQUIRED = generateId();
	public static final String TEXT_PASSWORD_REQUIRED = generateId();
	public static final String TEXT_NAME_LENGTH_REQUIRED_FMT = generateId();
	public static final String TEXT_GENERATED_GROUP_LENGTH_REQUIRED_FMT = generateId();
	public static final String TEXT_GENERATED_COUNT_INTEGER = generateId();
	public static final String TEXT_PASSWORD_LENGTH_REQUIRED_FMT = generateId();
	public static final String TEXT_NOT_OWN_ANY_USERS = generateId();
	public static final String TEXT_NOT_OWN_ANY_GROUPS = generateId();
	public static final String TEXT_GROUP_NAME_EXISTS = generateId();
	public static final String TEXT_USER_NAME_EXISTS = generateId();
	public static final String TEXT_ROLE_REQUIRED = generateId();
	public static final String TEXT_GROUP_REQUIRED = generateId();
	public static final String TEXT_ERROR = generateId();
	public static final String ERROR_APP_INITIALIZATION_FMT = generateId();
	public static final String TEXT_CHECK_FOR_UPDATE = generateId();
	public static final String TEXT_TEST_FINISHED = generateId();
	public static final String TEXT_BUTTON_CLOSE_TEST = generateId();

	// manager application errors
	public static final String ERROR_BAD_LOGIN = generateId();
	public static final String ERROR_NOT_ENABLED_USER = generateId();
	public static final String ERROR_UNSUFFICIENT_RIGHTS_ADMIN = generateId();

	// login window
	public static final String TEXT_LABEL_USERNAME = generateId();
	public static final String TEXT_LABEL_PASSWORD = generateId();
	public static final String TEXT_BUTTON_LOGIN = generateId();
	public static final String TEXT_MANAGER_LOGIN_TITLE = generateId();
	public static final String TEXT_LOGIN_TITLE = generateId();
	public static final String TEXT_MANAGER_LOGIN_HEADER = generateId();
	public static final String TEXT_LOGIN_HEADER = generateId();
	public static final String ERROR_LOGIN_FAILED = generateId();
	public static final String ERROR_LOAD_RESOURCE = generateId();

	// add group view
	public static final String TEXT_NEW_GROUP_TITLE = generateId();
	public static final String TEXT_UPDATE_GROUP_TITLE = generateId();
	public static final String TEXT_UPDATE_GROUPS_TITLE = generateId();
	public static final String INFO_GROUP_CREATED = generateId();

	// add user view
	public static final String TEXT_NEW_USER_TITLE = generateId();
	public static final String TEXT_UPDATE_USER_TITLE = generateId();
	public static final String TEXT_UPDATE_USERS_TITLE = generateId();
	public static final String TEXT_DISABLED_PACKS = generateId();
	public static final String INFO_USER_CREATED = generateId();
	public static final String INFO_USERS_CREATED = generateId();

	// edit groups view
	public static final String TEXT_EDIT_GROUPS_TITLE = generateId();
	public static final String TEXT_ADD_GROUP_TITLE = generateId();
	public static final String INFO_GROUP_SAVED = generateId();
	public static final String INFO_GROUP_DELETED = generateId();
	public static final String INFO_GROUPS_DELETED = generateId();
	public static final String TEXT_TOTAL_USERS_FMT = generateId();
	public static final String TEXT_TOTAL_PACKS_FMT = generateId();
	public static final String TEXT_NO_GROUPS_SELECTED = generateId();

	// edit permitions view
	public static final String TEXT_EDIT_PERMITIONS_TITLE = generateId();

	// edit personal data view
	public static final String TEXT_EDIT_PERSONAL_DATA_TITLE = generateId();
	public static final String TEXT_EDIT_PERSONAL_DATA_INFO = generateId();
	public static final String INFO_PERSONAL_DATA_SAVED = generateId();

	// edit user view
	public static final String TEXT_EDIT_USERS_TITLE = generateId();
	public static final String TEXT_EDIT_SELECTED_USER_TITLE = generateId();
	public static final String TEXT_EDIT_SELECTED_USERS_TITLE = generateId();
	public static final String TEXT_ADD_USER_TITLE = generateId();
	public static final String WARN_CREATE_GROUP_FIRST = generateId();
	public static final String INFO_USER_SAVED = generateId();
	public static final String INFO_USER_DELETED = generateId();
	public static final String INFO_USERS_DELETED = generateId();
	public static final String WARN_USER_ROLE_CHANGED = generateId();
	public static final String WARN_AT_LEAST_ONE_SU = generateId();
	public static final String WARN_ONLY_SU_CAN_DELETE_SU = generateId();
	public static final String TEXT_TOTAL_GROUPS_FMT = generateId();
	public static final String TEXT_NO_USERS_SELECTED = generateId();
	public static final String TEXT_GENERATE = generateId();

	// hypothesis pack select window
	public static final String TEXT_BASE_INFO = generateId();
	public static final String TEXT_TEST_INFO = generateId();

	// export
	public static final String TEXT_EXPORT_USERS_SHEET_NAME = generateId();
	public static final String TEXT_EXPORT_USERS_FILE_NAME = generateId();
	public static final String TEXT_EXPORT_GROUPS_SHEET_NAME = generateId();
	public static final String TEXT_EXPORT_GROUPS_FILE_NAME = generateId();
	public static final String ERROR_EXPORT_CANNOT_CREATE_FILE = generateId();
	public static final String ERROR_EXPORT_ROWS_LIMIT_EXCEEDED = generateId();
	public static final String ERROR_EXPORT_CANNOT_WRITE_TO_FILE = generateId();

	// delete
	public static final String TEXT_DELETE_CONFIRM = generateId();
	public static final String TEXT_DELETE_SELECTED_USERS = generateId();
	public static final String TEXT_DELETE_ALL_USERS = generateId();
	public static final String TEXT_DELETE_SELECTED_GROUPS = generateId();
	public static final String TEXT_DELETE_ALL_GROUPS = generateId();

	static int ids = 0;

	/**
	 * generate string id constant
	 * 
	 * @return
	 */
	private static String generateId() {
		return new Integer(ids++).toString();
	}

	@Override
	protected Object[][] getContents() {
		return null;
	}

}
