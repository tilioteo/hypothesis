/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;

import com.tilioteo.hypothesis.entity.Status;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasStatus extends Serializable {

	public Status getStatus();

}
