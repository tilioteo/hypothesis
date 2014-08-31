/**
 * 
 */
package org.vaadin.maps.server;

import com.vaadin.server.ExternalResource;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSResource extends ExternalResource implements TileResource {

	private String baseUrl = "";
	private int width = WMSConstants.DEFAULT_WIDTH;
	private int height = WMSConstants.DEFAULT_HEIGHT;
	private String version = WMSConstants.DEFAULT_VERSION;
	private String format = WMSConstants.DEFAULT_FORMAT;
	private String layers = "";
	private String styles = "";
	private String srs = WMSConstants.DEFAULT_SRS;
	private String bbox = "";
	private boolean transparent = false;
	

	public WMSResource(String baseURL) {
		super(baseURL);
		this.baseUrl = baseURL;
	}
	
	/*public WMSResource(String baseURL, String version, String format, String srs, String layers, String styles, String bbox, int width, int height) {
		this(baseURL);
		setVersion(version);
		setFormat(format);
		setSRS(srs);
		setLayers(layers);
		setStyles(styles);
		setBBox(bbox);
		setWidth(width);
		setHeight(height);
	}*/

    /**
     * Gets the URL of the external resource.
     * 
     * @return the URL of the external resource.
     */
    public String getURL() {
        return buildRequest();
    }

	@Override
	public String getMIMEType() {
		return format;
	}
	
	private String buildRequest() {
		StringBuilder builder = new StringBuilder(baseUrl);
		if (!baseUrl.endsWith("?")) {
			builder.append("?");
		}
		builder.append(WMSConstants.PARAM_SERVICE_WMS);
		builder.append("&" + WMSConstants.PARAM_VERSION + version);
		builder.append("&" + WMSConstants.PARAM_LAYERS + layers);
		builder.append("&" + WMSConstants.PARAM_REQUEST + WMSConstants.GET_MAP);
		
		if (version.startsWith("1.1")) {
			builder.append("&" + WMSConstants.PARAM_SRS);
		} else {
			builder.append("&" + WMSConstants.PARAM_CRS);
		}
		builder.append(srs);
		builder.append("&" + WMSConstants.PARAM_BBOX + bbox);
		builder.append("&" + WMSConstants.PARAM_WIDTH + width);
		builder.append("&" + WMSConstants.PARAM_HEIGHT + height);
		builder.append("&" + WMSConstants.PARAM_FORMAT + format);
		builder.append("&" + WMSConstants.PARAM_STYLES + styles);
		
		if (transparent) {
			builder.append("&" + WMSConstants.PARAM_TRANSPARENT + "TRUE");
		}
		
		return builder.toString();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (this.width != width) {
			if (width > 0) {
				this.width = width;
			} else {
				this.width = WMSConstants.DEFAULT_WIDTH;
			}
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (this.height != height) {
			if (height > 0) {
				this.height = height;
			} else {
				this.height = WMSConstants.DEFAULT_HEIGHT;
			}
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if (this.version != version) {
			if (version != null && !version.isEmpty()) {
				this.version = version;
			} else {
				this.version = WMSConstants.DEFAULT_VERSION;
			}
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		if (this.format != format) {
			if (format != null && !format.isEmpty()) {
				this.format = format;
			} else {
				this.format = WMSConstants.DEFAULT_FORMAT;
			}
		}
	}

	public String getLayers() {
		return layers;
	}

	public void setLayers(String layers) {
		if (this.layers != layers) {
			if (layers != null && !layers.isEmpty()) {
				this.layers = layers;
			} else {
				this.layers = "";
			}
		}
	}

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		if (this.styles != styles) {
			if (styles != null && !styles.isEmpty()) {
				this.styles = styles;
			} else {
				this.styles = "";
			}
		}
	}

	public String getSRS() {
		return srs;
	}

	public void setSRS(String srs) {
		if (this.srs != srs) {
			if (srs != null && !srs.isEmpty()) {
				this.srs = srs;
			} else {
				this.srs = WMSConstants.DEFAULT_SRS;
			}
		}
	}

	public String getBBox() {
		return bbox;
	}

	public void setBBox(String bbox) {
		if (this.bbox != bbox) {
			if (bbox != null && !bbox.isEmpty()) {
				this.bbox = bbox;
			} else {
				this.bbox = "";
			}
		}
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public String getBaseUrl() {
		return baseUrl;
	}
	
}
