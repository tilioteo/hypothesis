package com.tilioteo.hypothesis.shared.ui.timer;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.shared.AbstractComponentState;

@SuppressWarnings("serial")
public class TimerState extends AbstractComponentState {

	public enum Direction {UP, DOWN};
	
	public Direction direction = Direction.UP;
	public boolean running = false;
	public Set<Long> intervals = new HashSet<Long>();

}