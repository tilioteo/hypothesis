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
@SuppressWarnings("serial")
public class SlideBuilder implements Serializable {

	public static SlideContainer buildSlideContainer(Slide entity, DocumentReader reader) {
		
		SlideContainerFactory factory = new SlideContainerFactoryImpl();

		if (entity != null && reader != null) {
			SlideContainer container = factory.buildSlideContainer(entity.getTemplateXmlData(), entity.getData(), reader);
			if (container != null) {
				container.setData(entity.getId());
			}

			return container;
		}

		return null;
	}

}
