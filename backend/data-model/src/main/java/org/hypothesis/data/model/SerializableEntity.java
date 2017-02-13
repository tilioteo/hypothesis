/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.io.Serializable;

import org.hypothesis.data.interfaces.HasId;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         class for base serializable object providing numeric id
 * 
 */
@SuppressWarnings("serial")
public class SerializableEntity<ID> implements Serializable, HasId<ID> {

	protected ID id;

	@Override
	public ID getId() {
		return id;
	}

	protected void setId(ID id) {
		this.id = id;
	}
}
