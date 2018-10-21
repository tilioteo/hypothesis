package org.hypothesis.interfaces;

import com.vaadin.ui.Component;

public interface ControlPresenter extends ViewPresenter {

	Component buildHeader();

	Component buildControl();

}
