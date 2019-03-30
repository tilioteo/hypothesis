/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface FieldConstants {

	String ID = "id";
	String UID = "uid";
	String NAME = "name";
	String NOTE = "note";
	String RANK = "rank";
	String ORDER = "order";
	String KEY = "key";
	String TYPE = "type";
	String USER_ID = "user_id";
	String GROUP_ID = "group_id";
	String OWNER_ID = "owner_id";
	String DESCRIPTION = "description";
	String PUBLISHED = "published";
	String JAVA_REQUIRED = "java_required";
	String ROLE_ID = "role_id";
	String TIMESTAMP = "timestamp";
	String CLIENT_TIMESTAMP = "client_timestamp";
	String DATETIME = "datetime";
	String RANDOMIZED = "randomized";
	String VIEW_UID = "view_uid";

	String XML_DATA = "xml_data";
	String OUTPUT = "output";

	String PACK_ID = "pack_id";
	String BRANCH_ID = "branch_id";
	String NEXT_BRANCH_ID = "next_branch_id";
	String BRANCH_TREK_ID = "branch_trek_id"; // used?
	String TASK_ID = "task_id";
	String SLIDE_ID = "slide_id";
	String PACK_SET_ID = "pack_set_id";
	String TEST_ID = "test_id";
	String SLIDE_TEMPLATE_UID = "slide_template_uid";
	String EVENT_ID = "event_id";
	String SCORE_ID = "score_id";
	String PRODUCTION = "production";
	String CREATED = "created";
	String STARTED = "started";
	String FINISHED = "finished";
	String BROKEN = "broken";
	String LAST_ACCESS = "last_access";
	String STATUS = "status";
	String LAST_BRANCH_ID = "last_branch_id";
	String LAST_TASK_ID = "last_task_id";
	String LAST_SLIDE_ID = "last_slide_id";
	String USERNAME = "username";
	String PASSWORD = "password";
	String ENABLED = "enabled";
	String EXPIRE_DATE = "expire_date";
	String BIRTH_DATE = "birth_date";
	String TESTING_DATE = "testing_date";
	String PASS = "pass";
	String SCORES = "scores";
	String ENABLE_AFTER_PACK_ID = "after_pack_id";
	String GENDER = "gender";
	String EDUCATION = "education";
	String FIRST_NAME = "first_name";
	String AUTO_DISABLE = "auto_disable";
	String TESTING_SUSPENDED = "testing_suspended";

	String PACK_NAME = "pack_name";
	String BRANCH_NAME = "branch_name";
	String TASK_NAME = "task_name";
	String SLIDE_NAME = "slide_name";

	String USERS = "users";
	String AVAILABLE_PACKS = "available_packs";
	String PACK = "pack"; // used?
	String TEST = "test"; // used?
	String SLIDE = "slide"; // used?
	String ENABLED_PACKS = "enabled_packs"; // used?
	String ROLES = "roles";
	String GROUPS = "groups";
	String TEST_STATE = "test_state";
	String TEST_ENABLER = "test_enabler";
	String ENABLER = "enabler";
	String SELECTED = "selected";

	String PROPERTY_PACK_ID = "packId";
	String PROPERTY_TEST_ID = "testId";
	String PROPERTY_USER_ID = "userId"; // used?
	String PROPERTY_OWNER_ID = "ownerId";
	String PROPERTY_EVENT_ID = "eventId"; // used?
	String PROPERTY_TESTING_DATE = "testingDate";

	String NESTED_USER = "user.";
	String NESTED_USER_ID = NESTED_USER + ID;
	String NESTED_USER_USERNAME = NESTED_USER + USERNAME;
	String NESTED_USER_NAME = NESTED_USER + NAME;
	String NESTED_USER_PASSWORD = NESTED_USER + PASSWORD;

	String NESTED_PACK = "pack.";
	String NESTED_PACK_ID = NESTED_PACK + ID;
}
