/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.data.model.Slide;
import com.tilioteo.hypothesis.ui.SlideContainer;

/**
 * @author kamil
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
