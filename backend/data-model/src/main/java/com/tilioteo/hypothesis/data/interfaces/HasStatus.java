/**
 * 
 */
package com.tilioteo.hypothesis.data.interfaces;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.model.Status;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasStatus extends Serializable {

	public Status getStatus();

}
