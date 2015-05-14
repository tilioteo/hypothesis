/**
 * 
 */
package com.tilioteo.hypothesis.context;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public class SecretKey implements Serializable {

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
