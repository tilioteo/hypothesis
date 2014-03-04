/**
 * 
 */
package com.tilioteo.hypothesis.model;

import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.TokenManager;
import com.tilioteo.hypothesis.ui.BrowserApplet;
import com.vaadin.server.Page;
import com.vaadin.ui.JavaScript;

/**
 * @author kamil
 *
 */
public class PacksModel {
	
	private PermissionManager permissionManager;
	TokenManager tokenManager;
	
	public PacksModel() {
		
		permissionManager = PermissionManager.newInstance();
		tokenManager = TokenManager.newInstance();
	}

	public List<Pack> getPublicPacks() {
		return permissionManager.getPublishedPacks();
	}

	public void startTest(Pack pack, BrowserApplet applet) {
		Token token = createToken(pack);
		if (token != null) {
			if (applet.isReady()) {
				applet.startBrowser(token.getUid());
			} else {
				String url = "/hypothesis/process?token=" + token.getUid() + "&fs";
				JavaScript javaScript = Page.getCurrent().getJavaScript();
				javaScript.execute("open(\"" + url + "\",\"_blank\",\"width=800,height=600,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no\")");
			}
		}
		
	}

	private Token createToken(Pack pack) {
		return tokenManager.createToken(null, pack, true);
	}
	
	

}
