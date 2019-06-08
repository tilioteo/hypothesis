/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.TokenDto;

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

	TokenDto createToken(PackDto pack);
}
