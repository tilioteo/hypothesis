/**
 * 
 */
package com.tilioteo.hypothesis.event;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainEventBus extends HypothesisEventBus {
	
	private static final MainEventBus instance = new MainEventBus();
	
	public static final MainEventBus get() {
		return instance;
	}
}
