/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.tilioteo.hypothesis.ui.view.SimplePacksView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class SimpleUI extends UI {

	@WebServlet(value = "/simple/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SimpleUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}

	private Navigator navigator;
	private String pid = null;
	
	@Override
	protected void init(VaadinRequest request) {
		super.init(request);
		
		pid = request.getParameter("pid");

		getPage().setTitle("Hypothesis");

		navigator = new Navigator(this, this);
		
		SimplePacksView packsView = new SimplePacksView(pid);
		navigator.addView("/packs", packsView);
		
		navigator.navigateTo("/packs");
	}
}
