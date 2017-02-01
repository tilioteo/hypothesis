/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Token;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ProcessManager extends Serializable {

	/**
	 * Break current test if processing
	 */
	void requestBreakTest();

	void setAutoSlideShow(boolean value);

	/**
	 * Do cleanup of process manager
	 */
	void clean();

	void processToken(Token token, boolean b);

	void processTest(SimpleTest preparedTest);

	void fireTestError();

}
