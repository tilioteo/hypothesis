/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
public interface ReferenceCallback extends Serializable {

	public Object getReference(String name, String id, Evaluator evaluator);

}
