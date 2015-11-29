/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.builder.ComponentDataBuilder;
import com.tilioteo.hypothesis.builder.ComponentDataFactory;
import com.tilioteo.hypothesis.builder.SlideBuilder;
import com.tilioteo.hypothesis.builder.SlideContainerFactory;
import com.tilioteo.hypothesis.builder.xml.ComponentDataXmlFactory;
import com.tilioteo.hypothesis.builder.xml.SlideContainerXmlFactory;
import com.tilioteo.hypothesis.data.model.Slide;
import com.tilioteo.hypothesis.data.model.Task;
import com.tilioteo.hypothesis.evaluation.IndexedExpression;
import com.tilioteo.hypothesis.evaluation.Variable;
import com.tilioteo.hypothesis.event.model.ActionEvent;
import com.tilioteo.hypothesis.event.model.ComponentEvent;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.tilioteo.hypothesis.ui.SlideContainer;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class SlideManager extends ListManager<Task, Slide> {

	private static Logger log = Logger.getLogger(SlideManager.class);

	private SlideContainerFactory factory = new SlideContainerXmlFactory();
	private ComponentDataFactory dataFactory = new ComponentDataXmlFactory();

	private Slide current = null;
	private SlideContainer container = null;

	private HashMap<Integer, Object> nextInputValues = new HashMap<>();

	private Long userId = null;

	public SlideManager() {
		super();
	}

	@Override
	public Slide current() {
		Slide slide = super.current();

		if (current != slide) {
			current = slide;

			if (slide != null) {
				buildSlideContainer();
			}
		}

		return current;
	}

	private void buildSlideContainer() {
		log.debug("Building slide container.");

		container = SlideBuilder.buildSlideContainer(current, factory);
		if (container != null) {
			container.getPresenter().setUserId(userId);
			setInputValues();
			container.getPresenter().buildDone();
		}
	}

	public String getSerializedSlideData() {
		if (container != null) {
			return ComponentDataBuilder.buildSlideContainerData((SlideContainerPresenter) container.getPresenter(),
					dataFactory);
		}

		return null;
	}

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

	@Override
	public Slide get(int index) {
		current = super.get(index);
		buildSlideContainer();

		return current;
	}

	private void saveOutputValuesForNext() {
		nextInputValues.clear();

		Map<Integer, ExchangeVariable> map = container.getPresenter().getOutputs();
		for (Integer index : map.keySet()) {
			ExchangeVariable outputValueExpression = map.get(index);
			Object value = outputValueExpression.getValue();
			if (value != null) {
				nextInputValues.put(index, value);
			}
		}
	}

	private void setInputValues() {
		Map<Integer, ExchangeVariable> map = container.getPresenter().getInputs();
		for (ExchangeVariable inputValueExpression : map.values()) {
			if (inputValueExpression != null && inputValueExpression instanceof IndexedExpression) {
				IndexedExpression inputExpression = (IndexedExpression) inputValueExpression;
				if (inputExpression.getExpression() != null) {
					int index = inputExpression.getIndex();
					String name = inputExpression.getExpression().getSimpleVariableName();
					Object value = nextInputValues.get(index);
					if (name != null && value != null) {
						com.tilioteo.hypothesis.interfaces.Variable<?> variable = container.getPresenter()
								.getVariables().get(name);
						if (variable != null)
							variable.setRawValue(value);
						else {
							variable = new Variable<>(name);
							variable.setRawValue(value);
							container.getPresenter().getVariables().put(name, variable);
						}
					}
				}
			}
		}
	}

	public void finishSlide() {
		if (container != null) {
			container.getPresenter().viewDone();
		}
	}

	public Slide getSlide() {
		return current;
	}

	public void setUserId(Long userId) {
		this.userId = userId;

		if (container != null) {
			container.getPresenter().setUserId(userId);
		}
	}

	public Component getSlideContainer() {
		return container;
	}

	public Map<Integer, ExchangeVariable> getOutputs() {
		if (container != null) {
			return container.getPresenter().getOutputs();
		}

		return null;
	}

	public String getActionData(ActionEvent event) {
		return ComponentDataBuilder.buildActionData(event, dataFactory);
	}

	public String getComponentData(ComponentEvent componentEvent) {
		return ComponentDataBuilder.buildComponentData(componentEvent.getData(), dataFactory);
	}

}
