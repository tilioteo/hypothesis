/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.io.Serializable;


/**
 * @author Kamil Morong - Hypothesis
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
