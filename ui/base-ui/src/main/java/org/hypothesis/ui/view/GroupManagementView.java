/**
 * 
 */
package org.hypothesis.ui.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.interfaces.GroupManagementPresenter;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.ui.MainUI;

import com.vaadin.cdi.CDIView;
import com.vaadin.server.FontAwesome;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@CDIView(value = "/groups", uis = { MainUI.class })
@Title(value = "Caption.View.Groups", icon = FontAwesome.GROUP, index = 4)
@RolesAllowed(value = { RoleType.MANAGER, RoleType.SUPERUSER })
public class GroupManagementView extends ManagementView {

	@Inject
	private GroupManagementPresenter presenter;

	public GroupManagementView() {
		System.out.println("Construct " + getClass().getName());
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());

		setPresenter(presenter);
	}
}
