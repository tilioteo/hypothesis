/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;

import com.tilioteo.hypothesis.event.data.ComponentData;
import com.tilioteo.hypothesis.event.model.ActionEvent;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;

/**
 * @author kamil
 *
 */
public interface ComponentDataFactory extends Serializable {

	public String buildComponentData(ComponentData data);

	public String buildActionData(ActionEvent event);

	public String buildSlideContainerData(SlideContainerPresenter presenter);

}
