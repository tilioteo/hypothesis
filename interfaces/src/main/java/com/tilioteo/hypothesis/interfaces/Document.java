/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author kamil
 *
 */
public interface Document extends Serializable {

	public static String NAMESPACE_SEPARATOR = "/";

	public Element root();

	public Element createRoot(String name);

	public Element createRoot(Element element);

}
