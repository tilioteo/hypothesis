/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hypothesis.builder.ComponentDataFactory;
import org.hypothesis.builder.SlideContainerFactory;
import org.hypothesis.business.ListManager;
import org.hypothesis.business.SlideManager;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.DocumentWriter;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.XmlDocumentWriter;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.evaluation.Variable;
import org.hypothesis.event.data.ScoreData;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.ui.SlideContainer;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class SlideManagerImpl extends ListManager<Task, Slide> implements SlideManager {

	private static Logger log = Logger.getLogger(SlideManagerImpl.class);

	@Inject
	private SlideContainerFactory containerFactory;

	@Inject
	private ComponentDataFactory dataFactory;

	private DocumentReader reader = new XmlDocumentReader();
	private DocumentWriter writer = new XmlDocumentWriter();

	private Slide current = null;
	private SlideContainer controller = null;

	private HashMap<Integer, Object> nextInputValues = new HashMap<>();

	private Long userId = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#current()
	 */
	@Override
	public Slide current() {
		Slide slide = super.current();

		if (current != slide) {
			current = slide;

			if (slide != null) {
				createSlideController();
			}
		}

		return current;
	}

	private void createSlideController() {
		log.debug("Creating slide controller.");

		controller = containerFactory.createSlideContainer(current, reader);
		if (controller != null) {
			controller.getPresenter().setUserId(userId);
			setInputValues();
			controller.getPresenter().buildDone();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#getSerializedSlideData()
	 */
	@Override
	public String getSerializedSlideData() {
		if (controller != null) {
			return dataFactory.buildSlideContainerData(controller.getPresenter(), writer);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#next()
	 */
	@Override
	public Slide next() {
		// save output value for next slide
		saveOutputValuesForNext();
		Slide next = super.next();

		// there is not another next slide, then clear nextInputValue
		if (next == null) {
			nextInputValues.clear();
		}

		return current();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#get(int)
	 */
	@Override
	public Slide get(int index) {
		current = super.get(index);
		createSlideController();

		return current;
	}

	private void saveOutputValuesForNext() {
		nextInputValues.clear();

		controller.getPresenter().getOutputs().entrySet().stream().filter(f -> f.getValue().getValue() != null)
				.forEach(e -> nextInputValues.put(e.getKey(), e));
	}

	private void setInputValues() {
		controller.getPresenter().getInputs().values().stream()
				.filter(f -> f != null && f instanceof IndexedExpression
						&& ((IndexedExpression) f).getExpression() != null)
				.map(m -> (IndexedExpression) m).forEach(e -> {
					int index = e.getIndex();
					String name = e.getExpression().getSimpleVariableName();
					Object value = nextInputValues.get(index);
					if (name != null && value != null) {
						org.hypothesis.interfaces.Variable<?> variable = controller.getPresenter().getVariables()
								.get(name);
						if (variable != null)
							variable.setRawValue(value);
						else {
							variable = new Variable<>(name);
							variable.setRawValue(value);
							controller.getPresenter().getVariables().put(name, variable);
						}
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#finishSlide()
	 */
	@Override
	public void finishSlide() {
		if (controller != null) {
			controller.getPresenter().viewDone();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#setUserId(java.lang.Long)
	 */
	@Override
	public void setUserId(Long userId) {
		this.userId = userId;

		if (controller != null) {
			controller.getPresenter().setUserId(userId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#getSlideContainer()
	 */
	@Override
	public Component getSlideContainer() {
		return controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.business.impl.SlideManager#getOutputs()
	 */
	@Override
	public Map<Integer, ExchangeVariable> getOutputs() {
		if (controller != null) {
			return controller.getPresenter().getOutputs();
		}

		return null;
	}

	public Map<Integer, ExchangeVariable> getScores() {
		if (controller != null) {
			return controller.getPresenter().getScores();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.impl.SlideManager#getActionData(org.hypothesis.
	 * event.model.ActionEvent)
	 */
	@Override
	public String getActionData(ActionEvent event) {
		return dataFactory.buildActionData(event, writer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.impl.SlideManager#getComponentData(org.hypothesis
	 * .event.model.ComponentEvent)
	 */
	@Override
	public String getComponentData(ComponentEvent componentEvent) {
		return dataFactory.buildComponentData(componentEvent.getData(), writer);
	}

	@Override
	public String getScoreData(ScoreData data) {
		return dataFactory.buildScoreData(data, writer);
	}

}
