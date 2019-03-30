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
public interface CancelableExportRunnable extends SimpleExportRunnable {

	void setCancelPending(boolean cancel);

	boolean isCancelPending();

}
