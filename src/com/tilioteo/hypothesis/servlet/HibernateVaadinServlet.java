/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.vaadin.server.VaadinServlet;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class HibernateVaadinServlet extends VaadinServlet {
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		LogUtil.initLogging(servletConfig.getServletContext());
		HibernateUtil.initSessionFactory(servletConfig.getServletContext());
	}

}
