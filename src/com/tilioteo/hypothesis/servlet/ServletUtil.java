/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kamil
 * 
 */
public class ServletUtil {

	public static String getContextURL(HttpServletRequest request) {
		String scheme = request.getScheme(); // http
		String serverName = request.getServerName(); // hostname.com
		int serverPort = request.getServerPort(); // 80
		String contextPath = request.getContextPath(); // /mywebapp
		/*String servletPath = request.getServletPath(); // /servlet/MyServlet
		String pathInfo = request.getPathInfo(); // /a/b;c=123
		String queryString = request.getQueryString(); // d=789*/

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		/*url.append(servletPath); // to get complete url

		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}*/

		return url.toString();
	}

}
