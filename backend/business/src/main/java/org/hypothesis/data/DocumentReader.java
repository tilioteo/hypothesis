/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data;

import java.io.Serializable;

import org.hypothesis.interfaces.Document;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface DocumentReader extends Serializable {

	/**
	 * Read string definition and create document object tree
	 * 
	 * @param string
	 *            string definition to read
	 * @return new document object or null if not valid string definition
	 */
	public Document readString(String string);

}
