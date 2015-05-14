/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.io.Serializable;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ParentAlignment implements Serializable {

	private Alignment alignment = null;

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

}
