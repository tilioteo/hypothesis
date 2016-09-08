/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface Document extends Serializable {

	String NAMESPACE_SEPARATOR = ".";

	Element root();

	Element createRoot(String name);

	Element createRoot(Element element);

}
