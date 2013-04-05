/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import com.vaadin.ui.Alignment;

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
