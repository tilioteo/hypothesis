/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ServletUtil {

	public static String getContextURL(HttpServletRequest request) {
		String scheme = request.getScheme(); // http
		String serverName = request.getServerName(); // hostname.com
		int serverPort = request.getServerPort(); // 80
		String contextPath = request.getContextPath(); // /mywebapp
		/*
		 * String servletPath = request.getServletPath(); // /servlet/MyServlet
		 * String pathInfo = request.getPathInfo(); // /a/b;c=123 String
		 * queryString = request.getQueryString(); // d=789
		 */

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		/*
		 * url.append(servletPath); // to get complete url
		 * 
		 * if (pathInfo != null) { url.append(pathInfo); } if (queryString !=
		 * null) { url.append("?").append(queryString); }
		 */

		return url.toString();
	}

	public static Manifest getManifest(ServletContext context) {
		InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest = null;
		;
		try {
			manifest = new Manifest(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return manifest;
	}

}
