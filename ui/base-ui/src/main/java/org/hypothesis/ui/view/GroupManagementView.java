/**
 * 
 */
package org.hypothesis.ui.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.server.FontAwesome;
import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.interfaces.GroupManagementPresenter;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.ui.MainUI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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

	@PostConstruct
	public void postConstruct() {
		setPresenter(presenter);
	}
}
