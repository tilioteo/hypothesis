/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.control.DrawFeatureControl;
import org.vaadin.maps.ui.control.NavigateControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.handler.PathHandler.FinishStrategy;
import org.vaadin.maps.ui.layer.Layer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.ui.DrawLineControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPolygonControl;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;
import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MapUtility implements Serializable {
	
	private static HashMap<SlideFascia, MapUtility> hashMap = new HashMap<SlideFascia, MapUtility>();
	
	public static MapUtility newInstance(SlideFascia slideFascia, Map map) {
		MapUtility utility = new MapUtility(map);
		hashMap.put(slideFascia, utility);
		
		return utility;
	}
	
	public static MapUtility getInstance(SlideFascia slideFascia) {
		return hashMap.get(slideFascia);
	}
	
	public static void remove(SlideFascia slideFascia) {
		hashMap.remove(slideFascia);
	}
	
	private Map map = null;
	
	protected MapUtility(Map map) {
		this.map = map;
	}
	
	/*public final Map getMap() {
		return map;
	}*/
	
	public void setCommonProperties(Component component, Element element, StringMap properties) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(SlideXmlUtility.getId(element));
	}

	public void setLayerProperties(Layer layer, Element element, StringMap properties) {
		setCommonProperties(layer, element, properties);
		
		// TODO
	}

	public void setFeatureLayerProperties(VectorFeatureLayer layer, Element element, StringMap properties) {
		setFeatureLayerStyle(layer, properties);		
		setFeatureLayerHoverStyle(layer, properties);		
	}

	public void setImageSequenceLayerProperties(ImageSequenceLayer layer, Element element, StringMap properties) {
		setImageSequenceLayerSources(layer, element);
	}

	private void setImageSequenceLayerSources(ImageSequenceLayer layer, Element element) {
		List<Element> images = SlideXmlUtility.getImages(element);
		for (Element image : images) {
			String url = image.attributeValue(SlideXmlConstants.URL);
			String tag = image.getTextTrim();
			
			layer.addSource(url, tag);
		}
	}
	
	public void setControlProperties(Control control, Element element, StringMap properties) {
		setCommonProperties(control, element, properties);
	}

	public void setNavigateControlProperties(NavigateControl<?> control, Element element, StringMap properties) {
		setControlProperties(control, element, properties);
	}

	public void setDrawFeatureControlProperties(DrawFeatureControl<?> control, Element element,
			StringMap properties, SlideFascia slideFascia) {
		
		setControlProperties(control, element, properties);
		setFeatureStyle(control, properties);
		setCursorStyle(control, properties);
		
		SlideComponent component = slideFascia.getComponent(properties.get(SlideXmlConstants.LAYER_ID));
		if (component != null && component instanceof VectorFeatureLayer) {
			control.setLayer((VectorFeatureLayer) component);
		}
	}

	public void setDrawPathControlProperties(DrawPathControl control, Element element,
			StringMap properties, SlideFascia slideFascia) {
		
		setStartPointStyle(control, properties);
		setLineStyle(control, properties);
		setVertexStyle(control, properties);
		setFinishStrategy(control, properties);
	}

	public void setDrawLineControlProperties(DrawLineControl control, Element element,
			StringMap properties, SlideFascia slideFascia) {
		
		setStartPointStyle(control, properties);
		setLineStyle(control, properties);
	}

	public void setDrawPolygonControlProperties(DrawPolygonControl control, Element element,
			StringMap properties, SlideFascia slideFascia) {
		
		setStartPointStyle(control, properties);
		setLineStyle(control, properties);
		setVertexStyle(control, properties);
		setFinishStrategy(control, properties);
	}

	private void setFeatureStyle(DrawFeatureControl<?> control,	StringMap properties) {
		String styleId = properties.get(SlideXmlConstants.FEATURE_STYLE);
		if (styleId != null && map != null) {
			Style style = map.getStyle(styleId);
			if (style != null) {
				control.setFeatureStyle(style);
				return;
			}
		}
		control.setFeatureStyle(null);
	}

	private void setCursorStyle(DrawFeatureControl<?> control, StringMap properties) {
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
	
	private void setStartPointStyle(DrawPathControl control, StringMap properties) {
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
	
	private void setStartPointStyle(DrawLineControl control, StringMap properties) {
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
	
	private void setStartPointStyle(DrawPolygonControl control, StringMap properties) {
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
	
	private void setVertexStyle(DrawPolygonControl control, StringMap properties) {
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
	
	private void setVertexStyle(DrawPathControl control, StringMap properties) {
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
	
	private void setFinishStrategy(DrawPolygonControl control, StringMap properties) {
		FinishStrategy strategy = stringToFinishStrategy(properties.get(SlideXmlConstants.FINISH_STRATEGY));
		if (strategy != null) {
			control.setStrategy(strategy);
		} else {
			control.setStrategy(FinishStrategy.AltClick);
		}
	}

	private void setFinishStrategy(DrawPathControl control, StringMap properties) {
		FinishStrategy strategy = stringToFinishStrategy(properties.get(SlideXmlConstants.FINISH_STRATEGY));
		if (strategy != null) {
			control.setStrategy(strategy);
		} else {
			control.setStrategy(FinishStrategy.AltClick);
		}
	}

	private FinishStrategy stringToFinishStrategy(String string) {
		if (string != null) {
			for (FinishStrategy strategy : FinishStrategy.values()) {
				if (strategy.name().equalsIgnoreCase(string)) {
					return strategy;
				}
			}
		}
		return null;
	}

	private void setLineStyle(DrawPathControl control, StringMap properties) {
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
	
	private void setLineStyle(DrawLineControl control, StringMap properties) {
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
	
	private void setLineStyle(DrawPolygonControl control, StringMap properties) {
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
	
	public void setFeatureProperties(VectorFeature vectorFeature,
			Element element, StringMap properties) {
		setCommonProperties(vectorFeature, element, properties);
		setGeometry(vectorFeature, element);
		setFeatureStyle(vectorFeature, properties);
		setFeatureHoverStyle(vectorFeature, properties);
		vectorFeature.setHidden(properties.getBoolean(SlideXmlConstants.HIDDEN, false));
	}

	public void setFeatureText(VectorFeature vectorFeature, Element element) {
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
	
	private Double getAttributeDouble(Element element, String name) {
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
	
	private void setFeatureStyle(VectorFeature vectorFeature, StringMap properties) {
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

	private void setFeatureHoverStyle(VectorFeature vectorFeature, StringMap properties) {
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

	private void setFeatureLayerStyle(VectorFeatureLayer vectorFeatureLayer, StringMap properties) {
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

	private void setFeatureLayerHoverStyle(VectorFeatureLayer vectorFeatureLayer, StringMap properties) {
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

	private void setGeometry(VectorFeature vectorFeature, Element element) {
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
