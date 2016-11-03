/**
 * 
 */
package org.hypothesis.ui.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.server.FontAwesome;
import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.cdi.PublicPacks;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.ui.MainUI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@CDIView(value = "/public", uis = { MainUI.class })
@Title(value = "Caption.View.Public", icon = FontAwesome.EYE, index = 2)
@RolesAllowed(value = { RoleType.USER, RoleType.MANAGER, RoleType.SUPERUSER })
public class PublicPacksView extends PacksView {

	@Inject
	@PublicPacks
	private PacksPresenter presenter;

	public PublicPacksView() {
		System.out.println("Construct " + getClass().getName());
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());

		setPresenter(presenter);
		presenter.setView(this);
	}
}
