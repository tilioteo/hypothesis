/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.util.Locale;

import org.dom4j.Element;

import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class SlideFactory {

	public static void writeImageLayerData(Element sourceElement, ImageLayerData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.IMAGE_LAYER);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		if (data.hasCoordinate()) {
			writeCoordinate(sourceElement, data.getCoordinate());
		}
	}

	public static void writeVectorFeatureLayerData(Element sourceElement, VectorFeatureLayerData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.FEATURE_LAYER);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		if (data.hasCoordinate()) {
			writeCoordinate(sourceElement, data.getCoordinate());
		}
	}
	
	private static void writeCoordinate(Element sourceElement, Coordinate coordinate) {
		Element subElement = sourceElement.addElement(SlideXmlConstants.X);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.x));
		subElement = sourceElement.addElement(SlideXmlConstants.Y);
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.y));
	}

	public static void writeVectorFeatureData(Element sourceElement, VectorFeatureData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.FEATURE);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		Element subElement = sourceElement.addElement(SlideXmlConstants.GEOMETRY);
		subElement.addText(data.getSender().getGeometry().toText());
	}

}
