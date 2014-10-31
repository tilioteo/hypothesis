package org.vaadin.tltv.vprocjs.shared.ui.processing;

import com.vaadin.shared.AbstractComponentState;

@SuppressWarnings("serial")
public class ProcessingState extends AbstractComponentState {

    public String processingCode;

    public boolean mouseClickListened = false;
    public boolean mousePressListened = false;
    public boolean mouseReleaseListened = false;
    public boolean keyPressListened = false;
    public boolean keyReleaseListened = false;
    public boolean mouseEnterListened = false;
    public boolean mouseLeaveListened = false;
    public boolean mouseWheelListened = false;

}
