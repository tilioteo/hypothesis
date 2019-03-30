/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface PacksPresenter extends ViewPresenter {

	boolean isTestRunning();
	
	void maskView();
	void unmaskView();
	void refreshView();
	
	Token createToken(Pack pack);
}
