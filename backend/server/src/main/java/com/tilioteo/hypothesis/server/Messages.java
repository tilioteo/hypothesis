/**
 * 
 */
package com.tilioteo.hypothesis.server;

import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author kamil
 *
 */
public class Messages {

	private static MessageSource messageSource = null;
	private static Locale locale = Locale.ENGLISH;

	public static void initMessageSource(ApplicationContext applicationContext) {
		messageSource = applicationContext.getBean(ReloadableResourceBundleMessageSource.class);
	}

	public static void initMessageSource(ApplicationContext applicationContext, Locale locale) {
		initMessageSource(applicationContext);
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
			} catch (Throwable e) {
				return key;
			}
		}
		return key;
	}

	public static String getString(String key) {
		return getString(key, (Object[]) null);
	}
}
