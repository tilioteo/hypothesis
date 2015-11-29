/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class AlignmentWrapper implements Serializable {

	private Alignment alignment = null;

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

}
