/**
 * 
 */
package com.tilioteo.hypothesis.data.model;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.interfaces.HasUid;

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
