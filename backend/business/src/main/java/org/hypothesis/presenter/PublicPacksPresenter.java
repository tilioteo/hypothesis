/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.ServletUtil;
import org.hypothesis.ui.ControlledUI;
import org.hypothesis.ui.PackPanel;
import org.hypothesis.ui.view.PacksView;
import org.vaadin.button.ui.OpenPopupButton;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedEvent;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedListener;
import org.vaadin.jre.ui.DeployJava;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PublicPacksPresenter implements PacksPresenter {

	protected PermissionService permissionService;
	private TokenService tokenService;

	private PacksView view;

	private HashMap<PackPanel, BeanItem<Pack>> panelBeans = new HashMap<>();

	private User user = null;

	private boolean testStarted = false;
	private Date featuredStart;

	private ClickListener featuredButtonClickListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			if (!testStarted) {
				Pack pack = getPanelBean(getParentPanel(event.getButton()));

				if (pack != null) {
					Date now = new Date();

					// apply 30 seconds delay from last featured start
					Date beforeDate = new Date(now.getTime() - 30000);
					if (featuredStart != null && featuredStart.after(beforeDate)) {
						return;
					}

					featuredStart = null;
					Token token = createToken(pack);

					if (token != null) {
						featuredStart = new Date();
						DeployJava.get(view.getUI()).launchJavaWebStart(constructStartJnlp(token.getUid()));
					}
				}
			}
		}

		private String constructStartJnlp(String uid) {
			StringBuilder builder = new StringBuilder();
			String contextUrl = ServletUtil.getContextURL((VaadinServletRequest) VaadinService.getCurrentRequest());
			builder.append(contextUrl);
			builder.append("/resource/browserapplication.jnlp?");
			builder.append("jnlp.app_url=");
			builder.append(contextUrl);
			builder.append("/process/");
			builder.append("&jnlp.close_key=");
			builder.append("close.html");
			builder.append("&jnlp.token=");
			builder.append(uid);

			return builder.toString();
		}
	};

	private ClickListener legacyButtonClickListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			if (!testStarted) {
				Button button = event.getButton();

				if (button instanceof OpenPopupButton) {
					Pack pack = getPanelBean(getParentPanel(button));

					if (pack != null) {
						Token token = createToken(pack);

						if (token != null) {
							view.mask();
							((OpenPopupButton) button).setUrl(constructStartUrl(token.getUid(), false));
							testStarted = true;
						}
					}
				}
			}
		}

		private String constructStartUrl(String uid, boolean returnBack) {
			StringBuilder builder = new StringBuilder();
			String contextUrl = ServletUtil.getContextURL((VaadinServletRequest) VaadinService.getCurrentRequest());
			builder.append(contextUrl);
			builder.append("/process/?");

			// client debug
			// builder.append("gwt.codesvr=127.0.0.1:9997&");

			builder.append("token=");
			builder.append(uid);
			builder.append("&fs");
			if (returnBack) {
				builder.append("&bk=true");
			}

			String lang = ControlledUI.getCurrentLanguage();
			if (lang != null) {
				builder.append("&lang=");
				builder.append(lang);
			}

			return builder.toString();
		}
	};

	private WindowClosedListener legacyButtonWindowClosedListener = new WindowClosedListener() {
		@Override
		public void windowClosed(WindowClosedEvent event) {
			testStarted = false;
			view.unmask();
		}
	};

	/**
	 * `Constructor
	 */
	public PublicPacksPresenter() {
		permissionService = PermissionService.newInstance();
		tokenService = TokenService.newInstance();
	}

	@Override
	public void attach() {
		// nop
	}

	@Override
	public void detach() {
		// nop
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshView();
	}

	protected void afterCreate() {
		refreshView();
	}

	private Token createToken(Pack pack) {
		String viewUid = SessionManager.getMainUID();
		return tokenService.createToken(user, pack, viewUid, true);
	}

	protected List<Pack> getPacks() {
		return permissionService.getPublishedPacks();
	}

	protected void refreshView() {
		view.clearMainLayout();

		List<Pack> packs = getPacks();
		panelBeans.clear();

		if (packs != null && !packs.isEmpty()) {
			for (Pack pack : packs) {
				view.addPackPanel(createPackPanel(pack));
			}
		} else {
			view.setEmptyInfo();
		}

		view.markAsDirty();
	}

	private PackPanel createPackPanel(Pack pack) {
		BeanItem<Pack> beanItem = new BeanItem<Pack>(pack);
		PackPanel panel = new PackPanel();

		panel.setCaption(pack.getName());
		panel.setIcon(FontAwesome.ARCHIVE);
		panel.setDescriptionPropertyDataSource(beanItem.getItemProperty("description"));
		panel.setStartInfoCaption(Messages.getString("Caption.Pack.ControlTop"));
		panel.setStartInfoSingleCaption(Messages.getString("Caption.Pack.ControlTopSingle"));
		panel.setModeCaption(Messages.getString("Caption.Pack.ControlBottom"));
		panel.setModeSingleCaption(Messages.getString("Caption.Pack.ControlBottomSingle"));
		panel.setNoJavaCaption(Messages.getString("Caption.Pack.NoJava"));
		panel.setFeaturedButtonCaption(Messages.getString("Caption.Button.StartFeatured"));
		panel.setFeaturedButtonClickListener(featuredButtonClickListener);
		panel.setLegacyButtonCaption(Messages.getString("Caption.Button.StartLegacy"));
		panel.setLegacyButtonClickListener(legacyButtonClickListener);
		panel.setLegacyButtonWindowClosedListener(legacyButtonWindowClosedListener);

		panel.setJavaRequired(pack.isJavaRequired());

		panelBeans.put(panel, beanItem);

		return panel;
	}

	private PackPanel getParentPanel(Component component) {
		Component searchComponent = component;
		while (searchComponent != null && !(searchComponent instanceof PackPanel)) {
			searchComponent = searchComponent.getParent();
		}

		return (PackPanel) searchComponent;
	}

	private Pack getPanelBean(PackPanel panel) {
		BeanItem<Pack> beanItem = panelBeans.get(panel);
		if (beanItem != null) {
			return beanItem.getBean();
		}

		return null;
	}

	protected User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@Override
	public View createView() {
		view = new PacksView(this);

		view.setEmptyInfoCaption(FontAwesome.FROWN_O.getHtml() + " " + Messages.getString("Message.Info.NoPacks"));
		view.setCheckingJavaInfo(Messages.getString("Message.Info.CheckingJava"));
		view.setJavaInstalledCaption(Messages.getString("Message.Info.JavaInstalled"));
		view.setJavaNotInstalledCaption(Messages.getString("Message.Info.JavaNotInstalled"));
		view.setJavaInstalLinkCaption(Messages.getString("Message.Info.GetJava"));

		return view;
	}

}
