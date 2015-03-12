/**
 * 
 */
package com.tilioteo.hypothesis.data;

/**
 * @author kamil
 *
 */
public interface Validator extends com.vaadin.data.Validator {
	
	boolean isValid(Object value);

}
