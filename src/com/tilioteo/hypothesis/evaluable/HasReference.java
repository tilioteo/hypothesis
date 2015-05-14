/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public interface HasReference extends Serializable {
	
	public void setReference(Primitive reference);
	
}
