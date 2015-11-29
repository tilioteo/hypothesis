/**
 * 
 */
package com.tilioteo.hypothesis.data.interfaces;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasUid<T> extends Serializable {

	public T getUid();

}
