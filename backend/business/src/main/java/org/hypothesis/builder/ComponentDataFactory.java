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
public interface ComponentDataFactory extends Serializable {

	/**
	 * Serialize component data by provided writer implementation
	 * 
	 * @param data
	 * @param writer
	 * @return string representation of component data or null when some
	 *         parameter is null.
	 */
	String buildComponentData(ComponentData data, DocumentWriter writer);

	/**
	 * Serialize action data from event by provided writer implementation
	 * 
	 * @param event
	 * @param writer
	 * @return string representation of event data or null when some parameter
	 *         is null.
	 */
	String buildActionData(ActionEvent event, DocumentWriter writer);

	/**
	 * Serialize slide data from presenter by provided writer implementation
	 * 
	 * @param presenter
	 * @param writer
	 * @return string representation of slide data or null when some parameter
	 *         is null.
	 */
	String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer);

	/**
	 * Serialize score data by provided writer implementation
	 * 
	 * @param scoreData
	 * @param writer
	 * @return string representation of score data or null when some parameter
	 *         is null.
	 */
	String buildScoreData(ScoreData scoreData, DocumentWriter writer);

}
