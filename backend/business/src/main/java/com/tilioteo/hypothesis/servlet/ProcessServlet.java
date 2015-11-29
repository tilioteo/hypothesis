/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.ui.ProcessUI;
import com.vaadin.annotations.VaadinServletConfiguration;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@WebServlet(value = "/process/*", asyncSupported = true, name = "process-servlet", initParams = {
		@WebInitParam(name = "UIProvider", value = "com.tilioteo.hypothesis.provider.ProcessUIProvider") })
@VaadinServletConfiguration(productionMode = false, ui = ProcessUI.class, widgetset = "com.tilioteo.hypothesis.WidgetSet", heartbeatInterval = 60)
public class ProcessServlet extends HibernateVaadinServlet {

}
