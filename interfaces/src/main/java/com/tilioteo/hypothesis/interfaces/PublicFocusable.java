/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.tilioteo.hypothesis.annotations.Public;
import com.tilioteo.hypothesis.annotations.PublicType;

/**
 * @author kamil
 *
 */
@PublicType
public interface PublicFocusable extends PublicComponent {

	@Public
	public void focus();

}
