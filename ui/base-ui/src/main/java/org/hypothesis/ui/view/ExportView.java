/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.interfaces.ExportPresenter;
import org.hypothesis.interfaces.RoleType;
import org.hypothesis.ui.MainUI;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
@CDIView(value = "/export", uis = { MainUI.class })
@Title(value = "Caption.View.Export", icon = FontAwesome.TABLE, index = 5)
@RolesAllowed(value = { RoleType.MANAGER, RoleType.SUPERUSER })
public class ExportView extends VerticalLayout implements View {

	@Inject
	private ExportPresenter presenter;

	public ExportView() {
		System.out.println("Construct " + getClass().getName());

		setSizeFull();
		setMargin(true);
		setSpacing(true);
	}

	private void buildContent() {
		removeAllComponents();

		addComponent(presenter.buildHeader());
		Component content = presenter.buildContent();
		addComponent(content);
		setExpandRatio(content, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());

		buildContent();
	}
}
