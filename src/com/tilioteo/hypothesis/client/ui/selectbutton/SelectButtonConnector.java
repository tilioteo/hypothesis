package com.tilioteo.hypothesis.client.ui.selectbutton;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.tilioteo.hypothesis.client.ui.SelectButton;
import com.tilioteo.hypothesis.shared.ui.selectbutton.SelectButtonServerRpc;
import com.tilioteo.hypothesis.shared.ui.selectbutton.SelectButtonState;
import com.vaadin.client.EventHelper;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;

@SuppressWarnings("serial")
public abstract class SelectButtonConnector extends AbstractFieldConnector implements
		FocusHandler, BlurHandler, ClickHandler {

    private HandlerRegistration focusHandlerRegistration = null;
    private HandlerRegistration blurHandlerRegistration = null;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addClickHandler(this);
        getWidget().client = getConnection();
        addStateChangeHandler("errorMessage", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                if (null != getState().errorMessage) {
                    if (getWidget().errorIndicatorElement == null) {
                        getWidget().errorIndicatorElement = DOM.createSpan();
                        getWidget().errorIndicatorElement
                                .setClassName("v-errorindicator");
                    }
                    getWidget().wrapper.insertBefore(
                            getWidget().errorIndicatorElement,
                            getWidget().captionElement);

                } else if (getWidget().errorIndicatorElement != null) {
                    getWidget().wrapper
                            .removeChild(getWidget().errorIndicatorElement);
                    getWidget().errorIndicatorElement = null;
                }
            }
        });

        addStateChangeHandler("resources", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                if (getIcon() != null) {
                    /*if (getWidget().icon == null) {
                        getWidget().icon = new Icon(getConnection());
                        getWidget().wrapper.insertBefore(
                                getWidget().icon.getElement(),
                                getWidget().captionElement);
                    }
                    getWidget().icon.setUri(getIcon());
                    getWidget().icon.setAlternateText(getState().iconAltText);*/
                } else {
                    if (getWidget().icon != null) {
                        getWidget().wrapper.removeChild(getWidget().icon
                                .getElement());
                        getWidget().icon = null;
                    }
                }
            }
        });
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

        if (stateChangeEvent.hasPropertyChanged("id")) {
            if (getState().id != null) {
                getWidget().updateIdRelatives();
            }
        }

        if (stateChangeEvent.hasPropertyChanged("caption")
                || stateChangeEvent.hasPropertyChanged("htmlContentAllowed")) {
            // Set text
            if (getState().htmlContentAllowed) {
                getWidget().setHtml(getState().caption);
            } else {
                getWidget().setText(getState().caption);
            }
        }

        if (getWidget().icon != null
                && stateChangeEvent.hasPropertyChanged("iconAltText")) {
            getWidget().icon.setAlternateText(getState().iconAltText);
        }

        getWidget().clickShortcut = getState().clickShortcutKeyCode;
        
        if (stateChangeEvent.hasPropertyChanged("labelVisible") || stateChangeEvent.hasPropertyChanged("labelPosition")) {
			getWidget().labelVisible = getState().labelVisible;
			getWidget().labelPosition = getState().labelPosition;

			getWidget().buildLabel();
        }
        
        if (stateChangeEvent.hasPropertyChanged("checked")) {
        	getWidget().setValue(getState().checked);
        }
    }

    @Override
    public SelectButtonState getState() {
        return (SelectButtonState) super.getState();
    }

    @Override
    public SelectButton getWidget() {
        return (SelectButton) super.getWidget();
    }

    @Override
    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).blur();
    }

    @Override
    public void onClick(ClickEvent event) {
        if (!isEnabled()) {
            return;
        }

        getState().checked = getWidget().getValue();

        // Add mouse details
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(), getWidget()
                        .getElement());
        getRpcProxy(SelectButtonServerRpc.class).setChecked(getState().checked,
                details);

    }
}
