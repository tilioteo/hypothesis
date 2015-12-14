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

	public void setComponentValidator(Validatable component, Element element, String name, String message);

}
