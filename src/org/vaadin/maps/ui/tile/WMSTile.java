/**
 * 
 */
package org.vaadin.maps.ui.tile;

import org.vaadin.maps.server.WMSResource;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSTile extends AbstractProxyTile<WMSResource> {
	
	private WMSResource resource;
	private ClippedSizeHandler sizeHandler = null;
	
	public WMSTile(String baseURL) {
		super();
		
		resource = new WMSResource(baseURL);
		setSource(resource);
	}
	
	public void updateSource() {
		setSource(resource);
	}
	
	public void setWidth(int width) {
		setWidth(width, Unit.PIXELS);
		resource.setWidth(width);
		updateSource();
	}
	
	public void setHeight(int height) {
		setHeight(height, Unit.PIXELS);
		resource.setHeight(height);
		updateSource();
	}
	
	public void setVersion(String version) {
		resource.setVersion(version);
		updateSource();
	}
	
	public String getVersion() {
		return resource.getVersion();
	}
	
	public void setFormat(String format) {
		resource.setFormat(format);
		updateSource();
	}
	
	public String getFormat() {
		return resource.getFormat();
	}
	
	public void setLayers(String layers) {
		resource.setLayers(layers);
		updateSource();
	}
	
	public String getLayers() {
		return resource.getLayers();
	}
	
	public void setStyles(String styles) {
		resource.setStyles(styles);
		updateSource();
	}
	
	public String getStyles() {
		return resource.getStyles();
	}
	
	public void setSRS(String srs) {
		resource.setSRS(srs);
		updateSource();
	}
	
	public String getSRS() {
		return resource.getSRS();
	}
	
	public void setBBox(String bbox) {
		resource.setBBox(bbox);
		updateSource();
	}
	
	public String getBBox() {
		return resource.getBBox();
	}
	
	public void setTransparent(boolean transparent) {
		resource.setTransparent(transparent);
		updateSource();
	}
	
	public boolean isTransparent() {
		return resource.isTransparent();
	}
	
	public String getBaseUrl() {
		return resource.getBaseUrl();
	}
	
	public ClippedSizeHandler getSizeHandler() {
		return sizeHandler;
	}

	public void setSizeHandler(ClippedSizeHandler sizeHandler) {
		this.sizeHandler = sizeHandler;
	}

	@Override
	protected void clippedSizeChanged(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		if (sizeHandler != null) {
			sizeHandler.onSizeChange(oldWidth, oldHeight, newWidth, newHeight);
		}
	}
}
