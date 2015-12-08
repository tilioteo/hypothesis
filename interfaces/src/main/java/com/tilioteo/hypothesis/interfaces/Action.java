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
public interface Action extends Serializable {

	public void execute();

	public String getId();

	public Map<Integer, ExchangeVariable> getOutputs();

}
