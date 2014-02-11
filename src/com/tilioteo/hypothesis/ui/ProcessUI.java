/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class ProcessUI extends UI {

	@WebServlet(value = "/process/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ProcessUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}
	
	@Override
	protected void init(VaadinRequest request) {
		// TODO Auto-generated method stub
		
	}

}
