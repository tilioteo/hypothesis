package org.hypothesis.interfaces;

import java.util.Collection;

import org.hypothesis.data.model.Group;

public interface GroupWindowPresenter extends WindowPresenter {

	/**
	 * Show the window for editing group
	 * 
	 * @param group
	 */
	void showWindow(Group group);

	/**
	 * Show the window for editing more groups
	 * 
	 * @param groups
	 */
	void showWindow(Collection<Group> groups);

}