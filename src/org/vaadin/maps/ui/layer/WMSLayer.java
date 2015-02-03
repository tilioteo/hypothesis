/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.server.Bounds;
import org.vaadin.maps.server.Size;
import org.vaadin.maps.server.WMSConstants;
import org.vaadin.maps.shared.ui.layer.WMSLayerServerRpc;
import org.vaadin.maps.shared.ui.layer.WMSLayerState;
import org.vaadin.maps.ui.MapConstants;
import org.vaadin.maps.ui.tile.WMSTile;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSLayer extends GridLayer<WMSTile> {
	
	private String baseUrl = "";
	private String format = WMSConstants.DEFAULT_FORMAT;
	private String styles = "";
	private String layers = "";

	private Size tileSize = new Size(WMSConstants.DEFAULT_WIDTH, WMSConstants.DEFAULT_HEIGHT);
	private Size visible = new Size(); 
	
	private Bounds bounds = new Bounds();
	
	private WMSLayerServerRpc rpc = new WMSLayerServerRpc() {
		@Override
		public void requestSingleTile(int width, int height, int shiftX, int shiftY) {
			WMSLayer.this.requestSingleTile(width, height, shiftX, shiftY);
		}
	};
	
	public WMSLayer() {
		super();
		registerRpc(rpc);
	}

	public WMSLayer(String baseUrl) {
		this();
		setBaseUrl(baseUrl);
	}

	@Override
	protected WMSLayerState getState() {
		return (WMSLayerState)super.getState();
	}

	@Override
	public boolean isBase() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return false;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		if (this.baseUrl != baseUrl) {
			this.baseUrl = baseUrl;
			rebuildTiles();
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

	public String getLayers() {
		return layers;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}

	private void rebuildTiles() {
		
		getGrid().removeAllComponents();
		
		if (!Strings.isNullOrEmpty(baseUrl) && visible.isValid()) {
			
			if (getState().singleTile) {
				getGrid().setRows(1);
				getGrid().setColumns(1);
				
				tileSize.setWidth(2*visible.getWidth());
				tileSize.setHeight(2*visible.getHeight());
				
				WMSTile tile = createTile();
				
				getGrid().addComponent(tile);
			} else {
				// TODO implement grid arrangement
			}
		}
	}
	
	private void requestSingleTile(int width, int height, int shiftX, int shiftY) {
		rebuildTiles();
	}

	private WMSTile createTile() {
		String crs = MapConstants.DEFAULT_CRS;
		if (getForLayer() != null) {
			if (getForLayer().getCRS() != null) {
				crs = getForLayer().getCRS();
			}
			bounds = getForLayer().getExtent();
		}
		
		WMSTile tile = new WMSTile(baseUrl);
		tile.setLayers(layers);
		tile.setWidth(tileSize.getWidth());
		tile.setHeight(tileSize.getHeight());
		tile.setSRS(crs);
		tile.setStyles(styles);
		tile.setFormat(format);
		tile.setBBox(bounds.scale(2).toBBOX());
		
		return tile;
	}

	public boolean isSingleTile() {
		return getState().singleTile;
	}

	/*public void setSingleTile(boolean singleTile) {
		if (getState().singleTile != singleTile) {
			getState().singleTile = singleTile;
			rebuildTiles();
		}
	}*/
	
	@Override
	public void sizeChanged(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		visible.setWidth(newWidth);
		visible.setHeight(newHeight);
		
		if ((oldWidth <= 0 && newWidth > oldWidth) || (oldHeight <= 0 && newHeight > oldHeight)) {
			rebuildTiles();
		}
	}
	
}
