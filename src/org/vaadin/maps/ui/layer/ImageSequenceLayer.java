/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.net.URL;

import org.vaadin.maps.shared.ui.layer.ImageSequenceLayerState;
import org.vaadin.maps.ui.tile.ImageSequenceTile;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeListener;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ClickListener;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ErrorListener;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadListener;

import com.vaadin.ui.Component.Focusable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ImageSequenceLayer extends AbstractLayer<ImageSequenceTile> implements Focusable {
	
	private ImageSequenceTile tile = null;

	public ImageSequenceLayer() {
		//registerRpc(rpc);
		getState().tabIndex = -1;
		initTile();
	}
	
	private void initTile() {
		tile = new ImageSequenceTile();
		tile.setSizeFull();
		setContent(tile);
	}

	@Override
	public boolean isBase() {
		return true;
	}

	@Override
	protected ImageSequenceLayerState getState() {
		return (ImageSequenceLayerState) super.getState();
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
    
    public void addTileUrl(String url) {
    	tile.addResource(url);
    }
    
    public void addTileUrl(URL url) {
    	tile.addResource(url);
    }
    
    public void clearTile() {
    	tile.clear();
    }
    
    public void setTileIndex(int index) {
    	tile.setIndex(index);
    }
    
    public int getTileIndex() {
    	return tile.getIndex();
    }

	/**
	 * Adds the tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addLoadListener(LoadListener listener) {
		getContent().addLoadListener(listener);
	}

	/**
	 * Removes the tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeLoadListener(LoadListener listener) {
		getContent().removeLoadListener(listener);
	}

	/**
	 * Adds the tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addErrorListener(ErrorListener listener) {
		getContent().addErrorListener(listener);
	}

	/**
	 * Removes the tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeErrorListener(ErrorListener listener) {
		getContent().removeErrorListener(listener);
	}

	/**
	 * Adds the tile change listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addChangeListener(ChangeListener listener) {
		getContent().addChangeListener(listener);
	}

	/**
	 * Removes the tile change listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeChangeListener(ChangeListener listener) {
		getContent().removeChangeListener(listener);
	}

	/**
	 * Add a click listener to the component. The listener is called whenever
	 * the user clicks inside the component. Depending on the content the event
	 * may be blocked and in that case no event is fired.
	 * 
	 * Use {@link #removeClickListener(ClickListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addClickListener(ClickListener listener) {
		getContent().addClickListener(listener);
	}

	/**
	 * Remove a click listener from the component. The listener should earlier
	 * have been added using {@link #addClickListener(ClickListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeClickListener(ClickListener listener) {
		getContent().removeClickListener(listener);
	}

}
