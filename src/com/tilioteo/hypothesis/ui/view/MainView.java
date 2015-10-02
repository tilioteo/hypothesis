package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.ui.HypothesisNavigator;
import com.vaadin.shared.ui.label.ContentMode;
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
	
	private static final String VERSION = "1.4.9";

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
		panel.addStyleName("gradient");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		panel.setContent(layout);

		Label label = new Label("Hypothesis");
		//label.setSizeUndefined();
		label.setWidthUndefined();
		label.addStyleName(ValoTheme.LABEL_H1);
		label.addStyleName(ValoTheme.LABEL_COLORED);
		//label.addStyleName(ValoTheme.LABEL_BOLD);
		label.addStyleName("toptitle");
		
		Label space = new Label();
		layout.addComponent(space);
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		layout.setExpandRatio(space, 0.6f);
		layout.setExpandRatio(label, 1.0f);
		return panel;
	}

	private Panel buildBottomPanel() {
		Panel panel = new Panel();
		panel.setWidth(100.0f,  Unit.PERCENTAGE);
		panel.setHeight(25.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName("color2");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		panel.setContent(layout);
		
		Label label = new Label("Hypothesis&emsp;v."+VERSION+"&emsp;&emsp;&emsp;© 2013-2015 Tilioteo Ltd");
		label.setContentMode(ContentMode.HTML);
		label.setWidthUndefined();
		label.addStyleName(ValoTheme.LABEL_TINY);
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.TOP_CENTER);

		return panel;
	}

}
