/**
 * 
 */
package com.tilioteo.hypothesis.context;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Configuration
public class ContextConfig implements Serializable {
	
	@Bean
	public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:com/tilioteo/hypothesis/server/messages");
		
		return messageSource;
	}
}
