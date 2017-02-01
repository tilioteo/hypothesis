/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;

import org.hypothesis.data.DocumentWriter;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.ScoreData;
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

	public static String buildScoreData(ScoreData data, DocumentWriter writer) {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();

		if (data != null && writer != null) {
			return factory.buildScoreData(data, writer);
		}

		return null;
	}

}
