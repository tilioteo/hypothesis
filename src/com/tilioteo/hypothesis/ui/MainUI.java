/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.tilioteo.hypothesis.ui.view.PacksView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class MainUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}

	private Navigator navigator;
	private String pid = null;
	
	@Override
	protected void init(VaadinRequest request) {
		
		pid = request.getParameter("pid");

		getPage().setTitle("Hypothesis");

		navigator = new Navigator(this, this);
		
		PacksView packsView = new PacksView(pid);
		navigator.addView("/packs", packsView);
		
		navigator.navigateTo("/packs");
	}
}