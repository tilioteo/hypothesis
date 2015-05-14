/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
public interface Action extends Serializable {

	public void execute();

	public String getId();
	
}
