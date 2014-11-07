/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.server.WMSConstants;
import org.vaadin.maps.ui.tile.WMSTile;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSLayer extends GridLayer<WMSTile> {
	
	private String baseUrl = "";
	private int tileWidth = WMSConstants.DEFAULT_WIDTH;
	private int tileHeight = WMSConstants.DEFAULT_HEIGHT;
	private String srs = WMSConstants.DEFAULT_SRS;
	private String format = WMSConstants.DEFAULT_FORMAT;
	private String styles = "";
	private String bbox = "";
	private String layers = "";
	
	
	private int visibleWidth = 0;
	private int visibleHeight = 0;
	
	private boolean singleTile = true;
	
	public WMSLayer() {
		super();
	}

	public WMSLayer(String baseUrl) {
		this();
		setBaseUrl(baseUrl);
	}

	@Override
	public boolean isBase() {
		return true;
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

	public String getSRS() {
		return srs;
	}

	public void setSRS(String srs) {
		this.srs = srs;
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

	public String getBBox() {
		return bbox;
	}

	public void setBBox(String bbox) {
		this.bbox = bbox;
	}

	public String getLayers() {
		return layers;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}

	private void rebuildTiles() {
		getGrid().removeAllComponents();
		
		if (!Strings.isNullOrEmpty(baseUrl) && visibleWidth > 0 && visibleHeight > 0) {
			if (singleTile) {
				tileWidth = 2*visibleWidth;
				tileHeight = 2*visibleHeight;
				
				WMSTile tile = createTile();
				
				getGrid().addComponent(tile);
			} else {
				// TODO 
			}
		}
	}

	private WMSTile createTile() {
		WMSTile tile = new WMSTile(baseUrl);
		tile.setLayers(layers);
		tile.setWidth(tileWidth);
		tile.setHeight(tileHeight);
		tile.setSRS(srs);
		tile.setStyles(styles);
		tile.setBBox(bbox);
		tile.setFormat(format);
		
		return tile;
	}

	public boolean isSingleTile() {
		return singleTile;
	}

	/*public void setSingleTile(boolean singleTile) {
		if (this.singleTile != singleTile) {
			this.singleTile = singleTile;
			rebuildTiles();
		}
	}*/
	
	@Override
	public void sizeChanged(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		visibleWidth = newWidth;
		visibleHeight = newHeight;
		
		rebuildTiles();
	}

	
}
