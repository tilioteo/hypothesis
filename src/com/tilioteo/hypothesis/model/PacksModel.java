/**
 * 
 */
package com.tilioteo.hypothesis.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.engio.mbassy.listener.Handler;

import org.vaadin.jre.ui.DeployJava;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent.StartFeaturedTestEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.StartLegacyTestEvent;
import com.tilioteo.hypothesis.persistence.PermissionService;
import com.tilioteo.hypothesis.persistence.TokenService;
import com.tilioteo.hypothesis.persistence.UserService;
import com.tilioteo.hypothesis.servlet.ServletUtil;
import com.tilioteo.hypothesis.ui.UI;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PacksModel implements Serializable {
	
	private PermissionService permissionService;
	private TokenService tokenService;
	//private PersistenceService persistenceService;
	private UserService userService;
	
	public PacksModel() {
		
		permissionService = PermissionService.newInstance();
		tokenService = TokenService.newInstance();
		//persistenceService = PersistenceService.newInstance();
		userService = UserService.newInstance();
	}

	public List<Pack> getPublicPacks() {
		return permissionService.getPublishedPacks();
	}
	
	public List<Pack> getUserPacks(User user) {
		if (user != null) {
			try {
				user = userService.merge(user);
				//user = persistenceService.merge(user);
				Set<Pack> packs = permissionService.findUserPacks(user, false);
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
		return permissionService.getSimplePublishedPacks();
	}
	
	public List<Pack> getPackByHash(String hash) {
		List<Pack> packs = permissionService.findAllPacks();
		
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

	@Handler
	public void startFeaturedTest(StartFeaturedTestEvent event) {
		Token token = createToken(event.getUser(), event.getPack());
		
		if (token != null) {
			DeployJava.get(UI.getCurrent()).launchJavaWebStart(constructProcessJnlp(token.getUid()));
		}
	}
	
	@Handler
	public void startLegacyTest(StartLegacyTestEvent event) {
		Token token = createToken(event.getUser(), event.getPack());

		if (token != null) {
			event.getUrlConsumer().setUrl(constructProcessUrl(token.getUid(), false));
		}
	}

	private Token createToken(User user, Pack pack) {
		
		return tokenService.createToken(user, pack, true);
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
