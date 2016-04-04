/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.util.Date;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ComponentEvent {

	public void setProperty(String name, Object value);

	public void setProperty(String name, Class<?> clazz, Object value);

	public void setProperty(String name, Object value, String pattern);

	public void setProperty(String name,  Class<?> clazz, Object value, String pattern);

	public void setClientTimestamp(Date clientTimestamp);

	public void setTimestamp(Date timestamp);

}
