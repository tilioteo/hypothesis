/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import org.dom4j.Element;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.feature.VectorFeature.ClickEvent;
import org.vaadin.maps.ui.feature.VectorFeature.ClickListener;
import org.vaadin.maps.ui.layer.Layer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author kamil
 *
 */
public class MapUtility {

	public static void setLayerProperties(Layer layer, Element element, StringMap properties) {
		
	}

	public static void setControlProperties(Layer layer, Element element, StringMap properties) {
		
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
