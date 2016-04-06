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

	public static String NAMESPACE_SEPARATOR = ".";

	public Element root();

	public Element createRoot(String name);

	public Element createRoot(Element element);

}
