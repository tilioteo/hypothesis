/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Map;

/**
 * @author kamil
 *
 */
public interface ExchangeVariable {
	
	public int getIndex();
	
	public Object getValue();

	public void setVariables(Map<String, Variable<?>> variables);
}
