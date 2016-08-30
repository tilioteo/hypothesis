/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.jar.Attributes;

import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.MainPresenter;
import org.hypothesis.ui.MainScreen;
import org.hypothesis.ui.menu.HypothesisMenu;
import org.hypothesis.utility.ManifestUtility;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MainPresenterImpl implements MainPresenter {

	private static String VERSION;
	private static String VERSION_ADDITIONAL;

	private ComponentContainer content;

	private HypothesisMenuPresenter menuPresenter;
	private VersionInfoPresenter versionInfoPresenter;

	public MainPresenterImpl(MainEventBus bus) {

		menuPresenter = new HypothesisMenuPresenter(bus);
		versionInfoPresenter = new VersionInfoPresenter(bus);

		Attributes attributes = ManifestUtility.getManifestAttributes();

		VERSION = attributes.getValue(ManifestUtility.VERSION);
		VERSION_ADDITIONAL = attributes.getValue(ManifestUtility.VERSION_ADDITIONAL);
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

		if (!VERSION_ADDITIONAL.isEmpty()) {
			Label additional = new Label(VERSION_ADDITIONAL);
			layout.addComponent(additional);
			layout.setComponentAlignment(additional, Alignment.MIDDLE_RIGHT);
			// layout.setExpandRatio(additional, 0.3f);
		}

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

		Button button = new Button("Hypothesis&emsp;v." + VERSION + "&emsp;&emsp;&emsp;© 2013-2016 Tilioteo Ltd");
		button.setCaptionAsHtml(true);
		button.setWidthUndefined();
		button.addStyleName(ValoTheme.BUTTON_TINY);
		button.addStyleName(ValoTheme.BUTTON_BORDERLESS);

		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				versionInfoPresenter.showWindow();
			}
		});

		/*
		 * Label label = new Label("Hypothesis&emsp;v." + VERSION +
		 * "&emsp;&emsp;&emsp;© 2013-2016 Tilioteo Ltd");
		 * label.setContentMode(ContentMode.HTML); label.setWidthUndefined();
		 * label.addStyleName(ValoTheme.LABEL_TINY);
		 */
		layout.addComponent(button);
		layout.setComponentAlignment(button, Alignment.TOP_CENTER);

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
