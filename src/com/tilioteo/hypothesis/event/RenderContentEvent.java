/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.LayoutComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RenderContentEvent extends AbstractContentEvent {

	public RenderContentEvent(LayoutComponent content) {
		super(content);
	}

	public String getName() {
		return ProcessEventTypes.RenderSlide;
	}
}
