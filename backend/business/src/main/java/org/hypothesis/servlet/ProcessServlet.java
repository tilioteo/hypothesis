/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import javax.servlet.annotation.WebServlet;

import org.hypothesis.ui.ProcessUI;

import com.vaadin.annotations.VaadinServletConfiguration;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@WebServlet(value = "/process/*", asyncSupported = true, name = "process-servlet")
@VaadinServletConfiguration(productionMode = false, ui = ProcessUI.class, widgetset = "org.hypothesis.WidgetSet", heartbeatInterval = 60)
public class ProcessServlet extends HibernateVaadinServlet {

}
