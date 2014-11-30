/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.net.URL;

import org.vaadin.maps.server.ImageResource;
import org.vaadin.maps.shared.ui.layer.ImageLayerServerRpc;
import org.vaadin.maps.shared.ui.layer.ImageLayerState;
import org.vaadin.maps.ui.MeasuredSizeHandler;
import org.vaadin.maps.ui.tile.ImageTile;
import org.vaadin.maps.ui.tile.AbstractProxyTile.ErrorListener;
import org.vaadin.maps.ui.tile.AbstractProxyTile.LoadListener;

import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Component.Focusable;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class ImageLayer extends AbstractLayer<ImageTile> implements Focusable, MeasuredSizeHandler {
	
	private ImageLayerServerRpc rpc = new ImageLayerServerRpc() {
		/*@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(ImageLayer.this, mouseDetails));
		}*/
	};
	
	private ImageTile tile = null;
	
	public ImageLayer() {
		registerRpc(rpc);
		getState().tabIndex = -1;
		getState().base = true;
		
		initTile();
	}
	
	private void initTile() {
		tile = new ImageTile();
		//tile.setSizeFull();
		setContent(tile);
	}

	public ImageLayer(URL imageURL) {
		this();
		setTileUrl(imageURL);
	}
	
	public ImageLayer(String imageURL) {
		this();
		setTileUrl(imageURL);
	}

	@Override
	public boolean isBase() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return getState().fixed;
	}
	
	public void setFixed(boolean fixed) {
		getState().fixed = fixed;
	}

	@Override
	protected ImageLayerState getState() {
		return (ImageLayerState) super.getState();
	}

	@Override
	public int getTabIndex() {
        return getState().tabIndex;
	}

	@Override
	public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
	}

    @Override
    public void focus() {
        super.focus();
    }
    
    public void setTileUrl(URL imageURL) {
    	tile.setSource(new ImageResource(imageURL));
    }

    public void setTileUrl(String imageURL) {
    	tile.setSource(new ImageResource(imageURL));
    }

	/**
	 * Adds the image tile click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		getContent().addClickListener(listener);
	}

	/**
	 * Removes the image tile click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		getContent().removeClickListener(listener);
	}

	/**
	 * Adds the image tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addLoadListener(LoadListener listener) {
		getContent().addLoadListener(listener);
	}

	/**
	 * Removes the image tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeLoadListener(LoadListener listener) {
		getContent().removeLoadListener(listener);
	}

	/**
	 * Adds the image tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addErrorListener(ErrorListener listener) {
		getContent().addErrorListener(listener);
	}

	/**
	 * Removes the image tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeErrorListener(ErrorListener listener) {
		getContent().removeErrorListener(listener);
	}

	@Override
	public void sizeChanged(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		
	}

}
