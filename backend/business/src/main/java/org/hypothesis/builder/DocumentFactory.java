/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.interfaces.Document;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface DocumentFactory {

	Document mergeSlideDocument(Document templateDocument, Document contentDocument);

	Document createEventDataDocument();

}