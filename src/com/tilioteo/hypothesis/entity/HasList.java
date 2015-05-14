/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasList<E> extends Serializable {

	public List<E> getList();
}
