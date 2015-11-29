/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.io.Serializable;

import org.dom4j.Element;

import com.vaadin.data.Validatable;

/**
 * @author kamil
 *
 */
public interface ValidatorCallback extends Serializable {

	public void setComponentValidator(Validatable component, Element element, String name, String message);

}
