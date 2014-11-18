/**
 * 
 */
package com.tilioteo.hypothesis.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.TokenManager;
import com.tilioteo.hypothesis.servlet.ServletUtil;
import com.tilioteo.hypothesis.ui.BrowserAppletFrame;
import com.tilioteo.hypothesis.ui.BrowserAppletFrame.ReadyCheckedEvent;
import com.tilioteo.hypothesis.ui.BrowserAppletFrame.ReadyCheckedListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.JavaScript;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PacksModel implements ReadyCheckedListener {
	
	private PermissionManager permissionManager;
	private TokenManager tokenManager;
	
	private BrowserAppletFrame frame;
	private Token token = null;
	
	public PacksModel() {
		
		permissionManager = PermissionManager.newInstance();
		tokenManager = TokenManager.newInstance();
	}

	public List<Pack> getPublicPacks() {
		return permissionManager.getPublishedPacks();
	}
	
	public List<Pack> getSimplePublicPacks() {
		return permissionManager.getSimplePublishedPacks();
	}
	
	public List<Pack> getPackByHash(String hash) {
		List<Pack> packs = permissionManager.findAllPacks();
		
		List<Pack> onlyPack = new ArrayList<Pack>();
		
		for (Pack pack : packs) {
			String packHash = getPackIdHash(pack.getId());
			
			if (packHash.equals(hash)) {
				onlyPack.add(pack);
				break;
			}
		}
		return onlyPack;		
	}
	
	private String getPackIdHash(Long id) {
		String digest = null;
		String message = "hypothesis" + id;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(message.getBytes("UTF-8"));
			
			StringBuilder sb = new StringBuilder(2*hash.length);
			for (byte b : hash) {
				sb.append(String.format("%02x", b&0xff));
			}
			
			digest = sb.toString();
			
		} catch (Throwable e) {}
		
		return digest;
	}

	public void startTest(Pack pack, BrowserAppletFrame frame, boolean forceLegacy) {
		this.frame = frame;
		token = createToken(pack);
		
		if (token != null) {
			if (!forceLegacy) {
				this.frame.checkReady();
			} else {
				startLegacyWindow();
			}
		}
	}
	
	public void startSimpleTest(Pack pack) {
		token = createToken(pack);
		
		if (token != null) {
			navigateToTest();
		}
	}

	private Token createToken(Pack pack) {
		return tokenManager.createToken(null, pack, true);
	}
	
	private void startLegacyWindow() {
		String contextUrl = ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest());

		//client debug
		//String url = String.format("%s/process/?gwt.codesvr=127.0.0.1:9997&%s=%s%s", contextUrl, "token", token.getUid(), "&fs");
		String url = String.format("%s/process/?%s=%s%s", contextUrl, "token", token.getUid(), "&fs");
		JavaScript javaScript = Page.getCurrent().getJavaScript();
		javaScript.execute("open(\"" + url + "\",\"_blank\",\"width=800,height=600,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no\")");
		token = null;
	}

	private void navigateToTest() {
		String contextUrl = ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest());

		//client debug
		//String url = String.format("%s/process/?gwt.codesvr=127.0.0.1:9997&%s=%s%s", contextUrl, "token", token.getUid(), "&fs&bk=true");
		String url = String.format("%s/process/?%s=%s%s", contextUrl, "token", token.getUid(), "&fs&bk=true");
		Page.getCurrent().setLocation(url);
		token = null;
	}

	@Override
	public void readyChecked(ReadyCheckedEvent event) {
		if (event.isReady()) {
			frame.startBrowser(token.getUid());
		} else {
			startLegacyWindow();
		}
		token = null;
		frame = null;
	}
}
