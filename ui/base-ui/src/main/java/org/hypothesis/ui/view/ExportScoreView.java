/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.interfaces.ExportScorePresenter;
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
@SuppressWarnings("serial")
@CDIView(value = "/scores", uis = { MainUI.class })
@Title(value = "Caption.View.Scores", icon = FontAwesome.BAR_CHART, index = 6)
@RolesAllowed(value = { RoleType.MANAGER, RoleType.SUPERUSER })
public class ExportScoreView extends VerticalLayout implements View {

	@Inject
	private ExportScorePresenter presenter;

	public ExportScoreView() {
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
		buildContent();
	}

}
