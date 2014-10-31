/*
 * Copyright 2013 Tomi Virtanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.tltv.vprocjs.client.ui.processing;

import org.vaadin.tltv.vprocjs.client.ui.VProcessing;
import org.vaadin.tltv.vprocjs.shared.ui.processing.ProcessingServerRpc;
import org.vaadin.tltv.vprocjs.shared.ui.processing.ProcessingState;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(org.vaadin.tltv.vprocjs.ui.Processing.class)
public class ProcessingConnector extends AbstractComponentConnector implements ClickHandler,
		MouseWheelHandler, MouseUpHandler, MouseDownHandler, MouseOutHandler,
		MouseOverHandler, KeyPressHandler, KeyUpHandler {

    /*@Override
    protected Widget createWidget() {
        VProcessing widget = GWT.create(VProcessing.class);
        return widget;
    }*/

    @Override
	protected void init() {
		super.init();
		
		VProcessing widget = getWidget();
        widget.setUId(getConnectorId());
		
		widget.addClickHandler(this);
		widget.addMouseUpHandler(this);
		widget.addMouseDownHandler(this);
		widget.addMouseOutHandler(this);
		widget.addMouseOverHandler(this);
		widget.addMouseWheelHandler(this);
		widget.addKeyPressHandler(this);
		widget.addKeyUpHandler(this);

    }

	@Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

        ProcessingState state = getState();
        VProcessing widget = getWidget();
		
        if (stateChangeEvent.hasPropertyChanged("processingCode")) {
			widget.setProcessingCode(state.processingCode);
	        widget.stateChanged();
		}
    }

    @Override
    public VProcessing getWidget() {
        return (VProcessing) super.getWidget();
    }

    @Override
    public ProcessingState getState() {
        return (ProcessingState) super.getState();
    }

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (getState().keyReleaseListened) {
			getRpcProxy(ProcessingServerRpc.class).keyRelease();
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (getState().keyPressListened) {
			getRpcProxy(ProcessingServerRpc.class).keyPress(event.getCharCode());
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (getState().mouseEnterListened) {
			getRpcProxy(ProcessingServerRpc.class).mouseEnter();
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (getState().mouseLeaveListened) {
			getRpcProxy(ProcessingServerRpc.class).mouseLeave();
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (getState().mousePressListened) {
			getRpcProxy(ProcessingServerRpc.class).mousePress(event.getX(), event.getY());
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (getState().mouseReleaseListened) {
			getRpcProxy(ProcessingServerRpc.class).mouseRelease(event.getX(), event.getY());
		}
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if (getState().mouseWheelListened) {
			boolean up = event.isNorth();
			getRpcProxy(ProcessingServerRpc.class).mouseWheel(up, event.getDeltaY());
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (getState().mouseClickListened) {
			getRpcProxy(ProcessingServerRpc.class).click();
		}
	}
}
