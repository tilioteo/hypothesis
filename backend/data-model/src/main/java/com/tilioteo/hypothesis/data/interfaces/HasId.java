/**
 * 
 */
package com.tilioteo.hypothesis.data.interfaces;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasId<T> extends Serializable {
	
	public T getId();
}
