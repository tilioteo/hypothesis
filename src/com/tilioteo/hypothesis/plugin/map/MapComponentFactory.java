/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.vaadin.maps.shared.ui.Style;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.event.DrawLineControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawLineControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.DrawPointControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPointControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.DrawPolygonControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPolygonControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.ImageSequenceLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ImageSequenceLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.PanControlData;
import com.tilioteo.hypothesis.plugin.map.event.PanControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.WMSLayerData;
import com.tilioteo.hypothesis.plugin.map.event.WMSLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.ZoomControlData;
import com.tilioteo.hypothesis.plugin.map.event.ZoomControlEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawLineControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPolygonControl;
import com.tilioteo.hypothesis.plugin.map.ui.ImageLayer;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;
import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.tilioteo.hypothesis.plugin.map.ui.PanControl;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeature;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeatureLayer;
import com.tilioteo.hypothesis.plugin.map.ui.WMSLayer;
import com.tilioteo.hypothesis.plugin.map.ui.ZoomControl;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.slide.ui.ComponentFactory;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public class MapComponentFactory implements Serializable {

	public static SlideComponent createComponentFromElement(Element element, SlideFascia slideFascia) {
		if (element != null) {
			String name = element.getName();
			SlideComponent component = null;

			if (name.equals(SlideXmlConstants.MAP))
				component = ComponentFactory.<Map> createFromElement(
						Map.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.IMAGE_LAYER))
				component = ComponentFactory.<ImageLayer> createFromElement(
						ImageLayer.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.WMS_LAYER))
				component = ComponentFactory.<WMSLayer> createFromElement(
						WMSLayer.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.IMAGE_SEQUENCE_LAYER))
				component = ComponentFactory.<ImageSequenceLayer> createFromElement(
						ImageSequenceLayer.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.FEATURE_LAYER))
				component = ComponentFactory.<VectorFeatureLayer> createFromElement(
								VectorFeatureLayer.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.DRAW_POINT))
				component = ComponentFactory.<DrawPointControl> createFromElement(
								DrawPointControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.DRAW_PATH))
				component = ComponentFactory.<DrawPathControl> createFromElement(
								DrawPathControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.DRAW_LINE))
				component = ComponentFactory.<DrawLineControl> createFromElement(
						DrawLineControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.DRAW_POLYGON))
				component = ComponentFactory.<DrawPolygonControl> createFromElement(
								DrawPolygonControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.PAN))
				component = ComponentFactory.<PanControl> createFromElement(
						PanControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.ZOOM))
				component = ComponentFactory.<ZoomControl> createFromElement(
						ZoomControl.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.FEATURE))
				component = ComponentFactory.<VectorFeature> createFromElement(
						VectorFeature.class, element, slideFascia);

			// TODO create other components

			String id = SlideXmlUtility.getId(element);
			slideFascia.registerComponent(id, component);

			return component;
		}
		return null;
	}

	public static Command createWMSLayerClickEventCommand(WMSLayerData data, Date timestamp, Date clientTimestamp) {
		WMSLayerEvent event = new WMSLayerEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageLayerClickEventCommand(ImageLayerData data, Date timestamp, Date clientTimestamp) {
		ImageLayerEvent event = new ImageLayerEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageLayerLoadEventCommand(ImageLayerData data, Date timestamp, Date clientTimestamp) {
		ImageLayerEvent event = new ImageLayerEvent.Load(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageSequenceLayerClickEventCommand(ImageSequenceLayerData data, Date timestamp, Date clientTimestamp) {
		ImageSequenceLayerEvent event = new ImageSequenceLayerEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageSequenceLayerLoadEventCommand(ImageSequenceLayerData data, Date timestamp, Date clientTimestamp) {
		ImageSequenceLayerEvent event = new ImageSequenceLayerEvent.Load(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageSequenceLayerChangeEventCommand(ImageSequenceLayerData data, Date timestamp, Date clientTimestamp) {
		ImageSequenceLayerEvent event = new ImageSequenceLayerEvent.Change(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createVectorFeatureLayerClickEventCommand(VectorFeatureLayerData data, Date timestamp, Date clientTimestamp) {
		VectorFeatureLayerEvent event = new VectorFeatureLayerEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createVectorFeatureClickEventCommand(VectorFeatureData data, Date timestamp, Date clientTimestamp) {
		VectorFeatureEvent event = new VectorFeatureEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawPointControlEventCommand(DrawPointControlData data, Date timestamp, Date clientTimestamp) {
		DrawPointControlEvent event = new DrawPointControlEvent.DrawPoint(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawPathControlEventCommand(DrawPathControlData data, Date timestamp, Date clientTimestamp) {
		DrawPathControlEvent event = new DrawPathControlEvent.DrawPath(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawLineControlEventCommand(DrawLineControlData data, Date timestamp, Date clientTimestamp) {
		DrawLineControlEvent event = new DrawLineControlEvent.DrawLine(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawPolygonControlEventCommand(DrawPolygonControlData data, Date timestamp, Date clientTimestamp) {
		DrawPolygonControlEvent event = new DrawPolygonControlEvent.DrawPolygon(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createPanControlPanStartEventCommand(PanControlData data, Date timestamp, Date clientTimestamp) {
		PanControlEvent event = new PanControlEvent.PanStart(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createPanControlPanEndEventCommand(PanControlData data, Date timestamp, Date clientTimestamp) {
		PanControlEvent event = new PanControlEvent.PanEnd(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createZoomControlZoomChangeEventCommand(ZoomControlData data, Date timestamp, Date clientTimestamp) {
		ZoomControlEvent event = new ZoomControlEvent.ZoomChange(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return CommandFactory.createComponentEventCommand(event);
	}

	@SuppressWarnings("unchecked")
	public static Style createStyleFromElement(Element element) {
		if (element != null) {
			Style style = new Style();
			List<Element> elements = element.elements();
			
			for (Element subElement : elements) {
				String name = subElement.getName();
				if (SlideXmlConstants.STYLE_ATTRIBUTES.contains(name)) {
					Attribute attr = subElement.attribute(SlideXmlConstants.VALUE);
					String value = attr.getValue();
					
					if (value != null) {
						String propertyName = Introspector.decapitalize(name);
						
						try {
							Field field = style.getClass().getDeclaredField(propertyName);
							String typeName = field.getType().getName().toLowerCase();
							if (typeName.contains("string")) {
								field.set(style, value);
							} else if (typeName.contains("int")) {
								if (value.isEmpty()) {
									field.set(style, 0);
								} else {
									field.set(style, Strings.toInteger(value));
								}
							} else if (typeName.contains("double")) {
								if (value.isEmpty()) {
									field.set(style, 0.0);
								} else {
									field.set(style, Strings.toDouble(value));
								}
							}
						} catch (Exception e) {
						}
					}
				}
			}
			
			return style;
		}
		return null;
	}

}
