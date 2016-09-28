package org.hypothesis.interfaces;

import org.hypothesis.event.interfaces.MainUIEvent.GroupAddedEvent;
import org.hypothesis.event.interfaces.MainUIEvent.GroupSelectionChangedEvent;
import org.hypothesis.event.interfaces.MainUIEvent.GroupUsersChangedEvent;

public interface GroupManagementPresenter extends ManagementPresenter {

	/**
	 * Make ui changes when new group added
	 * 
	 * @param event
	 */
	void addGroupIntoTable(GroupAddedEvent event);

	/**
	 * Make ui changes when group user changed
	 * 
	 * @param event
	 */
	void changeGroupUsers(GroupUsersChangedEvent event);

	void setToolsEnabled(GroupSelectionChangedEvent event);

}