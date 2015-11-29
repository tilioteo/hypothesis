/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
public interface ComponentPresenter extends Serializable {

	public void attach();

	public void detach();
	
}
