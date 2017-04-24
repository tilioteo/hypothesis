/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.List;
import java.util.Map;

import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.event.data.ScoreData;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.interfaces.ExchangeVariable;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlideManager {

	Slide current();

	String getSerializedSlideData();

	Slide next();

	Slide prior();

	Slide get(int index);

	/**
	 * Do some work on finish slide
	 */
	void finishSlide();

	void setUserId(Long userId);

	Component getSlideContainer();

	Map<Integer, ExchangeVariable> getOutputs();

	/**
	 * Create serialized action data
	 * 
	 * @param event
	 * @return
	 */
	String getActionData(ActionEvent event);

	/**
	 * Create serialized component data
	 * 
	 * @param componentEvent
	 *            event
	 * @return
	 */
	String getComponentData(ComponentEvent componentEvent);

	Slide find(Slide lastSlide);

	int getCount();

	void setListFromParent(Task task);

	List<Integer> createRandomOrder();

	void setOrder(List<Integer> order);

	Map<Integer, ExchangeVariable> getScores();

	/**
	 * Create serialized score data
	 * 
	 * @param data
	 * @return
	 */
	String getScoreData(ScoreData data);

}
