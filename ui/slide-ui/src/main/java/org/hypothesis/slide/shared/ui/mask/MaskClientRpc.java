/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.shared.ui.mask;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface MaskClientRpc extends ClientRpc {

	void show();

	void hide();

}
