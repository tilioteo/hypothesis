/**
 * 
 */
package com.tilioteo.hypothesis.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kamil
 * 
 */
@Configuration
public class HypothesisConfig {
	
	@Bean
	public SecretKey secretKey() {
		return new SecretKey();
	}

}
