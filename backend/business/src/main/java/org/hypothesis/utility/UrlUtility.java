package org.hypothesis.utility;

import org.hypothesis.servlet.ServletUtil;
import org.hypothesis.ui.ControlledUI;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

public class UrlUtility {

	public static String constructStartUrl(String uid/*, boolean returnBack*/) {
		StringBuilder builder = new StringBuilder();
		String contextUrl = ServletUtil.getHttpContextURL((VaadinServletRequest) VaadinService.getCurrentRequest());
		builder.append(contextUrl);
		builder.append("/process/?");

		// client debug
		// builder.append("gwt.codesvr=127.0.0.1:9997&");

		builder.append("token=");
		builder.append(uid);
		builder.append("&fs");
		//if (returnBack) {
		//	builder.append("&bk=true");
		//}

		String lang = ControlledUI.getCurrentLanguage();
		if (lang != null) {
			builder.append("&lang=");
			builder.append(lang);
		}

		return builder.toString();
	}

}
