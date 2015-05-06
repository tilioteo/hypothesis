/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;


/**
 * 
 * @author Kamil Morong - Hypothesis
 * 
 *         interface for classes which provide actions
 * 
 */
public interface HasActions {

	void setAction(String id, Action action);
	Action getAction(String id);

}