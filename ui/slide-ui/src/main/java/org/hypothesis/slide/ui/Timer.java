package org.hypothesis.slide.ui;

@SuppressWarnings("serial")
public class Timer extends org.vaadin.special.ui.Timer {

	public Timer() {
		super();
	}
	
	public void start() {
		start(getTime());
	}

}
