/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.EventObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ViewportEvent extends EventObject {

	protected ViewportEvent(Object source) {
		super(source);
	}

}
