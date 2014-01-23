/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import com.tilioteo.hypothesis.common.StringConstants;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UnexpectedCharException extends ExpressionException {
	
	public UnexpectedCharException(int position) {
		super(String.format(StringConstants.ERROR_UNEXP_CHAR_AT_POS_FMT, position));
	}
}
