/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentWriter;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.interfaces.SlidePresenter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ComponentDataBuilder implements Serializable {

	/**
	 * Serialize component data by provided writer implementation and default
	 * component data factory implementation
	 * 
	 * @param data
	 * @param writer
	 * @return string representation of component data or null when some
	 *         parameter is null.
	 */
	public static String buildComponentData(ComponentData data, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (data != null && writer != null) {
			return factory.buildComponentData(data, writer);
		}

		return null;
	}

	/**
	 * Serialize action data from event by provided writer implementation and
	 * default component data factory implementation
	 * 
	 * @param event
	 * @param writer
	 * @return string representation of event data or null when some parameter
	 *         is null.
	 */
	public static String buildActionData(ActionEvent event, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (event != null && writer != null) {
			return factory.buildActionData(event, writer);
		}

		return null;
	}

	/**
	 * Serialize slide data from presenter by provided writer implementation and
	 * default component data factory implementation
	 * 
	 * @param presenter
	 * @param writer
	 * @return string representation of slide data or null when some parameter
	 *         is null.
	 */
	public static String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (presenter != null && writer != null) {
			return factory.buildSlideContainerData(presenter, writer);
		}

		return null;
	}

}
