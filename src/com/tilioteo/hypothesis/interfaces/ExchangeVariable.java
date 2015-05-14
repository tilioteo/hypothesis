/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;
import java.util.Map;

/**
 * @author kamil
 *
 */
public interface ExchangeVariable extends Serializable {
	
	public int getIndex();
	
	public Object getValue();

	public void setVariables(Map<String, Variable<?>> variables);
}
