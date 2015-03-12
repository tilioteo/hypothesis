/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.util.Locale;

import org.dom4j.Element;

import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPointControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPolygonControlData;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ImageSequenceLayerData;
import com.tilioteo.hypothesis.plugin.map.event.PanControlData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.tilioteo.hypothesis.plugin.map.event.WMSLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ZoomControlData;
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
		if (data.hasWorldCoordinate()) {
			writeWorldCoordinate(sourceElement, data.getWorldCoordinate());
		}
	}

	public static void writeWMSLayerData(Element sourceElement, WMSLayerData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.WMS_LAYER);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		if (data.hasCoordinate()) {
			writeCoordinate(sourceElement, data.getCoordinate());
		}
		if (data.hasWorldCoordinate()) {
			writeWorldCoordinate(sourceElement, data.getWorldCoordinate());
		}
	}

	public static void writeImageSequenceLayerData(Element sourceElement, ImageSequenceLayerData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.IMAGE_SEQUENCE_LAYER);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		if (data.hasCoordinate()) {
			writeCoordinate(sourceElement, data.getCoordinate());
		}
		int index = data.getImageIndex();
		if (index > 0) {
			Element selectedElement = sourceElement.addElement(SlideXmlConstants.IMAGE);
			selectedElement.addAttribute(SlideXmlConstants.INDEX, String.format("%d", index));
			if (data.getImageTag() != null) {
				selectedElement.addText(data.getImageTag());
			}
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
		if (data.hasWorldCoordinate()) {
			writeWorldCoordinate(sourceElement, data.getWorldCoordinate());
		}
	}
	
	private static void writeCoordinate(Element sourceElement, Coordinate coordinate) {
		Element subElement = sourceElement.addElement(SlideXmlConstants.X);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.x));
		subElement = sourceElement.addElement(SlideXmlConstants.Y);
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.y));
	}

	private static void writeWorldCoordinate(Element sourceElement, Coordinate coordinate) {
		Element subElement = sourceElement.addElement(SlideXmlConstants.WORLD_X);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.x));
		subElement = sourceElement.addElement(SlideXmlConstants.WORLD_Y);
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.y));
	}

	private static void writeDelta(Element sourceElement, Coordinate coordinate) {
		Element subElement = sourceElement.addElement(SlideXmlConstants.DELTA_X);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.x));
		subElement = sourceElement.addElement(SlideXmlConstants.DELTA_Y);
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.y));
	}

	private static void writeWorldDelta(Element sourceElement, Coordinate coordinate) {
		Element subElement = sourceElement.addElement(SlideXmlConstants.WORLD_DELTA_X);
		// use Locale.ROOT for locale neutral formating of decimals
		subElement.addText(String.format(Locale.ROOT, "%g", coordinate.x));
		subElement = sourceElement.addElement(SlideXmlConstants.WORLD_DELTA_Y);
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

	public static void writeDrawPointControlData(Element sourceElement, DrawPointControlData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.DRAW_POINT);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		Element subElement = sourceElement.addElement(SlideXmlConstants.GEOMETRY);
		subElement.addText(data.getFeature().getGeometry().toText());
	}
	
	public static void writeDrawPathControlData(Element sourceElement, DrawPathControlData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.DRAW_PATH);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		Element subElement = sourceElement.addElement(SlideXmlConstants.GEOMETRY);
		subElement.addText(data.getFeature().getGeometry().toText());
	}
	
	public static void writeDrawPolygonControlData(Element sourceElement, DrawPolygonControlData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.DRAW_POLYGON);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		Element subElement = sourceElement.addElement(SlideXmlConstants.GEOMETRY);
		subElement.addText(data.getFeature().getGeometry().toText());
	}
	
	public static void writePanControlData(Element sourceElement, PanControlData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.PAN);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		if (data.hasCoordinate()) {
			writeCoordinate(sourceElement, data.getCoordinate());
		}
		if (data.hasWorldCoordinate()) {
			writeWorldCoordinate(sourceElement, data.getWorldCoordinate());
		}
		if (data.hasDelta()) {
			writeDelta(sourceElement, data.getDelta());
		}
		if (data.hasWorldDelta()) {
			writeWorldDelta(sourceElement, data.getWorldDelta());
		}
	}
	
	public static void writeZoomControlData(Element sourceElement, ZoomControlData data) {
		String id = data.getComponentId();
		sourceElement.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.ZOOM);
		if (id != null)
			sourceElement.addAttribute(SlideXmlConstants.ID, id);
		Element subElement = sourceElement.addElement(SlideXmlConstants.STEP);
		subElement.addText(String.format(Locale.ROOT, "%g", data.getZoomStep()));
	}
	
}
