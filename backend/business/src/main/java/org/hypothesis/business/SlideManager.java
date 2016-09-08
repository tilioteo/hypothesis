/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hypothesis.builder.ComponentDataBuilder;
import org.hypothesis.builder.SlideBuilder;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.DocumentWriter;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.XmlDocumentWriter;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.evaluation.Variable;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.ui.SlideContainer;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideManager extends ListManager<Task, Slide> {

	private static final Logger log = Logger.getLogger(SlideManager.class);

	private final DocumentReader reader = new XmlDocumentReader();
	private final DocumentWriter writer = new XmlDocumentWriter();

	private Slide current = null;
	private SlideContainer container = null;

	private final HashMap<Integer, Object> nextInputValues = new HashMap<>();

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

		container = SlideBuilder.buildSlideContainer(current, reader);
		if (container != null) {
			container.getPresenter().setUserId(userId);
			setInputValues();
			container.getPresenter().buildDone();
		}
	}

	public String getSerializedSlideData() {
		if (container != null) {
			return ComponentDataBuilder.buildSlideContainerData(container.getPresenter(), writer);
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
						org.hypothesis.interfaces.Variable<?> variable = container.getPresenter().getVariables()
								.get(name);
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
		return ComponentDataBuilder.buildActionData(event, writer);
	}

	public String getComponentData(ComponentEvent componentEvent) {
		return ComponentDataBuilder.buildComponentData(componentEvent.getData(), writer);
	}

}
