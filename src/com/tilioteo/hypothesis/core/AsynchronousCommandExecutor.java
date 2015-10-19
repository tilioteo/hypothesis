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
	boolean suspended = false;
	boolean stopped = false;
	
	public AsynchronousCommandExecutor() {
		super(1024);
		
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void put(Command command) throws InterruptedException {
		super.put(command);
		
		if (suspended) {
			resume();
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
		try {
			while (true) {
				while (!isEmpty()) {
					final Command command = take();

					Command.Executor.execute(command);
				}
				
				suspend();
				
				synchronized (this) {
					while (suspended) {
						wait();
					}
					
					if (stopped) {
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	synchronized void stop() {
		stopped = true;
		suspended = false;
		notify();
	}
	
	synchronized void suspend() {
		suspended = true;
	}
	
	synchronized void resume() {
		suspended = false;
		notify();
	}
	
}
