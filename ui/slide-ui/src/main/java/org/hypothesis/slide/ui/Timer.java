/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.ui;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Timer extends org.vaadin.special.ui.Timer {

	public Timer() {
		super();
	}

	public void start() {
		start(getTime());
	}

}
