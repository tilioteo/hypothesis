/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.concurrent.ArrayBlockingQueue;

import com.tilioteo.hypothesis.processing.Command;

/**
 * @author kamil
 *
 */
public class AsynchronousCommandExecutor extends ArrayBlockingQueue<Command> implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1913670527503390523L;

	private Thread thread;
	
	private boolean running = false;

	public AsynchronousCommandExecutor() {
		super(1024);
	}

	@Override
	public void put(Command command) throws InterruptedException {
		super.put(command);
		
		if (!running) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	@Override
	public boolean add(Command command) {
		try {
			put(command);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void run() {
		setRunning(true);

		try {
			while (!isEmpty()) {
				final Command command = take();

				Command.Executor.execute(command);
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
