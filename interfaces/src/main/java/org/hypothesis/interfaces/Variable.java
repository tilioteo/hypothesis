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

	public String getName();

	public Class<?> getType();

	public Object getValue();

	public String getStringValue();

	public void setRawValue(Object value);

}
