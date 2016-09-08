/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.interfaces.Element;

import com.vaadin.data.Validatable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ValidatorCallback extends Serializable {

	/**
	 * Callback function for validator definition iteration step. Implement your
	 * validation rules by context specified in parameters. To add new validator
	 * call {@link Validatable#addValidator(com.vaadin.data.Validator)}
	 * 
	 * @param component
	 *            Component to add validator
	 * @param element
	 * @param name
	 * @param message
	 */
	public void setComponentValidator(Validatable component, Element element, String name, String message);

}
