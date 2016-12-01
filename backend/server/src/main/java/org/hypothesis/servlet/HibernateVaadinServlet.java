/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.server.*;
import org.apache.log4j.Logger;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.context.LogUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class HibernateVaadinServlet extends VaadinCDIServlet implements SessionInitListener, SessionDestroyListener {

	private static final Logger log = Logger.getLogger(HibernateVaadinServlet.class);

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
