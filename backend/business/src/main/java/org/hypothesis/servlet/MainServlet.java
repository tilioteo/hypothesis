/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.hypothesis.ui.MainUI;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Constants;

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
		name = "main-servlet",
		initParams = {
				@WebInitParam(
						name = Constants.SERVLET_PARAMETER_UI_PROVIDER,
						value = "com.vaadin.cdi.CDIUIProvider")
		}
)
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
