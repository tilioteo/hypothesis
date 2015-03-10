/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import com.tilioteo.hypothesis.core.Messages;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class InvalidSlideContentXmlException extends AbstractSlideXmlException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1333706555021587586L;

	public InvalidSlideContentXmlException() {
		super(Messages.getString("Error.InvalidXmlContent"));
	}
}
