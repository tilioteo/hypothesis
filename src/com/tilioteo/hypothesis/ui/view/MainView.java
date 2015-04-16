package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.ui.HypothesisNavigator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class MainView extends VerticalLayout {

	public MainView() {
		setSizeFull();
		//addStyleName("mainview");

		addComponent(buildTopPanel());

		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setSizeFull();
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1.0f);

		mainLayout.addComponent(new HypothesisMenu());

		ComponentContainer content = new CssLayout();
		content.addStyleName("view-content");
		content.setSizeFull();
		mainLayout.addComponent(content);
		mainLayout.setExpandRatio(content, 1.0f);

		new HypothesisNavigator(content);

		addComponent(buildBottomPanel());
	}

	private Panel buildTopPanel() {
		Panel panel = new Panel();
		panel.setWidth(100.0f, Unit.PERCENTAGE);
		panel.setHeight(120.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName("color2");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		panel.setContent(layout);

		Label label = new Label("Hypothesis");
		label.setWidthUndefined();
		label.addStyleName(ValoTheme.LABEL_H1);
		label.addStyleName(ValoTheme.LABEL_COLORED);
		//label.addStyleName(ValoTheme.LABEL_BOLD);
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

		return panel;
	}

	private Panel buildBottomPanel() {
		Panel panel = new Panel();
		panel.setWidth(100.0f,  Unit.PERCENTAGE);
		panel.setHeight(25.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName("color2");

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		panel.setContent(layout);

		return panel;
	}

}
