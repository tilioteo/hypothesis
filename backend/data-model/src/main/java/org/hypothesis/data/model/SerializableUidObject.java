/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import org.hypothesis.data.interfaces.HasUid;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         class for base serializable object providing string unique id
 * 
 */
@SuppressWarnings("serial")
public class SerializableUidObject implements Serializable, HasUid<String> {

	protected String uid;

	@Override
	public String getUid() {
		return uid;
	}

	protected void setUid(String uid) {
		this.uid = uid;
	}

}
