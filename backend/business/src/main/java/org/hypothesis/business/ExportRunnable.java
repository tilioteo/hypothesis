/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.hypothesis.interfaces.Command;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ExportRunnable extends Runnable {

	void setCancelPending(boolean cancel);

	boolean isCancelPending();

	void setFinishCommand(Command command);

}
