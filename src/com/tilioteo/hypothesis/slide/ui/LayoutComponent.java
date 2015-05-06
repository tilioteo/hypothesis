/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class LayoutComponent {

	private Component component;
	private Alignment alignment;

	public LayoutComponent(Component component, Alignment alignment) {
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
