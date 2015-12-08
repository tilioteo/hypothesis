/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Date;

/**
 * @author kamil
 *
 */
public interface ComponentEvent {

	public void setProperty(String name, Object value);

	public void setProperty(String name, Object value, String pattern);

	public void setClientTimestamp(Date clientTimestamp);

	public void setTimestamp(Date timestamp);

}
