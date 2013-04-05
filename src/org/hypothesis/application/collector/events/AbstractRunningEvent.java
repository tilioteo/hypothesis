/**
 * 
 */
package org.hypothesis.application.collector.events;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractRunningEvent extends AbstractProcessEvent {

	protected AbstractRunningEvent(Object source) {
		super(source);
	}
}
