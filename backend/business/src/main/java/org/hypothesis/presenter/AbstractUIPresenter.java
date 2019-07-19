/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.io.File;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.configuration.ConfigManager;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.extension.PluginManager;
import org.hypothesis.interfaces.UIPresenter;
import org.hypothesis.server.LocaleManager;

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

	@Override
	public void initialize(VaadinRequest request) {
		initializeConfigurations(request);

		initializeLocales(request);
	}

	@Override
	public Locale getCurrentLocale() {
		return LocaleManager.getCurrentLocale();
	}

	private void initializeConfigurations(VaadinRequest request) {
		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();

		initializeConfig(getFile(servletContext, servletContext.getInitParameter(ConfigManager.CONFIG_LOCATION)));
		initializePlugins(
				getFile(servletContext, servletContext.getInitParameter(PluginManager.PLUGIN_CONFIG_LOCATION)));
	}

	private File getFile(ServletContext servletContext, String relativeFileName) {
		if (StringUtils.isNotBlank(relativeFileName) && servletContext != null) {
			String fileName = servletContext.getRealPath(relativeFileName);
			return new File(fileName);
		}
		return null;
	}

	private void initializeConfig(File configFile) {
		ConfigManager.get().setConfigFile(configFile);
	}

	private void initializePlugins(File pluginConfigFile) {
		PluginManager.get().setConfigFile(pluginConfigFile);
	}

	private void initializeLocales(VaadinRequest request) {
		LocaleManager.initializeLocale(request);
	}
}
