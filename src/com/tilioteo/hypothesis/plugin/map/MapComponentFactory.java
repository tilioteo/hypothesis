/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureEvent;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;
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

			if (name.equals(SlideXmlConstants.IMAGE_LAYER))
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

			// TODO create other layers

			String id = com.tilioteo.hypothesis.dom.SlideXmlUtility
					.getId(element);
			if (!Strings.isNullOrEmpty(id) && component != null) {
				slideManager.getComponents().put(id, component);
			}

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

}
