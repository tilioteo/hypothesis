/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.tilioteo.hypothesis.ui.UI;

/**
 * @author kamil
 *
 */
public class Messages {
	
	public static String getString(String key, Object... arguments) {
		UI current = UI.getCurrent();
		if (current != null) {
			ApplicationContext applicationContext =	current.getApplicationContext();
			MessageSource messageSource = applicationContext.getBean(ReloadableResourceBundleMessageSource.class);
			
			Locale locale = current.getLocale();
			return messageSource.getMessage(key, arguments, locale);
		}
		return key;
	}

	public static String getString(String key) {
		return getString(key, (Object[])null);
	}
}
