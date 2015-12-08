/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.interfaces.Element;
import com.vaadin.data.Validatable;

/**
 * @author kamil
 *
 */
public interface ValidatorCallback extends Serializable {

	public void setComponentValidator(Validatable component, Element element, String name, String message);

}
