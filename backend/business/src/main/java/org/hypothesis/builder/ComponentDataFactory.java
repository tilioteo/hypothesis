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
public interface ComponentDataFactory extends Serializable {

	String buildComponentData(ComponentData data, DocumentWriter writer);

	String buildActionData(ActionEvent event, DocumentWriter writer);

	String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer);

}
