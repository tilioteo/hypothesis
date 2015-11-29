/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.io.Serializable;

import com.tilioteo.hypothesis.interfaces.Evaluator;

/**
 * @author kamil
 *
 */
public interface ReferenceCallback extends Serializable {

	public Object getReference(String name, String id, Evaluator evaluator);

}
