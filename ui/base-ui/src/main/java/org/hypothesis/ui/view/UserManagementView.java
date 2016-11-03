/**
 * 
 */
package org.hypothesis.ui.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.server.FontAwesome;
import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.interfaces.UserManagementPresenter;
import org.hypothesis.ui.MainUI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@CDIView(value = "/users", uis = { MainUI.class })
@Title(value = "Caption.View.Users", icon = FontAwesome.USER, index = 3)
@RolesAllowed(value = { RoleType.MANAGER, RoleType.SUPERUSER })
public class UserManagementView extends ManagementView {

	@Inject
	private UserManagementPresenter presenter;

	public UserManagementView() {
		System.out.println("Construct " + getClass().getName());
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());

		setPresenter(presenter);
	}
}
