/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.control.DrawFeatureControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.layer.Layer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;
import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author kamil
 *
 */
public class MapUtility {
	private static Map map = null;
	
	public static final void setMap(Map map) {
		MapUtility.map = map;
	}
	public static final Map getMap() {
		return map;
	}
	
	public static void setCommonProperties(Component component, Element element, StringMap properties) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(SlideXmlUtility.getId(element));
	}

	public static void setLayerProperties(Layer layer, Element element, StringMap properties) {
		setCommonProperties(layer, element, properties);
		
		// TODO
	}

	public static void setFeatureLayerProperties(VectorFeatureLayer layer, Element element, StringMap properties) {
		setFeatureLayerStyle(layer, properties);		
		setFeatureLayerHoverStyle(layer, properties);		
	}

	public static void setImageSequenceLayerProperties(ImageSequenceLayer layer, Element element, StringMap properties) {
		setImageSequenceLayerSources(layer, element);
	}

	private static void setImageSequenceLayerSources(ImageSequenceLayer layer, Element element) {
		List<Element> images = SlideXmlUtility.getImages(element);
		for (Element image : images) {
			String url = image.attributeValue(SlideXmlConstants.URL);
			String tag = image.getTextTrim();
			
			layer.addSource(url, tag);
		}
	}
	public static void setControlProperties(Control control, Element element, StringMap properties) {
		setCommonProperties(control, element, properties);
	}

	public static void setDrawFeatureControlProperties(DrawFeatureControl<?> control, Element element,
			StringMap properties, SlideManager slideManager) {
		
		setControlProperties(control, element, properties);
		setCursorStyle(control, properties);
		
		SlideComponent component = slideManager.getComponent(properties.get(SlideXmlConstants.LAYER_ID));
		if (component != null && component instanceof VectorFeatureLayer) {
			control.setLayer((VectorFeatureLayer) component);
		}
	}

	public static void setDrawPathControlProperties(DrawPathControl control, Element element,
			StringMap properties, SlideManager slideManager) {
		
		setStartPointStyle(control, properties);
		setLineStyle(control, properties);
		setVertexStyle(control, properties);
	}

	private static void setCursorStyle(DrawFeatureControl<?> control, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.CURSOR_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				control.setCursorStyle(style);
				return;
			}
		}
		control.setCursorStyle(Style.DEFAULT_DRAW_CURSOR);
	}
	
	private static void setStartPointStyle(DrawPathControl control, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.START_POINT_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				control.setStartPointStyle(style);
				return;
			}
		}
		control.setStartPointStyle(Style.DEFAULT_DRAW_START_POINT);
	}
	
	private static void setVertexStyle(DrawPathControl control, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.VERTEX_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				control.setVertexStyle(style);
				return;
			}
		}
		control.setVertexStyle(Style.DEFAULT_DRAW_VERTEX);
	}
	
	private static void setLineStyle(DrawPathControl control, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.LINE_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				control.setLineStyle(style);
				return;
			}
		}
		control.setLineStyle(Style.DEFAULT_DRAW_LINE);
	}
	
	public static void setFeatureProperties(VectorFeature vectorFeature,
			Element element, StringMap properties) {
		setCommonProperties(vectorFeature, element, properties);
		setGeometry(vectorFeature, element);
		setFeatureStyle(vectorFeature, properties);
		setFeatureHoverStyle(vectorFeature, properties);
		vectorFeature.setHidden(properties.getBoolean(SlideXmlConstants.HIDDEN, false));
	}

	public static void setFeatureText(VectorFeature vectorFeature, Element element) {
		Element textElement = SlideXmlUtility.getTextElement(element);
		if (textElement != null) {
			String value = SlideXmlUtility.getValue(textElement);
			if (!Strings.isNullOrEmpty(value)) {
				vectorFeature.setText(value);
			}
			
			Element offsetElement = SlideXmlUtility.getOffsetElement(textElement);
			if (offsetElement != null) {
				Double offsetX = getAttributeDouble(offsetElement, SlideXmlConstants.X);
				Double offsetY = getAttributeDouble(offsetElement, SlideXmlConstants.Y);
				vectorFeature.setTextOffset(offsetX != null ? offsetX : 0.0, offsetY != null ? offsetY : 0.0);
			}
		}
		
	}
	
	private static Double getAttributeDouble(Element element, String name) {
		String value = element.attributeValue(name);
		if (value != null) {
			try {
				Double doubleValue = Double.parseDouble(value);
				return doubleValue;
			} catch (Throwable e) {
			}
		}
		
		return null;
	}
	
	public static HashMap<String, String> getStyleMap(Style style) {
		if (style != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			
			Field[] fields = style.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
					try {
						map.put(field.getName(), field.get(style).toString());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return map;
		}
		return null;
	}

	private static void setFeatureStyle(VectorFeature vectorFeature, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				vectorFeature.setStyle(style);
				return;
			}
		}
		vectorFeature.setStyle(null);
	}

	private static void setFeatureHoverStyle(VectorFeature vectorFeature, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.HOVER_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				vectorFeature.setHoverStyle(style);
				return;
			}
		}
		vectorFeature.setHoverStyle(null);
	}

	private static void setFeatureLayerStyle(VectorFeatureLayer vectorFeatureLayer, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				vectorFeatureLayer.setStyle(style);
				return;
			}
		}
		vectorFeatureLayer.setStyle(Style.DEFAULT);
	}

	private static void setFeatureLayerHoverStyle(VectorFeatureLayer vectorFeatureLayer, StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.HOVER_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				vectorFeatureLayer.setHoverStyle(style);
				return;
			}
		}
		vectorFeatureLayer.setHoverStyle(null);
	}

	private static void setGeometry(VectorFeature vectorFeature, Element element) {
		Element geometryElement = SlideXmlUtility.getGeometryElement(element);
		if (geometryElement != null) {
			String value = SlideXmlUtility.getValue(geometryElement);
			if (!Strings.isNullOrEmpty(value)) {
				try {
					WKTReader wktReader = new WKTReader();
					Geometry geometry = wktReader.read(value);
				 	vectorFeature.setGeometry(geometry);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
