/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.context.HibernateUtil;
import com.tilioteo.hypothesis.context.LogUtil;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class HibernateVaadinServlet extends VaadinServlet implements
		SessionInitListener, SessionDestroyListener {

	private static Logger log = Logger.getLogger(HibernateVaadinServlet.class);

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		LogUtil.initLogging(servletConfig.getServletContext());
		HibernateUtil.initSessionFactory(servletConfig.getServletContext());
	}

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

		// cleanup all sessions
		HibernateUtil.cleanup();
	}

}
