package org.hypothesis.interfaces;

import com.vaadin.ui.AbstractComponent;

public interface SlideControlHandler {

    void addControl(AbstractComponent timer);

    boolean removeControl(AbstractComponent timer);

    void removeAllControls();

}
