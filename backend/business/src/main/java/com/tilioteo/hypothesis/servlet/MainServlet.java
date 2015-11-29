/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.ui.MainUI;
import com.vaadin.annotations.VaadinServletConfiguration;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true, name = "main-servlet", initParams = {
		@WebInitParam(name = "UIProvider", value = "com.tilioteo.hypothesis.provider.MainUIProvider") })
@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.WidgetSet")
public class MainServlet extends HibernateVaadinServlet {

}
