/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.image;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.tilioteo.hypothesis.client.ui.VImage;
import com.tilioteo.hypothesis.shared.ui.image.ImageServerRpc;
import com.tilioteo.hypothesis.shared.ui.image.ImageState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Image.class)
public class ImageConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().addHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                getLayoutManager().setNeedsMeasure(ImageConnector.this);
                getRpcProxy(ImageServerRpc.class).load();
            }

        }, LoadEvent.getType());
    }

    @Override
    public VImage getWidget() {
        return (VImage) super.getWidget();
    }

    @Override
    public ImageState getState() {
        return (ImageState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        String url = getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE);
        getWidget().setUrl(url != null ? url : "");

        String alt = getState().alternateText;
        // Some browsers turn a null alt text into a literal "null"
        getWidget().setAltText(alt != null ? alt : "");
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(ImageServerRpc.class).click(mouseDetails);
        }

    };

}
