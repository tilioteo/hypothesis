/**
 * 
 */
package org.hypothesis.common.i18n;

import java.util.Locale;
import java.util.ResourceBundle;


import com.vaadin.Application;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Application locale messages
 */
public class ApplicationMessages implements LocalMessages {

	private static ApplicationMessages applicationMessages = null;

	/**
	 * Gets instance
	 * 
	 * @return AppMessage instance
	 */
	public static ApplicationMessages get() {
		assert (applicationMessages != null);

		return applicationMessages;
	}

	public static void init(Application application) {
		applicationMessages = new ApplicationMessages(application);
	}

	private final Application application;

	private ResourceBundle i18nBundle;

	private Locale locale;

	private ApplicationMessages(Application application) {
		assert (application != null);

		this.application = application;
		initLocaleBundle();
	}

	/**
	 * Get locale string by key
	 * 
	 * @param key
	 *            key string of locale
	 * @return locale string
	 */
	public String getString(String key) {
		if (!application.getLocale().equals(locale)) {
			initLocaleBundle();
		}
		return i18nBundle.getString(key);
	}

	/**
	 * (re)initialize resource bundle for current application locale
	 */
	private void initLocaleBundle() {
		locale = application.getLocale();
		i18nBundle = ResourceBundle.getBundle(Messages.class.getName(), locale);
	}
}
