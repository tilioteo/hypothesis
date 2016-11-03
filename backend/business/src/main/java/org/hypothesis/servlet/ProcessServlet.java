/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Constants;
import org.hypothesis.ui.ProcessUI;

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
		value = "/process/*",
		asyncSupported = true,
		name = "process-servlet",
		initParams = {
				@WebInitParam(
						name = Constants.SERVLET_PARAMETER_UI_PROVIDER,
						value = "org.hypothesis.provider.ProcessUIProvider")
						//value = "com.vaadin.cdi.CDIUIProvider")
		}
)
@VaadinServletConfiguration(
		productionMode = false,
		ui = ProcessUI.class,
		widgetset = "org.hypothesis.WidgetSet",
		heartbeatInterval = 60
)
public class ProcessServlet extends HibernateVaadinServlet {

	public ProcessServlet() {
		super();
	}

}
