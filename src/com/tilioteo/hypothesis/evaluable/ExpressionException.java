/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class ExpressionException extends Exception {

	protected ExpressionException(String message) {
		super(message);
	}
}
