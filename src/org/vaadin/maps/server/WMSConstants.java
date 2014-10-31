/**
 * 
 */
package org.vaadin.maps.server;

/**
 * @author kamil
 *
 */
public class WMSConstants {

	public static final String PARAM_SERVICE_WMS = "SERVICE=WMS";
	public static final String PARAM_VERSION = "VERSION";
	public static final String PARAM_REQUEST = "REQUEST";
	public static final String PARAM_LAYERS = "LAYERS";
	public static final String PARAM_STYLES = "STYLES";
	public static final String PARAM_SRS = "SRS";
	public static final String PARAM_CRS = "CRS";
	public static final String PARAM_BBOX = "BBOX";
	public static final String PARAM_WIDTH = "WIDTH";
	public static final String PARAM_HEIGHT = "HEIGHT";
	public static final String PARAM_FORMAT = "FORMAT";
	public static final String PARAM_TRANSPARENT = "TRANSPARENT";
	public static final String PARAM_BGCOLOR = "BGCOLOR";

	public static final String DEFAULT_VERSION = "1.1.1";
	public static final String DEFAULT_FORMAT = "image/jpeg";
	public static final String DEFAULT_SRS = "EPSG:4326";
	public static final int DEFAULT_WIDTH = 255;
	public static final int DEFAULT_HEIGHT = 255;
	
	public static final String GET_CAPABILITIES = "GetCapabilities";
	public static final String GET_MAP = "GetMap";
	public static final String GET_FEATURE_INFO = "GetFeatureInfo";

}
