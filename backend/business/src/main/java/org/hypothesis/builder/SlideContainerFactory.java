/**
 * 
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentReader;
import org.hypothesis.ui.SlideContainer;

/**
 * @author kamil
 *
 */
public interface SlideContainerFactory extends Serializable {

	public SlideContainer buildSlideContainer(String template, String content, DocumentReader reader);

}
