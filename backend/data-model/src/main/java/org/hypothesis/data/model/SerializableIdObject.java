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
public class SerializableIdObject implements Serializable, HasId<Long> {

	protected Long id;

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}
}
