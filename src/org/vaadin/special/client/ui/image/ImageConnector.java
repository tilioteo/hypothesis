/**
 * 
 */
package org.vaadin.special.client.ui.image;

import org.vaadin.special.client.DateUtility;
import org.vaadin.special.client.ui.VImage;
import org.vaadin.special.shared.ui.image.ImageServerRpc;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.image.ImageState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.Image.class)
public class ImageConnector extends AbstractComponentConnector implements LoadHandler, ErrorHandler {

	@Override
	protected void init() {
		super.init();
		getWidget().addLoadHandler(this);
		getWidget().addErrorHandler(this);
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

	protected final ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
		@Override
		protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
			getRpcProxy(ImageServerRpc.class).click(DateUtility.getTimestamp(), mouseDetails);
		}
	};

	@Override
	public void onLoad(LoadEvent event) {
		getLayoutManager().setNeedsMeasure(ImageConnector.this);
        getRpcProxy(ImageServerRpc.class).load(DateUtility.getTimestamp());
	}

	@Override
	public void onError(ErrorEvent event) {
        getRpcProxy(ImageServerRpc.class).error(DateUtility.getTimestamp());
	}

}
