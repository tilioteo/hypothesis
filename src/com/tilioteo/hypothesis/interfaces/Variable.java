/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
public interface Variable<T> extends Serializable {

	public String getName();

	public Class<?> getType();

	public Object getValue();

	public String getStringValue();

	public void setRawValue(Object value);

}
