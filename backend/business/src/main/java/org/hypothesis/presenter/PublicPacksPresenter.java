/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static com.vaadin.server.FontAwesome.ARCHIVE;
import static com.vaadin.server.FontAwesome.FROWN_O;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_PACKS;
import static org.hypothesis.utility.PushUtility.pushCommand;

import java.util.HashMap;
import java.util.List;

import org.hypothesis.business.SessionManager;
import org.hypothesis.business.data.TestData;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.event.data.UIMessage;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.servlet.BroadcastService.BroadcastListener;
import org.hypothesis.ui.PackPanel;
import org.hypothesis.ui.view.PacksView;
import org.hypothesis.utility.UIMessageUtility;
import org.hypothesis.utility.ViewUtility;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

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

	private final HashMap<PackPanel, TestData> panelTestData = new HashMap<>();

	public PublicPacksPresenter() {
		permissionService = PermissionService.newInstance();
		tokenService = TokenService.newInstance();
	}

	protected PacksView getView() {
		return view;
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

	public Token createToken(Pack pack) {
		String viewUid = SessionManager.getMainUID();
		return tokenService.createToken(getLoggedUser(), pack, viewUid, true);
	}

	protected List<Pack> getPacks() {
		return permissionService.getPublishedPacks();
	}

	public void refreshView() {
		view.clearMainLayout();

		List<Pack> packs = getPacks();

		if (packs != null && !packs.isEmpty()) {
			for (Pack pack : packs) {
				view.addPackPanel(createPackPanel(pack));
			}
		} else {
			view.setEmptyInfo();
		}

		cleanOldTestData(packs);

		view.markAsDirty();
	}

	protected void cleanOldTestData(List<Pack> packs) {
		for (TestData testData : panelTestData.values()) {
			if (!packs.contains(testData.getPack())) {
				panelTestData.remove(testData);
			}
		}
	}

	protected PackPanel createPackPanel(Pack pack) {
		BeanItem<Pack> beanItem = new BeanItem<>(pack);
		PackPanel panel = new PackPanel();
		TestData testData = panelTestData.get(panel);
		if (testData == null) {
			testData = new TestData(pack);
			panelTestData.put(panel, testData);
		}

		panel.setCaption(pack.getName());
		panel.setIcon(ARCHIVE);
		panel.setDescriptionPropertyDataSource(beanItem.getItemProperty("description"));
		panel.setLegacyButtonCaption(Messages.getString("Caption.Button.StartLegacy"));
		panel.setLegacyButtonWindowClosedListener(new LegacyButtonWindowClosedListener(this, testData));

		LegacyButtonClickListener clickListener = new LegacyButtonClickListener(panel.getLegacyButton(), this,
				testData);
		panel.setLegacyButtonClickListener(clickListener);

		return panel;
	}

	@Override
	public View createView() {
		view = new PacksView(this);

		view.setEmptyInfoCaption(FROWN_O.getHtml() + " " + Messages.getString("Message.Info.NoPacks"));

		return view;
	}

	@Override
	public void receiveBroadcast(String message) {
		if (ViewUtility.isActiveView(view)) { // prevent from detached ui
			// deserialize received message
			final UIMessage uiMessage = UIMessage.fromJson(message);

			if (UIMessageUtility.canHandle(uiMessage, getLoggedUser())) {
				handleMessage(uiMessage);
			}
		}
	}

	private void handleMessage(UIMessage message) {
		if (REFRESH_PACKS.equals(message.getType())) {
			pushCommand(view.getUI(), () -> refreshView());
		}
	}

	@Override
	public boolean isTestRunning() {
		for (TestData data : panelTestData.values()) {
			if (data.isRunning()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void maskView() {
		view.mask();
	}

	@Override
	public void unmaskView() {
		view.unmask();
	}

}
