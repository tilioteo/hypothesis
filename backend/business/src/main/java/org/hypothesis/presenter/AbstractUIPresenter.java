/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.hypothesis.extension.PluginManager;
import org.hypothesis.interfaces.UIPresenter;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractUIPresenter implements UIPresenter {

	protected void initializePlugins(VaadinRequest request) {

		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();

		String configFileName = servletContext.getInitParameter(PluginManager.PLUGIN_CONFIG_LOCATION);

		if (configFileName != null && configFileName.length() > 0) {
			configFileName = servletContext.getRealPath(configFileName);
			File configFile = new File(configFileName);
			PluginManager.get().initializeFromFile(configFile);
		}
	}

}
