/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import org.hypothesis.data.interfaces.HasId;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         class for base serializable object providing numeric id
 * 
 */
@SuppressWarnings("serial")
public class SerializableIdObject implements Serializable, HasId<Long> {

	protected Long id;

	@Override
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}
}
