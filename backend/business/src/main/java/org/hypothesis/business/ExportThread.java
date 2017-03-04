/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ExportThread extends Thread {
	
	private ExportRunnable runnable;
	
	public ExportThread(ThreadGroup threadGroup, ExportRunnable runnable) {
		super(threadGroup, runnable);
		this.runnable = runnable;
	}

	public void cancel() {
		if (runnable != null) {
			runnable.setCancelPending(true);
		}
	}

}
