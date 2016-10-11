package org.hypothesis.interfaces;

import org.hypothesis.event.interfaces.MainUIEvent;

public interface UserManagementPresenter extends ManagementPresenter {

	void addUserIntoTable(MainUIEvent.UserAddedEvent event);

	void changeUserGroups(MainUIEvent.UserGroupsChangedEvent event);

	void setToolsEnabled(MainUIEvent.UserSelectionChangedEvent event);

}