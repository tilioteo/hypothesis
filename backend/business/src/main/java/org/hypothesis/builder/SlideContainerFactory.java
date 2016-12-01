/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.data.DocumentReader;
import org.hypothesis.ui.SlideContainer;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlideContainerFactory extends Serializable {

	SlideContainer buildSlideContainer(String template, String content, DocumentReader reader);

}
