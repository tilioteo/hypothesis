/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public final class ComponentWrapper implements Serializable {

	private Component component;
	private Alignment alignment;

	public ComponentWrapper(Component component, Alignment alignment) {
		this.component = component;
		this.alignment = alignment;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public Component getComponent() {
		return component;
	}

}
