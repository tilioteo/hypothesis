/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.io.Serializable;

import org.dom4j.Element;

import com.tilioteo.hypothesis.evaluation.AbstractBaseAction;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface HandlerCallback extends Serializable {

	public void setComponentHandler(Component component, Element element, String name, String actionId,
			AbstractBaseAction action, SlideContainerPresenter presenter);

}
