/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import org.dom4j.Element;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.control.DrawFeatureControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.layer.Layer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideManager;
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
	
	public static void setCommonProperties(Component component, Element element, StringMap properties) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(com.tilioteo.hypothesis.dom.SlideXmlUtility.getId(element));
	}

	public static void setLayerProperties(Layer layer, Element element, StringMap properties) {
		setCommonProperties(layer, element, properties);
		
		// TODO
	}

	public static void setControlProperties(Control control, Element element, StringMap properties) {
		setCommonProperties(control, element, properties);
		
	}

	public static void setDrawFeatureControlProperties(DrawFeatureControl<?> control, Element element,
			StringMap properties, SlideManager slideManager) {
		
		setControlProperties(control, element, properties);
		
		SlideComponent component = slideManager.getComponent(properties.get(SlideXmlConstants.LAYER_ID));
		if (component != null && component instanceof VectorFeatureLayer) {
			control.setLayer((VectorFeatureLayer) component);
		}
	}

	public static void setFeatureProperties(VectorFeature vectorFeature,
			Element element/*, StringMap properties*/) {
		
		setGeometry(vectorFeature, element);
		
	}

	private static void setGeometry(VectorFeature vectorFeature, Element element) {
		Element geometryElement = SlideXmlUtility.getGeometryElement(element);
		if (geometryElement != null) {
			String value = com.tilioteo.hypothesis.dom.SlideXmlUtility.getValue(geometryElement);
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
