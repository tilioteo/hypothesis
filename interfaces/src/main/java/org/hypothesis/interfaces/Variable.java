/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface Variable<T> extends Serializable {

	String getName();

	Class<?> getType();

	Object getValue();

	String getStringValue();

	void setRawValue(Object value);

}
