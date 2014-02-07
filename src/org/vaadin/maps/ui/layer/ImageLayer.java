/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.net.URL;

import org.vaadin.maps.shared.ui.layer.ImageLayerServerRpc;
import org.vaadin.maps.shared.ui.layer.ImageLayerState;
import org.vaadin.maps.ui.tile.ImageTile;
import org.vaadin.maps.ui.tile.ImageTile.ClickListener;
import org.vaadin.maps.ui.tile.ImageTile.LoadListener;

import com.vaadin.ui.Component.Focusable;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class ImageLayer extends AbstractLayer<ImageTile> implements Focusable {
	
	private ImageLayerServerRpc rpc = new ImageLayerServerRpc() {
		/*@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(ImageLayer.this, mouseDetails));
		}*/
	};
	
	public ImageLayer() {
		registerRpc(rpc);
		getState().tabIndex = -1;
	}

	public ImageLayer(URL imageURL) {
		this();
		ImageTile tile = new ImageTile(imageURL);
		tile.setSizeFull();
		setContent(tile);
	}
	
	public ImageLayer(String imageURL) {
		this();
		ImageTile tile = new ImageTile(imageURL);
		tile.setSizeFull();
		setContent(new ImageTile(imageURL));
	}

	@Override
	public boolean isBase() {
		return true;
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

}