/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import com.tilioteo.hypothesis.event.data.ComponentData;
import com.tilioteo.hypothesis.event.model.ActionEvent;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;

/**
 * @author kamil
 *
 */
public class ComponentDataBuilder {

	public static String buildComponentData(ComponentData data, ComponentDataFactory factory) {
		if (data != null && factory != null) {
			return factory.buildComponentData(data);
		}

		return null;
	}

	public static String buildActionData(ActionEvent event, ComponentDataFactory factory) {
		if (event != null && factory != null) {
			return factory.buildActionData(event);
		}

		return null;
	}

	public static String buildSlideContainerData(SlideContainerPresenter presenter, ComponentDataFactory factory) {
		if (presenter != null && factory != null) {
			return factory.buildSlideContainerData(presenter);
		}

		return null;
	}

}
