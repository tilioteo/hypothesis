/**
 * 
 */
package com.tilioteo.hypothesis.processing;

/**
 * 
 * @author Kamil Morong - Hypothesis
 * 
 *         interface for classes which provide actions
 * 
 */
public interface HasActions {

	void setAction(String id, AbstractBaseAction action);
	AbstractBaseAction getAction(String id);

}