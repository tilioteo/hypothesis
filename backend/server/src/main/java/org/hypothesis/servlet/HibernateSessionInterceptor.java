/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.StaleObjectStateException;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.context.LogUtil;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         Servlet filter to handle Hibernate Session per-request
 * 
 */
/*
 * @WebFilter(urlPatterns = { "/*" }, dispatcherTypes = {
 * DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE,
 * DispatcherType.ERROR })
 */
public class HibernateSessionInterceptor implements Filter {

	private static final Logger log = Logger.getLogger(HibernateSessionInterceptor.class);

	@Override
	public void destroy() {
		HibernateUtil.shutdown();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			log.debug("Starting a database transaction");
			HibernateUtil.beginTransaction();

			// Call the next filter (continue request processing)
			chain.doFilter(request, response);

			// Commit and cleanup
			log.debug("Committing the database transaction");
			HibernateUtil.commitTransaction();

		} catch (StaleObjectStateException e) {
			log.error("This interceptor does not implement optimistic concurrency control!");
			log.error("Your application will not work until you add compensation actions!");
			// Rollback, close everything, possibly compensate for any permanent
			// changes
			// during the conversation, and finally restart business
			// conversation. Maybe
			// give the user of the application a chance to merge some of his
			// work with
			// fresh data... what you do here depends on your applications
			// design.
			try {
				HibernateUtil.rollbackTransaction();
			} catch (Exception rbEx) {
				log.error("Could not rollback transaction after exception!", rbEx);
			}
			throw e;
		} catch (Exception e) {
			// Rollback only
			e.printStackTrace();
			try {
				HibernateUtil.rollbackTransaction();
			} catch (Exception rbEx) {
				log.error("Could not rollback transaction after exception!", rbEx);
			}

			// Let others handle it... maybe another interceptor for exceptions?
			throw new ServletException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Initializing filter...");
		log.debug("Initializing SessionFactory in static HibernateUtil singleton");

		try {
			LogUtil.initLogging(filterConfig.getServletContext());
			HibernateUtil.initSessionFactory(filterConfig.getServletContext());
		} catch (ExceptionInInitializerError e) {
			log.error("Hibernate SessionFactory initialization failed.");
			throw new ServletException(e);
		}
	}

}
