/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface AlignmentWrapper extends Serializable {

	public Alignment getAlignment();

	public void setAlignment(Alignment alignment);

}
