/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import org.hypothesis.business.SessionManager;
import org.hypothesis.business.data.TestData;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.TokenService;
import org.hypothesis.event.data.UIMessage;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.push.Pushable;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.ui.PackPanel;
import org.hypothesis.ui.view.PacksView;
import org.hypothesis.utility.UIMessageUtility;
import org.hypothesis.utility.ViewUtility;

import java.util.HashMap;
import java.util.List;

import static com.vaadin.server.FontAwesome.ARCHIVE;
import static com.vaadin.server.FontAwesome.FROWN_O;
import static org.hypothesis.presenter.BroadcastMessages.PROCESS_VIEW_CLOSED;
import static org.hypothesis.presenter.BroadcastMessages.REFRESH_PACKS;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class PublicPacksPresenter extends AbstractViewPresenter implements PacksPresenter, Broadcaster, Broadcaster.Listener, Pushable {

    protected final PermissionService permissionService;
    private final TokenService tokenService;
    private final HashMap<PackPanel, TestData> panelTestData = new HashMap<>();
    private PacksView view;

    public PublicPacksPresenter() {
        permissionService = PermissionService.newInstance();
        tokenService = TokenService.newInstance();
    }

    protected PacksView getView() {
        return view;
    }

    @Override
    public void attach() {
        listenBroadcasting();
    }

    @Override
    public void detach() {
        unlistenBroadcasting();
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
            view.removeEmptyInfo();
            for (Pack pack : packs) {
                view.addPackPanel(createPackPanel(pack));
            }
        } else {
            view.setEmptyInfo();
        }

        cleanOldTestData(packs);

        if (isTestRunning()) {
            maskView();
        } else {
            unmaskView();
        }

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

            if (UIMessageUtility.canHandle(uiMessage, getLoggedUser())
                    || UIMessageUtility.canHandle(uiMessage, SessionManager.getMainUID())) {
                handleMessage(uiMessage);
            }
        }
    }

    private void handleMessage(UIMessage message) {
        if (REFRESH_PACKS.equals(message.getType())
                || PROCESS_VIEW_CLOSED.equals(message.getType())) {
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
