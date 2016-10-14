/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.context;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
//@Configuration
public class ContextConfig implements Serializable {

	//@Bean
	/*public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:org/hypothesis/server/messages");

		return messageSource;
	}*/
}
