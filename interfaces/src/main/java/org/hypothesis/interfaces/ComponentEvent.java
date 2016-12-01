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

	void setProperty(String name, Object value);

	void setProperty(String name, Class<?> clazz, Object value);

	void setProperty(String name, Object value, String elementPath);

	void setProperty(String name, Class<?> clazz, Object value, String elementPath);

	void setClientTimestamp(Date clientTimestamp);

	void setTimestamp(Date timestamp);

}
