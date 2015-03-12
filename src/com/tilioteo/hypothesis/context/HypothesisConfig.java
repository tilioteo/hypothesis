/**
 * 
 */
package com.tilioteo.hypothesis.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author kamil
 * 
 */
@Configuration
public class HypothesisConfig {
	
	//@Autowired
	//private SecretKey secretKey;
	
	/*@Bean
	public SecretKey secretKey() {
		return new SecretKey();
	}*/
	
	//public SecretKey getSecretKey() {
	//	return secretKey;
	//}

//	@Bean
//    public LocaleResolver localeResolver() {
//        FixedLocaleResolver localeResolver = new FixedLocaleResolver();
//        localeResolver.setDefaultLocale(new Locale("en"/*env.getProperty("system.default.language")*/));
//        return localeResolver;
//    }
	
	@Bean
	public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:com/tilioteo/hypothesis/messages/messages");
		
		return messageSource;
	}
}
