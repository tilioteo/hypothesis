/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.concurrent.ArrayBlockingQueue;

import com.tilioteo.hypothesis.processing.Command;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public class CommandQueue extends ArrayBlockingQueue<Command> implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2562821141318954609L;
	
	private Thread thread;
	
	private boolean running = false;

	public CommandQueue() {
		super(1024);
	}

	@Override
	public void put(Command e) throws InterruptedException {
		super.put(e);
		
		if (!running) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		setRunning(true);

		try {
			while (!isEmpty()) {
				final Command command = take();

				// push to current ui
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						Command.Executor.execute(command);
					}
				});
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setRunning(false);
	}
	
	private synchronized void setRunning(boolean running) {
		this.running = running;
	}
	
}
