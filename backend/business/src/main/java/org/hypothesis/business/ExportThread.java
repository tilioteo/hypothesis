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

	private final SimpleExportRunnable runnable;

	public ExportThread(ThreadGroup threadGroup, SimpleExportRunnable runnable) {
		super(threadGroup, runnable);
		this.runnable = runnable;
	}

	public void cancel() {
		if (runnable != null && runnable instanceof CancelableExportRunnable) {
			((CancelableExportRunnable) runnable).setCancelPending(true);
		}
	}

}
