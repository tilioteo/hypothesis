/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasId<T> extends Serializable {
	public T getId();
}
