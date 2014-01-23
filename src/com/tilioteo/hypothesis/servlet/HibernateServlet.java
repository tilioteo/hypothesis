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
public class HibernateServlet extends VaadinServlet {
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		HibernateUtil.initSessionFactory(servletConfig.getServletContext());
	}

}
