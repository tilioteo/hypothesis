/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import javax.servlet.annotation.WebServlet;

import org.hypothesis.ui.MainUI;

import com.vaadin.annotations.VaadinServletConfiguration;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@WebServlet(
		value = "/*",
		asyncSupported = true,
		name = "main-servlet")
@VaadinServletConfiguration(
		productionMode = false,
		ui = MainUI.class,
		widgetset = "org.hypothesis.WidgetSet"
)
public class MainServlet extends HibernateVaadinServlet {
	
	public MainServlet() {
		super();
	}

}
