/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.io.Serializable;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.common.collections.StringSet;
/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideXmlUtility implements Serializable {

	//private static Logger log = Logger.getLogger(SlideXmlUtility.class);

	public static String getId(Element element) {
		return element.attributeValue(SlideXmlConstants.ID);
	}

	public static String getValue(Element element) {
		return element.attributeValue(SlideXmlConstants.VALUE);
	}

	public static List<Element> getStyles(Element element) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(element, SlideXmlConstants.STYLES,
				new StringSet(SlideXmlConstants.STYLE));
	}

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

	public static Element getTextElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.TEXT);
		}

		return null;
	}

	public static Element getOffsetElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.OFFSET);
		}

		return null;
	}

	public static List<Element> getImages(Element element) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(element, SlideXmlConstants.IMAGES,
				new StringSet(SlideXmlConstants.IMAGE));
	}

}
