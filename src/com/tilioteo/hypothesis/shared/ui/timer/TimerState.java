package com.tilioteo.hypothesis.shared.ui.timer;

import com.vaadin.shared.AbstractComponentState;

@SuppressWarnings("serial")
public class TimerState extends AbstractComponentState {

	public enum Direction {UP, DOWN};
	
	public Direction direction = Direction.UP;
	public boolean running = false;

}