/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.vaadin.maps.shared.ui.Style;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.DrawPointControlData;
import com.tilioteo.hypothesis.plugin.map.event.DrawPointControlEvent;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;
import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeature;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeatureLayer;
import com.tilioteo.hypothesis.plugin.map.ui.ImageLayer;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.ui.ComponentFactory;
import com.tilioteo.hypothesis.ui.SlideComponent;

/**
 * @author kamil
 * 
 */
public class MapComponentFactory {

	public static SlideComponent createComponentFromElement(Element element,
			SlideManager slideManager) {
		if (element != null) {
			String name = element.getName();
			SlideComponent component = null;

			if (name.equals(SlideXmlConstants.MAP))
				component = ComponentFactory.<Map> createFromElement(
						Map.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.IMAGE_LAYER))
				component = ComponentFactory.<ImageLayer> createFromElement(
						ImageLayer.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.FEATURE_LAYER))
				component = ComponentFactory
						.<VectorFeatureLayer> createFromElement(
								VectorFeatureLayer.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.DRAW_POINT))
				component = ComponentFactory
						.<DrawPointControl> createFromElement(
								DrawPointControl.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.DRAW_PATH))
				component = ComponentFactory
						.<DrawPathControl> createFromElement(
								DrawPathControl.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.FEATURE))
				component = ComponentFactory.<VectorFeature> createFromElement(
						VectorFeature.class, element, slideManager);

			// TODO create other components

			String id = com.tilioteo.hypothesis.dom.SlideXmlUtility.getId(element);
			slideManager.registerComponent(id, component);

			return component;
		}
		return null;
	}

	public static Command createImageLayerClickEventCommand(ImageLayerData data) {
		final ImageLayerEvent event = new ImageLayerEvent.Click(data);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createImageLayerLoadEventCommand(
			ImageLayer component, SlideManager slideManager) {
		final ImageLayerEvent event = new ImageLayerEvent.Load(
				new ImageLayerData(component, slideManager));

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createVectorFeatureLayerClickEventCommand(
			VectorFeatureLayerData data) {
		final VectorFeatureLayerEvent event = new VectorFeatureLayerEvent.Click(
				data);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createVectorFeatureClickEventCommand(
			VectorFeatureData data) {
		final VectorFeatureEvent event = new VectorFeatureEvent.Click(data);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawPointControlEventCommand(
			DrawPointControlData data) {
		final DrawPointControlEvent event = new DrawPointControlEvent.DrawPoint(data);

		return CommandFactory.createComponentEventCommand(event);
	}

	public static Command createDrawPathControlEventCommand(
			DrawPathControlData data) {
		final DrawPathControlEvent event = new DrawPathControlEvent.DrawPath(data);

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
					Attribute attr = subElement.attribute(com.tilioteo.hypothesis.dom.SlideXmlConstants.VALUE);
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
									field.set(style, Integer.parseInt(value));
								}
							} else if (typeName.contains("double")) {
								if (value.isEmpty()) {
									field.set(style, 0.0);
								} else {
									field.set(style, Double.parseDouble(value));
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
