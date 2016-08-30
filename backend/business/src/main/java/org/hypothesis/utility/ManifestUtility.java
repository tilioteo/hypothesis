/**
 * 
 */
package org.hypothesis.utility;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.hypothesis.servlet.ServletUtil;

import com.vaadin.server.VaadinServlet;

/**
 * @author john
 *
 */
public class ManifestUtility {

	public static String VERSION = "Version";
	public static String VERSION_SPECIFIC = "Version-Specific";
	public static String VERSION_ADDITIONAL = "Version-Additional";
	public static String SPECIFICATION_VERSION = "Specification-Version";

	public static Attributes getManifestAttributes() {
		Manifest manifest = ServletUtil.getManifest(VaadinServlet.getCurrent().getServletContext());
		return manifest.getMainAttributes();
	}
}
