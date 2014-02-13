/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringSet;

/**
 * @author kamil
 *
 */
public class SlideXmlUtility {

	//private static Logger log = Logger.getLogger(SlideXmlUtility.class);

	public static List<Element> getLayers(Element element, StringSet valids) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(element, SlideXmlConstants.LAYERS,
				valids);
	}

	public static List<Element> getControls(Element element, StringSet valids) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(element, SlideXmlConstants.CONTROLS,
				valids);
	}

	public static List<Element> getFeatures(Element element, StringSet valids) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(element, SlideXmlConstants.FEATURES,
				valids);
	}
	
	public static Element getGeometryElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.GEOMETRY);
		}

		return null;
	}



}
