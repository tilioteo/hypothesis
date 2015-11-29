/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.ui.SlideContainer;

/**
 * @author kamil
 *
 */
public interface SlideContainerFactory extends Serializable {

	public SlideContainer buildSlideContainer(String template, String content);

}
