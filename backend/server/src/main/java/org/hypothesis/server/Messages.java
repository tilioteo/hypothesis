/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.server;

import java.util.Locale;

import org.hypothesis.resource.context.MessageSource;
import org.hypothesis.resource.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class Messages {

	private static MessageSource messageSource = null;
	private static Locale locale = Locale.ENGLISH;

	private Messages() {
	}

	private static void initMessageSource() {
		messageSource = new ReloadableResourceBundleMessageSource();
		((ReloadableResourceBundleMessageSource) messageSource).setBasename("classpath:org/hypothesis/server/messages");
	}

	public static void initMessageSource(Locale locale) {
		initMessageSource();
		setLocale(locale);
	}

	private static void setLocale(Locale locale) {
		if (locale != null) {
			if (locale != Messages.locale) {
				Messages.locale = locale;
			}
		} else {
			Messages.locale = Locale.ENGLISH;
		}
	}

	public static String getString(String key, Object... arguments) {
		if (messageSource != null) {
			try {
				return messageSource.getMessage(key, arguments, locale);
			} catch (Exception e) {
				return key;
			}
		}
		return key;
	}

	public static String getString(String key) {
		return getString(key, (Object[]) null);
	}
}
