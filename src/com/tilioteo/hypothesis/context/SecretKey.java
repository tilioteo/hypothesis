/**
 * 
 */
package com.tilioteo.hypothesis.context;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author kamil
 * 
 */
public class SecretKey {

	private String method;

	private String key;

	public String getMethod() {
		return method;
	}

	@Required
	public void setMethod(String method) {
		this.method = method;
	}

	public String getKey() {
		return key;
	}

	@Required
	public void setKey(String key) {
		this.key = key;
	}

}
