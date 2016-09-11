/**
 * @(#)JnlpDownloadServlet.java	1.10 07/03/15
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */
package org.hypothesis.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hypothesis.servlet.jnlp.DownloadRequest;
import org.hypothesis.servlet.jnlp.DownloadResponse;
import org.hypothesis.servlet.jnlp.ErrorResponseException;
import org.hypothesis.servlet.jnlp.JarDiffHandler;
import org.hypothesis.servlet.jnlp.JnlpFileHandler;
import org.hypothesis.servlet.jnlp.JnlpResource;
import org.hypothesis.servlet.jnlp.PathRemapper;

/**
 * This Servlet class is an implementation of JNLP Specification's Download
 * Protocols.
 * 
 * All requests to this servlet is in the form of HTTP GET commands. The
 * parameters that are needed are:
 * <ul>
 * <li><code>arch</code>,
 * <li><code>os</code>,
 * <li><code>locale</code>,
 * <li><code>version-id</code> or <code>platform-version-id</code>,
 * <li><code>current-version-id</code>,
 * <li>code>known-platforms</code>
 * </ul>
 * <p>
 * 
 * @version 1.8 01/23/03
 */
@SuppressWarnings("serial")
@WebServlet(value = "/resource/*", asyncSupported = true)
public class JnlpDownloadServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(JnlpDownloadServlet.class);

	// Servlet configuration
	private static final String PARAM_JNLP_EXTENSION = "jnlp-extension";
	private static final String PARAM_JAR_EXTENSION = "jar-extension";

	private JnlpFileHandler jnlpFileHandler = null;
	private JarDiffHandler jarDiffHandler = null;
	// private ResourceCatalog _resourceCatalog = null;

	private PathRemapper pathRemapper = null;

	/**
	 * Given a DownloadPath and a DownloadRequest, it constructs the data stream
	 * to return to the requester
	 */
	private DownloadResponse constructResponse(JnlpResource jnlpres, DownloadRequest dreq) throws IOException {
		// String path = jnlpres.getPath();
		if (jnlpres.isJnlpFile()) {
			// It is a JNLP file. It need to be macro-expanded, so it is handled
			// differently
			boolean supportQuery = JarDiffHandler.isJavawsVersion(dreq, "1.5+");

			// only support query string in href for 1.5 and above
			if (supportQuery) {
				return jnlpFileHandler.getJnlpFileEx(jnlpres, dreq);
			} else {
				return jnlpFileHandler.getJnlpFile(jnlpres, dreq);
			}
		}

		// Check if a JARDiff can be returned
		if (dreq.getCurrentVersionId() != null && jnlpres.isJarFile()) {
			DownloadResponse response = jarDiffHandler.getJarDiffEntry(/* _resourceCatalog, */dreq, jnlpres);
			if (response != null) {
				return response;
			}
		}

		// check and see if we can use pack resource
		JnlpResource jr = new JnlpResource(getServletContext(), jnlpres.getName(), jnlpres.getVersionId(),
				jnlpres.getOSList(), jnlpres.getArchList(), jnlpres.getLocaleList(), jnlpres.getPath(),
				jnlpres.getReturnVersionId(), dreq.getEncoding());

		// Return WAR file resource
		return DownloadResponse.getFileDownloadResponse(jr.getResource(), jr.getMimeType(), jr.getLastModified(),
				jr.getReturnVersionId());
	}

	/*
	 * public static synchronized ResourceBundle getResourceBundle() { if
	 * (_resourceBundle == null) { _resourceBundle =
	 * ResourceBundle.getBundle("jnlp/sample/servlet/resources/strings"); }
	 * return _resourceBundle; }
	 */

	/**
	 * We handle get requests too - eventhough the spec. only requeres POST
	 * requests
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, false);
	}

	@Override
	public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, true);
	}

	private JnlpResource handleBasicDownload(DownloadRequest dreq) throws ErrorResponseException, IOException {
		// Do not return directory names for basic protocol
		if (dreq.getPath() == null || dreq.getPath().endsWith("/")) {
			throw new ErrorResponseException(DownloadResponse.getNoContentResponse());
		}

		// Lookup resource
		JnlpResource jnlpres = new JnlpResource(getServletContext(), dreq.getPath());
		if (!jnlpres.exists()) {
			throw new ErrorResponseException(DownloadResponse.getNoContentResponse());
		}
		return jnlpres;
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response, boolean isHead)
			throws IOException {
		String requestStr = request.getRequestURI();

		log.trace("reguest: " + requestStr);

		if (request.getQueryString() != null)
			requestStr += "?" + request.getQueryString().trim();

		// Parse HTTP request
		DownloadRequest dreq = new DownloadRequest(getServletContext(), pathRemapper, request);

		long ifModifiedSince = request.getDateHeader("If-Modified-Since");

		// Check if it is a valid request
		try {
			// Check if the request is valid
			validateRequest(dreq);

			// Decide what resource to return
			JnlpResource jnlpres = locateResource(dreq);

			DownloadResponse dres;

			if (isHead) {
				int cl = jnlpres.getResource().openConnection().getContentLength();

				// head request response
				dres = DownloadResponse.getHeadRequestResponse(jnlpres.getMimeType(), jnlpres.getVersionId(),
						jnlpres.getLastModified(), cl);

			} else if (ifModifiedSince != -1 && (ifModifiedSince / 1000) >= (jnlpres.getLastModified() / 1000)) {
				// We divide the value returned by getLastModified here by 1000
				// because if protocol is HTTP, last 3 digits will always be
				// zero. However, if protocol is JNDI, that's not the case.
				// so we divide the value by 1000 to remove the last 3 digits
				// before comparison

				// return 304 not modified if possible
				dres = DownloadResponse.getNotModifiedResponse();

			} else {

				// Return selected resource
				dres = constructResponse(jnlpres, dreq);
			}

			dres.sendRespond(response);

		} catch (ErrorResponseException ere) {
			System.err.println("servlet.log.info.badrequest " + requestStr);
			System.err.println("Response: " + ere.toString());
			// Return response from exception
			ere.getDownloadResponse().sendRespond(response);
		} catch (Exception e) {
			System.err.println("servlet.log.fatal.internalerror " + e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/** Initialize servlet */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Get extension from Servlet configuration, or use default
		JnlpResource.setDefaultExtensions(config.getInitParameter(PARAM_JNLP_EXTENSION),
				config.getInitParameter(PARAM_JAR_EXTENSION));

		jnlpFileHandler = new JnlpFileHandler(config.getServletContext());
		jarDiffHandler = new JarDiffHandler(config.getServletContext());

		pathRemapper = new PathRemapper();
		pathRemapper.put("/resource/close", "/WEB-INF/close");
		pathRemapper.put("/resource/browserapplet", "/WEB-INF/browserapplet");
		pathRemapper.put("/resource/browserapplication", "/WEB-INF/browserapplication");
		pathRemapper.put("/resource/swt", "/WEB-INF/swt");
		pathRemapper.put("/resource/browser-applet", "/WEB-INF/lib/browser-applet-0.0.2");
		pathRemapper.put("/resource/browser-application", "/WEB-INF/lib/browser-application-0.0.1");
		pathRemapper.put("/resource/swt-win32-x86_64", "/WEB-INF/lib/swt-signed-4.3-win32-win32-x86_64");
		pathRemapper.put("/resource/swt-win32-x86", "/WEB-INF/lib/swt-signed-4.3-win32-win32-x86");
		pathRemapper.put("/resource/swt-linux-x86_64", "/WEB-INF/lib/swt-signed-4.3-gtk-linux-x86_64");
		pathRemapper.put("/resource/swt-linux-x86", "/WEB-INF/lib/swt-signed-4.3-gtk-linux-x86");
		pathRemapper.put("/resource/swt-linux-ppc64", "/WEB-INF/lib/swt-signed-4.3-gtk-linux-ppc64");
		pathRemapper.put("/resource/swt-linux-ppc", "/WEB-INF/lib/swt-signed-4.3-gtk-linux-ppc");
		pathRemapper.put("/resource/swt-macosx-x86_64", "/WEB-INF/lib/swt-signed-4.3-cocoa-macosx-x86_64");
		pathRemapper.put("/resource/swt-macosx", "/WEB-INF/lib/swt-signed-4.3-cocoa-macosx");
	}

	/**
	 * Interprets the download request and convert it into a resource that is
	 * part of the Web Archive.
	 */
	private JnlpResource locateResource(DownloadRequest dreq) throws IOException, ErrorResponseException {
		/* if (dreq.getVersion() == null) { */
		return handleBasicDownload(dreq);
		/*
		 * } else { return handleVersionRequest(dreq); }
		 */
	}

	/*
	 * private JnlpResource handleVersionRequest(DownloadRequest dreq) throws
	 * IOException, ErrorResponseException { _log.addDebug(
	 * "Version-based/Extension based lookup"); return
	 * _resourceCatalog.lookupResource(dreq); }
	 */

	/**
	 * Make sure that it is a valid request. This is also the place to implement
	 * the reverse IP lookup
	 */
	private void validateRequest(DownloadRequest dreq) throws ErrorResponseException {
		String path = dreq.getPath();
		if (/* path.endsWith(ResourceCatalog.VERSION_XML_FILENAME) || */path.indexOf("__") != -1) {
			throw new ErrorResponseException(DownloadResponse.getNoContentResponse());
		}
	}
}
