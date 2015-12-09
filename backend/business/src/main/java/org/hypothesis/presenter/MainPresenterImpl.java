/**
 * 
 */
package org.hypothesis.presenter;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.MainPresenter;
import org.hypothesis.servlet.ServletUtil;
import org.hypothesis.ui.MainScreen;
import org.hypothesis.ui.menu.HypothesisMenu;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinServlet;
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

	private static String VERSION;// = Manifests.read("Version");
	private static String VERSION_SPECIFIC;// =
											// Manifests.read("Version-Specific");
	private static String VERSION_ADDITIONAL;// =
												// Manifests.read("Version-Additional");*/

	private ComponentContainer content;

	private HypothesisMenuPresenter menuPresenter;

	public MainPresenterImpl(MainEventBus bus) {

		menuPresenter = new HypothesisMenuPresenter(bus);

		Manifest manifest = ServletUtil.getManifest(VaadinServlet.getCurrent().getServletContext());
		Attributes attributes = manifest.getMainAttributes();
		VERSION = attributes.getValue("Version");
		VERSION_SPECIFIC = attributes.getValue("Version-Specific");
		VERSION_ADDITIONAL = attributes.getValue("Version-Additional");
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
