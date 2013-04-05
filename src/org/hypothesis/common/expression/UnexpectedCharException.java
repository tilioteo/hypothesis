/**
 * 
 */
package org.hypothesis.common.expression;

import org.hypothesis.common.constants.StringConstants;

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
