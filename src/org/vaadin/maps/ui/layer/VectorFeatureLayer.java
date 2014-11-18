/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.util.Iterator;

import com.vaadin.ui.Component.Focusable;

import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.shared.ui.layer.VectorFeatureLayerServerRpc;
import org.vaadin.maps.shared.ui.layer.VectorFeatureLayerState;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.ClickListener;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class VectorFeatureLayer extends AbstractLayer<VectorFeatureContainer> implements Focusable {

	private VectorFeatureLayerServerRpc rpc = new VectorFeatureLayerServerRpc() {
		/*@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(VectorFeatureLayer.this, mouseDetails));
		}*/
	};
	
	private Style style = null;
	private Style hoverStyle = null;
	
	public VectorFeatureLayer() {
		registerRpc(rpc);
		VectorFeatureContainer container = new VectorFeatureContainer();
		container.setSizeFull();
		setContent(container);
		getContent().setSizeFull();
		getState().tabIndex = -1;
		
		setStyle(null);
	}
	
	@Override
	protected VectorFeatureLayerState getState() {
		return (VectorFeatureLayerState) super.getState();
	}
	
	@Override
	public boolean isBase() {
		return false;
	}

	public void setFixed(boolean fixed) {
		getState().fixed = fixed;
	}
	
	@Override
	public boolean isFixed() {
		return getState().fixed;
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
     * Add a feature into this layer.
     * 
     * @param feature
     *            the feature to be added.
     */
    public void addComponent(VectorFeature feature) {
    	getContent().addComponent(feature);
    	
    	setInheritedStyles(feature);
    }

	/**
     * Adds a feature into indexed position in this layer.
     * 
     * @param feature
     *            the feature to be added.
     * @param index
     *            the index of the feature position. The features currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(VectorFeature feature, int index) {
    	getContent().addComponent(feature, index);
    	
    	setInheritedStyles(feature);
    }

    /**
     * Adds the features in the given order to this layer.
     * 
     * @param features
     *            The features to add.
     */
    public void addComponents(VectorFeature... features) {
    	getContent().addComponents(features);
    	
    	for (VectorFeature feature : features) {
    		setInheritedStyles(feature);
    	}
    }

    private void setInheritedStyles(VectorFeature feature) {
    	if (feature != null) {
   			feature.setInheritedStyle(style);
   			feature.setInheritedHoverStyle(hoverStyle);
    	}
	}

    /**
     * Removes the feature from this layer.
     * 
     * @param feature
     *            the feature to be removed.
     */
    public void removeComponent(VectorFeature feature) {
    	getContent().removeComponent(feature);
    }

    /**
     * Removes all features from this layer.
     */
    public void removeAllComponents() {
    	getContent().removeAllComponents();
    }

    /**
     * Gets the typed feature container iterator for going trough all the features
     * in the layer.
     * 
     * @return the Iterator of the features inside the layer.
     */
	public Iterator<VectorFeature> featureIterator() {
        return getContent().typedIterator();
	}

	/**
     * Gets the number of contained features. Consistent with the iterator
     * returned by {@link #featureIterator()}.
     * 
     * @return the number of contained features
     */
    public int getFeatureCount() {
        return getContent().getComponentCount();
    }

    /**
     * Replaces the feature in the layer with another one without changing
     * position.
     * 
     * <p>
     * This method replaces feature with another one is such way that the new
     * feature overtakes the position of the old feature. If the old
     * feature is not in the layer, the new feature is added to the
     * layer. If the both feature are already in the layer, their
     * positions are swapped. Component attach and detach events should be taken
     * care as with add and remove.
     * </p>
     * 
     * @param oldFeature
     *            the old feature that will be replaced.
     * @param newFeature
     *            the new feature to be replaced.
     */
    public void replaceComponent(VectorFeature oldFeature, VectorFeature newFeature) {
    	getContent().replaceComponent(oldFeature, newFeature);
    }
    
    /**
     * Returns the index of the given feature.
     * 
     * @param feature
     *            The feature to look up.
     * @return The index of the feature or -1 if the feature is not a child.
     */
    public int getComponentIndex(VectorFeature feature) {
        return getContent().getComponentIndex(feature);
    }

    /**
     * Returns the feature at the given position.
     * 
     * @param index
     *            The position of the feature.
     * @return The feature at the given index.
     * @throws IndexOutOfBoundsException
     *             If the index is out of range.
     */
    public VectorFeature getComponent(int index) throws IndexOutOfBoundsException {
        return getContent().getComponent(index);
    }

	/**
	 * Adds the layers container click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		getContent().addClickListener(listener);
	}

	/**
	 * Removes the container click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		getContent().removeClickListener(listener);
	}

	public Style getStyle() {
		return style;
	}
	
	public void setStyle(Style style) {
		if (this.style != style) {
			this.style = style;

			updateFeaturesInheritedStyle();
		}
	}
	
	private void updateFeaturesInheritedStyle() {
		for (Iterator<VectorFeature> iterator = featureIterator(); iterator.hasNext();) {
			iterator.next().setInheritedStyle(style);
		}
	}

	public Style getHoverStyle() {
		return hoverStyle;
	}

	public void setHoverStyle(Style style) {
		if (hoverStyle != style) {
			hoverStyle = style;
			
			updateFeaturesInheritedHoverStyle();
		}
	}
	
	private void updateFeaturesInheritedHoverStyle() {
		for (Iterator<VectorFeature> iterator = featureIterator(); iterator.hasNext();) {
			iterator.next().setInheritedHoverStyle(style);
		}
	}

}
