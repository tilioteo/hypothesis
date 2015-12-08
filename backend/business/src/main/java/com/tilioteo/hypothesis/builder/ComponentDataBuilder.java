/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.DocumentWriter;
import com.tilioteo.hypothesis.event.data.ComponentData;
import com.tilioteo.hypothesis.event.model.ActionEvent;
import com.tilioteo.hypothesis.interfaces.SlidePresenter;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ComponentDataBuilder implements Serializable {

	public static String buildComponentData(ComponentData data, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (data != null && writer != null) {
			return factory.buildComponentData(data, writer);
		}

		return null;
	}

	public static String buildActionData(ActionEvent event, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (event != null && writer != null) {
			return factory.buildActionData(event, writer);
		}

		return null;
	}

	public static String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (presenter != null && writer != null) {
			return factory.buildSlideContainerData(presenter, writer);
		}

		return null;
	}

}
