/**
 * 
 */
package com.tilioteo.hypothesis.presenter;

import com.tilioteo.hypothesis.eventbus.MainEventBus;
import com.tilioteo.hypothesis.interfaces.MainPresenter;
import com.tilioteo.hypothesis.ui.MainScreen;
import com.tilioteo.hypothesis.ui.menu.HypothesisMenu;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainPresenterImpl implements MainPresenter {

	private static final String VERSION = "1.5.0";

	private ComponentContainer content;

	private HypothesisMenuPresenter menuPresenter;

	public MainPresenterImpl(MainEventBus bus) {

		menuPresenter = new HypothesisMenuPresenter(bus);

	}

	@Override
	public void attach() {
	}

	@Override
	public void detach() {
	}

	@Override
	public Component buildTopPanel() {
		Panel panel = new Panel();
		panel.setWidth(100.0f, Unit.PERCENTAGE);
		panel.setHeight(120.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName("gradient");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		panel.setContent(layout);

		Label label = new Label("Hypothesis");
		// label.setSizeUndefined();
		label.setWidthUndefined();
		label.addStyleName(ValoTheme.LABEL_H1);
		label.addStyleName(ValoTheme.LABEL_COLORED);
		// label.addStyleName(ValoTheme.LABEL_BOLD);
		label.addStyleName("toptitle");

		Label space = new Label();
		layout.addComponent(space);
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		layout.setExpandRatio(space, 0.6f);
		layout.setExpandRatio(label, 1.0f);
		return panel;
	}

	@Override
	public Component buildMainPane() {
		HorizontalLayout layout = new HorizontalLayout();

		layout.addComponent(new HypothesisMenu(menuPresenter));

		content = new CssLayout();
		content.addStyleName("view-content");
		content.setSizeFull();
		layout.addComponent(content);
		layout.setExpandRatio(content, 1.0f);

		return layout;
	}

	@Override
	public Component buildBottomPanel() {
		Panel panel = new Panel();
		panel.setWidth(100.0f, Unit.PERCENTAGE);
		panel.setHeight(25.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName("color2");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		panel.setContent(layout);

		Label label = new Label("Hypothesis&emsp;v." + VERSION + "&emsp;&emsp;&emsp;© 2013-2015 Tilioteo Ltd");
		label.setContentMode(ContentMode.HTML);
		label.setWidthUndefined();
		label.addStyleName(ValoTheme.LABEL_TINY);
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.TOP_CENTER);

		return panel;
	}

	@Override
	public ComponentContainer getContent() {
		return content;
	}

	@Override
	public MainScreen createScreen() {
		return new MainScreen(this);
	}

}
