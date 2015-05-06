/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

/**
 * @author kamil
 *
 */
public interface Variable<T> {

	public String getName();

	public Class<?> getType();

	public Object getValue();

	public String getStringValue();

	public void setRawValue(Object value);

}
