/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface AlignmentWrapper extends Serializable {

	public Alignment getAlignment();

	public void setAlignment(Alignment alignment);

}
