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
public interface ComponentDataFactory extends Serializable {

	public String buildComponentData(ComponentData data, DocumentWriter writer);

	public String buildActionData(ActionEvent event, DocumentWriter writer);

	public String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer);

}
