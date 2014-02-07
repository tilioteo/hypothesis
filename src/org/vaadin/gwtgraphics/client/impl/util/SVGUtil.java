/*
 * Copyright 2011 Henri Kerola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.gwtgraphics.client.impl.util;

import com.google.gwt.dom.client.Element;

/**
 * This class contains helpers used by the SVGImpl class.
 * 
 * @author Henri Kerola
 * 
 */
public abstract class SVGUtil {

	public static final String SVG_NS = "http://www.w3.org/2000/svg";

	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";

	public static Element createSVGElementNS(String tag) {
		return createElementNS(SVG_NS, tag);
	}

	public static native Element createElementNS(String ns, String tag) /*-{
		return $doc.createElementNS(ns, tag);
	}-*/;

	public static void setAttributeNS(Element elem, String attr, int value) {
		setAttributeNS(null, elem, attr, "" + value);
	}

	public static void setAttributeNS(Element elem, String attr, String value) {
		setAttributeNS(null, elem, attr, value);
	}

	public static native void setAttributeNS(String uri, Element elem,
			String attr, String value) /*-{
		elem.setAttributeNS(uri, attr, value);
	}-*/;

	public static native void setClassName(Element element, String name) /*-{
		// See http://newsgroups.cryer.info/mozilla/dev.tech.svg/200803/080318666.html
		element.className.baseVal = name;
	}-*/;
	
	public static native String getClassName(Element element) /*-{
		return element.className.baseVal;
	}-*/;

	public static void setClassName(Element element, String style, boolean add) {
		style = style.trim();
		if (style.length() != 0) {
			if (add) {
				addClassName(element, style);
			} else {
				removeClassName(element, style);
			}
		}
    }

	/**
	 * Returns the index of the first occurrence of name in a space-separated
	 * list of names, or -1 if not found.
	 * 
	 * @param nameList
	 *            list of space delimited names
	 * @param name
	 *            a non-empty string. Should be already trimmed.
	 */
	private static int indexOfName(String nameList, String name) {
		int idx = nameList.indexOf(name);

		// Calculate matching index.
		while (idx != -1) {
			if (idx == 0 || nameList.charAt(idx - 1) == ' ') {
				int last = idx + name.length();
				int lastPos = nameList.length();
				if ((last == lastPos)
						|| ((last < lastPos) && (nameList.charAt(last) == ' '))) {
					break;
				}
			}
			idx = nameList.indexOf(name, idx + 1);
		}

		return idx;
	}

	private static boolean addClassName(Element element, String className) {
		className = className.trim();
		if (className.length() != 0) {
			// Get the current style string.
			String oldClassName = getClassName(element);
			int idx = indexOfName(oldClassName, className);

			// Only add the style if it's not already present.
			if (idx == -1) {
				if (oldClassName.length() > 0) {
					setClassName(element, oldClassName + " " + className);
				} else {
					setClassName(element, className);
				}
				return true;
			}
		}
	    return false;
	}

	private static boolean removeClassName(Element element, String className) {
		className = className.trim();
		if (className.length() != 0) {
			// Get the current style string.
			String oldStyle = getClassName(element);
			int idx = indexOfName(oldStyle, className);

			// Don't try to remove the style if it's not there.
			if (idx != -1) {
				// Get the leading and trailing parts, without the removed name.
				String begin = oldStyle.substring(0, idx).trim();
				String end = oldStyle.substring(idx + className.length()).trim();

				// Some contortions to make sure we don't leave extra spaces.
				String newClassName;
				if (begin.length() == 0) {
					newClassName = end;
				} else if (end.length() == 0) {
					newClassName = begin;
				} else {
					newClassName = begin + " " + end;
				}

				setClassName(element, newClassName);
				return true;
			}
		}
		return false;
	}

	public static native SVGBBox getBBBox(Element element, boolean attached)
	/*-{
		var bbox = null;
		if (attached) {
			bbox = element.getBBox();
		} else {
			var ns = @org.vaadin.gwtgraphics.client.impl.util.SVGUtil::SVG_NS;
			var svg = $doc.createElementNS(ns, "svg");
			var parent = element.parentNode;
			svg.appendChild(element);
			$doc.documentElement.appendChild(svg);
			bbox = element.getBBox();
			$doc.documentElement.removeChild(svg);
			if (parent != null) {
				parent.appendChild(element);
			}
		}
		return bbox;
	}-*/;

}
