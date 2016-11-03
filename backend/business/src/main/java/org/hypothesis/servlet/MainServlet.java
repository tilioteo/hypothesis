/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Constants;
import org.hypothesis.ui.MainUI;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

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
