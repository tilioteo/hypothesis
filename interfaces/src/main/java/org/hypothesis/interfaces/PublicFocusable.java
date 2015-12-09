/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import org.hypothesis.annotations.Public;
import org.hypothesis.annotations.PublicType;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@PublicType
public interface PublicFocusable extends PublicComponent {

	@Public
	public void focus();

}
