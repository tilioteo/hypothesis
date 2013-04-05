/**
 * 
 */
package org.hypothesis.application.collector.core;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.application.collector.xml.SlideXmlUtility;
import org.hypothesis.common.StringMap;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideUtility {

	@SuppressWarnings("unchecked")
	public static StringMap getActionAttributesMap(Element element) {
		StringMap map = new StringMap();

		List<Attribute> attributes = element.attributes();
		for (Attribute attribute : attributes) {
			map.put(attribute.getName(), attribute.getValue());
		}

		return map;
	}

	public static List<Element> getHandlerElements(Element component) {
		return SlideXmlUtility.getComponentHandlers(component);
	}

	public static List<Element> getItemElements(Element component) {
		return SlideXmlUtility.getComponentItems(component);
	}

	public static StringMap getPropertyValueMap(Element component) {
		StringMap map = new StringMap();
		List<Element> elements = SlideXmlUtility
				.getComponentProperties(component);

		for (Element element : elements) {
			String name = element.getName();
			Attribute value = element.attribute(SlideXmlConstants.VALUE);
			if (value != null)
				map.put(name, value.getValue());
		}

		return map;
	}

	/*
	 * public static StringMap getHandlersMap(Element component) { StringMap map
	 * = new StringMap(); List<Element> elements =
	 * SlideXmlUtility.getComponentHandlers(component, null);
	 * 
	 * for (Element element : elements) { String name = element.getName();
	 * Attribute attribute = element.attribute(SlideXmlConstants.ACTION); if
	 * (attribute != null) map.put(name, attribute.getValue()); }
	 * 
	 * return map;
	 * 
	 * }
	 */

	/*
	 * public static Attributes getFeatureAttributes(Node parent) { Attributes
	 * map = new Attributes(); List<Node> attributes =
	 * SlideXmlUtility.getAttributeNodes(parent);
	 * 
	 * for (Node node : attributes) { Attribute name =
	 * Utility.findAttributeByName(node, SlideXmlConstants.NAME); Attribute
	 * value = Utility.findAttributeByName(node, SlideXmlConstants.VALUE); if
	 * (name != null && value != null) map.setValue(name.getValue(),
	 * value.getValue()); }
	 * 
	 * return map; }
	 */

	/*
	 * public static ListStore<ComboData<String>> getChoices(Node parent) {
	 * List<Node> choices = SlideXmlUtility.getChoiceNodes(parent);
	 * ListStore<ComboData<String>> store = new ListStore<ComboData<String>>();
	 * 
	 * for (Node node : choices) { Attribute value =
	 * Utility.findAttributeByName(node, SlideXmlConstants.VALUE); Attribute
	 * text = Utility.findAttributeByName(node, SlideXmlConstants.TEXT); if
	 * (value != null && text != null) store.add(new
	 * ComboData<String>(text.getValue(), value.getValue())); }
	 * 
	 * return store; }
	 */

	/*
	 * @SuppressWarnings("unchecked") public static HashMap<String, Icon>
	 * getIconsMap(Node parent) { List<Node> icons =
	 * SlideXmlUtility.getIconNodes(parent);
	 * 
	 * HashMap iconMap = new HashMap<String, Icon>();
	 * 
	 * for (Node node : icons) { Attribute idAttr =
	 * Utility.findAttributeByName(node, SlideXmlConstants.ID); Attribute
	 * imageAttr = Utility.findAttributeByName(node,
	 * SlideXmlConstants.IMAGE_URL); Attribute sizeAttr =
	 * Utility.findAttributeByName(node, SlideXmlConstants.SIZE); Attribute
	 * offAttr = Utility.findAttributeByName(node, SlideXmlConstants.OFFSET); if
	 * (idAttr != null && imageAttr != null && sizeAttr != null) { String id =
	 * idAttr.getValue(); String img = imageAttr.getValue(); Size size =
	 * OpenLayersUtility.getSizeFromString(sizeAttr.getValue()); if (id.length()
	 * > 0 && img.length() > 0 && size != null) { Pixel offset = (offAttr !=
	 * null ? OpenLayersUtility.getPixelFromString(offAttr.getValue()) : null);
	 * 
	 * Icon icon = (offset != null ? new Icon(img, size, offset) : new Icon(img,
	 * size)); iconMap.put(id, icon); } } } return iconMap; }
	 */

	/*
	 * public static StyleObject makeStyleObject(Node node) { if (node != null)
	 * { Attribute typeAttr = Utility.findAttributeByName(node,
	 * SlideXmlConstants.TYPE); StyleTemplate template = StyleTemplate.Default;
	 * if (typeAttr != null) { String type = typeAttr.getValue(); template =
	 * StyleObject.stringToStyleTemplate(type); } StyleObject style = new
	 * StyleObject(template); HashMap<String, String> properties =
	 * getPropertiesMap(node); String str =
	 * properties.get(SlideXmlConstants.PROP_FillColor); if (str != null &&
	 * str.length() > 0) { style.setFillColor(str); } str =
	 * properties.get(SlideXmlConstants.PROP_FillOpacity); if (str != null &&
	 * str.length() > 0) { double val = Double.parseDouble(str);
	 * style.setFillOpacity(val); } str =
	 * properties.get(SlideXmlConstants.PROP_StrokeColor); if (str != null &&
	 * str.length() > 0) { style.setStrokeColor(str); } str =
	 * properties.get(SlideXmlConstants.PROP_StrokeOpacity); if (str != null &&
	 * str.length() > 0) { double val = Double.parseDouble(str);
	 * style.setStrokeOpacity(val); } str =
	 * properties.get(SlideXmlConstants.PROP_StrokeWidth); if (str != null &&
	 * str.length() > 0) { double val = Double.parseDouble(str);
	 * style.setStrokeWidth(val); } str =
	 * properties.get(SlideXmlConstants.PROP_StrokeDashstyle); if (str != null
	 * && str.length() > 0) { StrokeDashstyle dashstyle =
	 * StyleObject.stringToStrokeDashstyle(str); if (dashstyle != null)
	 * style.setStrokeDashstyle(dashstyle); } str =
	 * properties.get(SlideXmlConstants.PROP_PointRadius); if (str != null &&
	 * str.length() > 0) { double val = Double.parseDouble(str);
	 * style.setPointRadius(val); } return style; } return null; }
	 */

	/*
	 * public static HashMap<String, StyleObject> getStylesMap(Node parent) {
	 * List<Node> styleNodes = SlideXmlUtility.getStyleNodes(parent);
	 * HashMap<String, StyleObject> styles = new HashMap<String, StyleObject>();
	 * 
	 * for (Node node : styleNodes) { Attribute idAttr =
	 * Utility.findAttributeByName(node, SlideXmlConstants.ID); if (idAttr !=
	 * null) { String id = idAttr.getValue(); StyleObject style =
	 * makeStyleObject(node); if (style != null && id.length() > 0)
	 * styles.put(id, style); } } return styles; }
	 */

	/*
	 * private static StyleSet makeDefaultStyleSet() { StyleSet styleSet = new
	 * StyleSet(); styleSet.setStyle(StyleType.Default, new
	 * StyleObject(StyleTemplate.Default)); styleSet.setStyle(StyleType.Select,
	 * new StyleObject(StyleTemplate.Select));
	 * styleSet.setStyle(StyleType.Temporary, new
	 * StyleObject(StyleTemplate.Temporary)); return styleSet; }
	 */

	/*
	 * @SuppressWarnings("unchecked") public static HashMap<String, StyleMap>
	 * getStyleMapsMap(Node parent) { HashMap<String, StyleObject> styles =
	 * getStylesMap(parent); List<Node> styleMaps =
	 * SlideXmlUtility.getStyleMapNodes(parent);
	 * 
	 * HashMap map = new HashMap<String, StyleMap>();
	 * 
	 * for (Node node : styleMaps) { Attribute idAttr =
	 * Utility.findAttributeByName(node, SlideXmlConstants.ID); if (idAttr !=
	 * null) { String id = idAttr.getValue(); if (id.length() > 0) { List<Node>
	 * styleNodes = SlideXmlUtility.getStyleNodes(node); StyleSet styleSet =
	 * makeDefaultStyleSet(); for (Node styleNode : styleNodes) { Attribute
	 * copyIdAttr = Utility.findAttributeByName(styleNode,
	 * SlideXmlConstants.COPY_STYLE_ID); Attribute typeAttr =
	 * Utility.findAttributeByName(styleNode, SlideXmlConstants.TYPE); if
	 * (typeAttr != null) { String type = typeAttr.getValue(); StyleType
	 * styleType = StyleSet.stringToStyleType(type); StyleTemplate styleTemplate
	 * = StyleObject.stringToStyleTemplate(type); String copyId = null; if
	 * (copyIdAttr != null) { copyId = copyIdAttr.getValue(); } if (styleType !=
	 * null && styleTemplate != null) { StyleObject styleObject = null; if
	 * (copyId != null) { styleObject = styles.get(copyId); } else { styleObject
	 * = makeStyleObject(styleNode); } if (styleObject != null)
	 * styleSet.setStyle(styleType, styleObject); } } } map.put(id, new
	 * StyleMap(styleSet)); } } }
	 * 
	 * return map; }
	 */

	/*
	 * public static TimerClock getTimerObject(SlideController slide, String id)
	 * { if (slide != null) { Object object =
	 * slide.getObjects().get(TIMER_PREFIX+id+ID_SUFFIX); if (object != null) {
	 * try { TimerClock timer = (TimerClock)object; return timer; } catch
	 * (Throwable e) {} } } return null; }
	 */

	/*
	 * public static void setTimerObject(SlideController slide, TimerClock
	 * timer, String id) { if (slide != null) {
	 * slide.getObjects().put(TIMER_PREFIX+id+ID_SUFFIX, timer); } }
	 */

	/*
	 * public static void stopSlideTimers(SlideController slide) { if (slide !=
	 * null) { for (Object object : slide.getObjects().values()) { try {
	 * TimerClock timer = (TimerClock)object; timer.stopCount(); }
	 * catch(Throwable e) {} } } }
	 */

	/*
	 * public static Control getMapControlObject(SlideController slide, String
	 * id) { if (slide != null) { Object object =
	 * slide.getObjects().get(MAP_CONTROL_PREFIX+id+ID_SUFFIX); if (object !=
	 * null) { try { Control control = (Control)object; return control; } catch
	 * (Throwable e) {} } } return null; }
	 */

	/*
	 * public static void setMapControlObject(SlideController slide, Control
	 * control, String id) { if (slide != null) {
	 * slide.getObjects().put(MAP_CONTROL_PREFIX+id+ID_SUFFIX, control); } }
	 */

	/*
	 * public static AbstractDialog getDialogObject(SlideController slide,
	 * String id) { if (slide != null) { Object object =
	 * slide.getObjects().get(DIALOG_PREFIX+id+ID_SUFFIX); if (object != null) {
	 * try { AbstractDialog dialog = (AbstractDialog)object; return dialog; }
	 * catch (Throwable e) {} } } return null; }
	 */

	/*
	 * public static List<AbstractDialog> getDialogObjects(SlideController
	 * slide) { List<AbstractDialog> dialogs = new ArrayList<AbstractDialog>();
	 * for (String key : slide.getObjects().keySet()) { if
	 * (key.startsWith(DIALOG_PREFIX)) { Object object =
	 * slide.getObjects().get(key); if (object != null) { try { AbstractDialog
	 * dialog = (AbstractDialog)object; if (dialog != null) dialogs.add(dialog);
	 * } catch (Throwable e) {} } } }
	 * 
	 * return dialogs; }
	 */

	/*
	 * public static void setDialogObject(SlideController slide, AbstractDialog
	 * dialog, String id) { if (slide != null) {
	 * slide.getObjects().put(DIALOG_PREFIX+id+ID_SUFFIX, dialog); } }
	 */

	/*
	 * public static Widget getComponentObject(SlideController slide, String id)
	 * { if (slide != null) { Object object =
	 * slide.getObjects().get(COMPONENT_PREFIX+id+ID_SUFFIX); if (object !=
	 * null) { try { Widget widget = (Widget)object; return widget; } catch
	 * (Throwable e) {} } } return null; }
	 * 
	 * public static void setComponentObject(SlideController slide, Widget
	 * widget, String id) { if (slide != null) {
	 * slide.getObjects().put(COMPONENT_PREFIX+id+ID_SUFFIX, widget); } }
	 */

	/*
	 * public static BButton getButtonObject(SlideController slide, String id) {
	 * if (slide != null) { Object object =
	 * slide.getObjects().get(BUTTON_PREFIX+id+ID_SUFFIX); if (object != null) {
	 * try { BButton button = (BButton)object; return button; } catch (Throwable
	 * e) {} } } return null; }
	 * 
	 * public static String getButtonObjectId(SlideController slide, BButton
	 * button) { if (slide.getObjects().containsValue(button)) {
	 * Set<Entry<String, Object>> entrySet = slide.getObjects().entrySet(); for
	 * (Iterator<Entry<String, Object>> iterator = entrySet.iterator();
	 * iterator.hasNext();) { Entry<String, Object> entry = iterator.next(); if
	 * (entry.getValue() == button) { String key = entry.getKey(); if
	 * (key.indexOf(BUTTON_PREFIX) == 0) { String id =
	 * key.substring(BUTTON_PREFIX.length(), key.length()-1); return id; } } } }
	 * return null; }
	 * 
	 * public static void setButtonObject(SlideController slide, BButton button,
	 * String id) { if (slide != null) {
	 * slide.getObjects().put(BUTTON_PREFIX+id+ID_SUFFIX, button); } }
	 */

	/*
	 * public static ExtendedMapPanel getMapPanelObject(SlideController slide,
	 * String id) { if (slide != null) { Object object =
	 * slide.getObjects().get(MAP_PANEL_PREFIX+id+ID_SUFFIX); if (object !=
	 * null) { try { ExtendedMapPanel map = (ExtendedMapPanel)object; return
	 * map; } catch (Throwable e) {} } } return null; }
	 * 
	 * public static String getMapPanelObjectId(SlideController slide,
	 * ExtendedMapPanel map) { if (slide.getObjects().containsValue(map)) {
	 * Set<Entry<String, Object>> entrySet = slide.getObjects().entrySet(); for
	 * (Iterator<Entry<String, Object>> iterator = entrySet.iterator();
	 * iterator.hasNext();) { Entry<String, Object> entry = iterator.next(); if
	 * (entry.getValue() == map) { String key = entry.getKey(); if
	 * (key.indexOf(MAP_PANEL_PREFIX) == 0) { String id =
	 * key.substring(MAP_PANEL_PREFIX.length(), key.length()-1); return id; } }
	 * } } return null; }
	 * 
	 * public static void setMapPanelObject(SlideController slide,
	 * ExtendedMapPanel map, String id) { if (slide != null) {
	 * slide.getObjects().put(MAP_PANEL_PREFIX+id+ID_SUFFIX, map); } }
	 */

	/*
	 * public static Image getImageObject(SlideController slide, String id) { if
	 * (slide != null) { Object object =
	 * slide.getObjects().get(IMAGE_PREFIX+id+ID_SUFFIX); if (object != null) {
	 * try { Image image = (Image)object; return image; } catch (Throwable e) {}
	 * } } return null; }
	 * 
	 * public static String getImageObjectId(SlideController slide, Image image)
	 * { if (slide.getObjects().containsValue(image)) { Set<Entry<String,
	 * Object>> entrySet = slide.getObjects().entrySet(); for
	 * (Iterator<Entry<String, Object>> iterator = entrySet.iterator();
	 * iterator.hasNext();) { Entry<String, Object> entry = iterator.next(); if
	 * (entry.getValue() == image) { String key = entry.getKey(); if
	 * (key.indexOf(IMAGE_PREFIX) == 0) { String id =
	 * key.substring(IMAGE_PREFIX.length(), key.length()-1); return id; } } } }
	 * return null; }
	 * 
	 * public static void setImageObject(SlideController slide, Image image,
	 * String id) { if (slide != null) {
	 * slide.getObjects().put(IMAGE_PREFIX+id+ID_SUFFIX, image); } }
	 */

}
