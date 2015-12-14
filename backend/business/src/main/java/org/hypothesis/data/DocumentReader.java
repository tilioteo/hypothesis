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

	public Document readString(String string);

}
