/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.io.Serializable;


/**
 * @author Kamil Morong - Hypothesis
 * 
 *         class for base serializable object providing string unique id
 * 
 */
@SuppressWarnings("serial")
public class SerializableUidObject implements Serializable, HasUid<String> {

	protected String uid;

	public String getUid() {
		return uid;
	}

	protected void setUid(String uid) {
		this.uid = uid;
	}

}
