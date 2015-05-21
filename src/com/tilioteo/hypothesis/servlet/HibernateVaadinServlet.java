/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javax.naming.Reference;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

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

		// cleanup all ThreadLocal values
		HibernateUtil.cleanup();
		cleanThreadLocals();
	}

	private void cleanThreadLocals() {
		log.debug("Cleaning ThreadLocal values");
		try {
			// Get a reference to the thread locals table of the current thread
			Thread thread = Thread.currentThread();
			Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			Object threadLocalTable = threadLocalsField.get(thread);

			// Get a reference to the array holding the thread local variables
			// inside the
			// ThreadLocalMap of the current thread
			Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
			Field tableField = threadLocalMapClass.getDeclaredField("table");
			tableField.setAccessible(true);
			Object table = tableField.get(threadLocalTable);

			// The key to the ThreadLocalMap is a WeakReference object. The
			// referent field of this object
			// is a reference to the actual ThreadLocal variable
			Field referentField = Reference.class.getDeclaredField("referent");
			referentField.setAccessible(true);

			for (int i = 0; i < Array.getLength(table); i++) {
				// Each entry in the table array of ThreadLocalMap is an Entry
				// object
				// representing the thread local reference and its value
				Object entry = Array.get(table, i);
				if (entry != null) {
					// Get a reference to the thread local object and remove it
					// from the table
					ThreadLocal<?> threadLocal = (ThreadLocal<?>) referentField.get(entry);
					threadLocal.remove();
				}
			}
		} catch (Exception e) {
			// We will tolerate an exception here and just log it
			//throw new IllegalStateException(e);
		}
	}
}
