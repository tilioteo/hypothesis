/**
 * 
 */
package com.tilioteo.hypothesis.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.PersistenceManager;
import com.tilioteo.hypothesis.persistence.TokenManager;
import com.tilioteo.hypothesis.servlet.ServletUtil;
import com.tilioteo.hypothesis.ui.DeployJava;
import com.tilioteo.hypothesis.ui.UI;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

/**
 * @author kamil
 *
 */
public class PacksModel {
	
	private PermissionManager permissionManager;
	private TokenManager tokenManager;
	
	private PersistenceManager persistenceManager;
	
	private Token token = null;
	
	public PacksModel() {
		
		permissionManager = PermissionManager.newInstance();
		tokenManager = TokenManager.newInstance();
		persistenceManager = PersistenceManager.newInstance();
	}

	public List<Pack> getPublicPacks() {
		return permissionManager.getPublishedPacks();
	}
	
	public List<Pack> getUserPacks(User user) {
		if (user != null) {
			try {
				user = persistenceManager.merge(user);
				Set<Pack> packs = permissionManager.findUserPacks(user, true);
				if (packs != null) {
					LinkedList<Pack> list = new LinkedList<>();
					for (Pack pack : packs) {
						list.add(pack);
					}
					return list;
				}
			} catch (Throwable e) {}
		}
		
		return null;
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

	public void startFeaturedTest(Pack pack) {
		token = createToken(pack);
		
		if (token != null) {
			DeployJava.get(UI.getCurrent()).launchJavaWebStart(constructProcessJnlp(token.getUid()));
		}
	}
	
	public void startLegacyTest(CanSetUrl canSetUrl, Pack pack) {
		token = createToken(pack);

		if (token != null) {
			canSetUrl.setUrl(constructProcessUrl(token.getUid(), false));
		}
	}

	private Token createToken(Pack pack) {
		return tokenManager.createToken(null, pack, true);
	}
	
	private String constructProcessJnlp(String token) {
		StringBuilder builder = new StringBuilder();
		String contextUrl = ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest());
		builder.append(contextUrl);
		builder.append("/resource/browserapplication.jnlp?");
		builder.append("jnlp.app_url=");
		builder.append(contextUrl);
		builder.append("/process/");
		builder.append("&jnlp.close_key=");
		builder.append("close.html");
		builder.append("&jnlp.token=");
		builder.append(token);

		return builder.toString();
}

	private String constructProcessUrl(String token, boolean returnBack) {
		StringBuilder builder = new StringBuilder();
		String contextUrl = ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest());
		builder.append(contextUrl);
		builder.append("/process/?");
		
		// client debug
		//builder.append("gwt.codesvr=127.0.0.1:9997&");
		
		builder.append("token=");
		builder.append(token);
		builder.append("&fs");
		if (returnBack) {
			builder.append("&bk=true");
		}
		String lang = UI.getCurrentLanguage();
		if (lang != null) {
			builder.append("&lang=");
			builder.append(lang);
		}
		
		return builder.toString();
	}

}
