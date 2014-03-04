/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessServlet extends HibernateVaadinServlet implements SessionInitListener, SessionDestroyListener {

	private static Logger log = Logger.getLogger(ProcessServlet.class);
	
	@Override
	protected void servletInitialized() throws ServletException {
		log.debug("Servlet initialized");
		super.servletInitialized();
		
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		log.debug("Session initializing");
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		log.debug("Session destroying");
	}

}
