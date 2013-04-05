/**
 * 
 */
package org.hypothesis.core;

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
		super("Invalid Xml document for slide content");
	}
}
