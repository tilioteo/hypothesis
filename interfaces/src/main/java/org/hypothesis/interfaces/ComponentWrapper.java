/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ComponentWrapper extends Serializable {

	Component getComponent();

	Alignment getAlignment();

}
