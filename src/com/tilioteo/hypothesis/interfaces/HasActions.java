/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;


/**
 * 
 * @author Kamil Morong - Hypothesis
 * 
 *         interface for classes which provide actions
 * 
 */
public interface HasActions extends Serializable {

	void setAction(String id, Action action);
	Action getAction(String id);

}