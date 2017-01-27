/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Slide;
import org.hypothesis.ui.SlideContainer;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlideContainerFactory extends Serializable {

	SlideContainer createSlideContainer(Slide entity, DocumentReader reader);

	SlideContainer createSlideContainer(String template, String content, DocumentReader reader);

}
