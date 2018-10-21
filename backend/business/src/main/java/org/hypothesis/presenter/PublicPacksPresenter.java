/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static org.hypothesis.presenter.BroadcastMessages.REFRESH_PACKS;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.event.data.UIMessage;
import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.servlet.BroadcastService.BroadcastListener;
import org.hypothesis.servlet.ServletUtil;
import org.hypothesis.ui.ControlledUI;
import org.hypothesis.ui.PackPanel;
import org.hypothesis.ui.view.PacksView;
import org.vaadin.button.ui.OpenPopupButton;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedEvent;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedListener;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PublicPacksPresenter extends AbstractViewPresenter implements PacksPresenter, BroadcastListener {

	protected final PermissionService permissionService;
	private final TokenService tokenService;

	private PacksView view;

	private final HashMap<PackPanel, BeanItem<Pack>> panelBeans = new HashMap<>();

	private boolean testStarted = false;

	private final ClickListener legacyButtonClickListener = new ClickListener() {
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
	};

	private final WindowClosedListener legacyButtonWindowClosedListener = new WindowClosedListener() {
		@Override
		public void windowClosed(WindowClosedEvent event) {
			refreshView();
			testStarted = false;
			view.unmask();
		}
	};

	public PublicPacksPresenter() {
		permissionService = PermissionService.newInstance();
		tokenService = TokenService.newInstance();
	}

	protected PacksView getView() {
		return view;
	}

	protected HashMap<PackPanel, BeanItem<Pack>> getPanelBeans() {
		return panelBeans;
	}

	@Override
	public void attach() {
		BroadcastService.register(this);
	}

	@Override
	public void detach() {
		BroadcastService.unregister(this);
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
		return tokenService.createToken(getLoggedUser(), pack, viewUid, true);
	}

	private String constructStartUrl(String uid, boolean returnBack) {
		StringBuilder builder = new StringBuilder();
		String contextUrl = ServletUtil.getHttpContextURL((VaadinServletRequest) VaadinService.getCurrentRequest());
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

	protected PackPanel createPackPanel(Pack pack) {
		BeanItem<Pack> beanItem = new BeanItem<>(pack);
		PackPanel panel = new PackPanel();

		panel.setCaption(pack.getName());
		panel.setIcon(FontAwesome.ARCHIVE);
		panel.setDescriptionPropertyDataSource(beanItem.getItemProperty("description"));
		panel.setLegacyButtonCaption(Messages.getString("Caption.Button.StartLegacy"));
		panel.setLegacyButtonClickListener(legacyButtonClickListener);
		panel.setLegacyButtonWindowClosedListener(legacyButtonWindowClosedListener);

		panelBeans.put(panel, beanItem);

		return panel;
	}

	private PackPanel getParentPanel(Component component) {
		while (component != null && !(component instanceof PackPanel)) {
			component = component.getParent();
		}

		return (PackPanel) component;
	}

	private Pack getPanelBean(PackPanel panel) {
		BeanItem<Pack> beanItem = panelBeans.get(panel);
		if (beanItem != null) {
			return beanItem.getBean();
		}

		return null;
	}

	@Override
	public View createView() {
		view = new PacksView(this);

		view.setEmptyInfoCaption(FontAwesome.FROWN_O.getHtml() + " " + Messages.getString("Message.Info.NoPacks"));

		return view;
	}

	@Override
	public void receiveBroadcast(String message) {
		if (view != null && view.getUI() != null && view.getUI().getSession() != null) { // prevent
																							// from
																							// detached
																							// ui
			// deserialize received message
			final UIMessage uiMessage = UIMessage.fromJson(message);

			if (canHandleMessage(uiMessage)) {
				handleMessage(uiMessage);
			}
		}
	}

	private boolean canHandleMessage(UIMessage message) {
		if (message != null) {
			Long groupId = message.getGroupId();
			Long userId = message.getUserId();

			User loggedUser = getLoggedUser();

			if (null == groupId && null == userId) { // non addressed broadcast
														// message
				return true;

			} else if (loggedUser != null // addressed message, user must be
											// logged
					&& ((groupId != null && groupMatches(groupId, loggedUser.getGroups()))
							|| (userId != null && loggedUser.getId().equals(userId)))) {
				return true;
			}
		}

		return false;
	}

	// TODO: replace by lambda
	private boolean groupMatches(Long groupId, Set<Group> groups) {
		for (Group group : groups) {
			if (group.getId().equals(groupId)) {
				return true;
			}
		}
		return false;
	}

	private void handleMessage(UIMessage message) {
		if (REFRESH_PACKS.equals(message.getType())) {
			pushCommand(new Command() {
				@Override
				public void execute() {
					refreshView();
				}
			});
		}
	}

	private void pushCommand(final Command command) {
		if (command != null && view != null && view.getUI() != null) {
			final UI ui = view.getUI();

			ui.access(new Runnable() {
				@Override
				public void run() {
					Command.Executor.execute(command);

					if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
						try {
							ui.push();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

}
