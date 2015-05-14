/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasUid<T> extends Serializable {
	public T getUid();
}
