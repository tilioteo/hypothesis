/**
 * 
 */
package com.tilioteo.hypothesis.common;

import com.tilioteo.hypothesis.interfaces.ComponentWrapper;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public final class ComponentWrapperImpl implements ComponentWrapper {

	private Component component;
	private Alignment alignment;

	public ComponentWrapperImpl(Component component, Alignment alignment) {
		this.component = component;
		this.alignment = alignment;
	}

	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	@Override
	public Component getComponent() {
		return component;
	}

}
