/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data;

import org.hypothesis.interfaces.Document;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface DocumentWriter extends Serializable {

	/**
	 * Write string definition from document object tree
	 * 
	 * @param document
	 *            document object tree
	 * @return string definition or null if document parameter is null
	 */
	public String writeString(Document document);

}
