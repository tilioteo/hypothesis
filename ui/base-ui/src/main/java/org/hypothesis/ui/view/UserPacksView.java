/**
 * 
 */
package org.hypothesis.ui.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.cdi.UserPacks;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.ui.MainUI;

import com.vaadin.cdi.CDIView;
import com.vaadin.server.FontAwesome;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@CDIView(value = "/packs", uis = { MainUI.class })
@Title(value = "Caption.View.Packs", icon = FontAwesome.BARS, index = 1)
@RolesAllowed(value = { RoleType.USER, RoleType.MANAGER, RoleType.SUPERUSER })
public class UserPacksView extends PacksView {

	@Inject
	@UserPacks
	private PacksPresenter presenter;

	public UserPacksView() {
		System.out.println("Construct " + getClass().getName());
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());

		setPresenter(presenter);
		presenter.setView(this);
	}
}
