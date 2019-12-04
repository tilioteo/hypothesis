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
import org.hypothesis.utility.UrlUtility;
import org.hypothesis.utility.ViewUtility;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.server.FontAwesome.ARCHIVE;
import static com.vaadin.server.FontAwesome.FROWN_O;
import static org.hypothesis.presenter.BroadcastMessages.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class PublicPacksPresenter extends AbstractViewPresenter implements PacksPresenter, Broadcaster, Broadcaster.Listener, Pushable {

    protected final PermissionService permissionService;
    private final TokenService tokenService;
    private final HashMap<Long, TestData> packTestData = new HashMap<>();
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

    protected synchronized void cleanOldTestData(List<Pack> packs) {
        final List<Long> ids = packs != null ? packs.stream().map(Pack::getId).collect(Collectors.toList()) : Collections.emptyList();
        final List<Long> idsToRemove = packTestData.keySet().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());
        idsToRemove.forEach(packTestData::remove);
    }

    protected PackPanel createPackPanel(final Pack pack) {
        BeanItem<Pack> beanItem = new BeanItem<>(pack);
        final PackPanel panel = new PackPanel();
        final TestData testData = ensureTestData(pack);

        panel.setCaption(pack.getName());
        panel.setIcon(ARCHIVE);
        panel.setDescriptionPropertyDataSource(beanItem.getItemProperty("description"));
        panel.setLegacyButtonCaption(Messages.getString("Caption.Button.StartLegacy"));

        panel.setLegacyButtonClickListener(e -> {
            if (!isTestRunning()) {
                testData.setRunning(true);
                Token token = createToken(pack);

                if (token != null) {
                    maskView();
                    panel.getLegacyButton().setUrl(UrlUtility.constructStartUrl(token.getUid()));
                }
            }
        });

        panel.setLegacyButtonWindowClosedListener(e -> {
            testData.setRunning(false);
            refreshView();
        });

        return panel;
    }

    private synchronized TestData ensureTestData(Pack pack) {
        TestData testData = packTestData.get(pack.getId());
        if (testData == null) {
            testData = new TestData(pack);
            packTestData.put(pack.getId(), testData);
        }
        return testData;
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
        switch (message.getType()) {
            case PREPARED_TEST:
                onPreparedTest(message.getPackId());
                break;
            case FINISHED_TEST:
            case PROCESS_VIEW_CLOSED:
                onFinnishTestOrProcessViewClosed(message.getPackId());
                break;
            case REFRESH_PACKS:
                onRefreshPacks();
                break;
        }
    }

    private void pushRefreshView() {
        pushCommand(view.getUI(), this::refreshView);
    }

    private void onPreparedTest(Long packId) {
        final TestData testData = packTestData.get(packId);
        if (testData != null) {
            testData.setRunning(true);
        }
        pushRefreshView();
    }

    private void onFinnishTestOrProcessViewClosed(Long packId) {
        final TestData testData = packTestData.get(packId);
        if (testData != null) {
            testData.setRunning(false);
        }
        pushRefreshView();
    }

    private void onRefreshPacks() {
        pushRefreshView();
    }

    @Override
    public synchronized boolean isTestRunning() {
        for (TestData data : packTestData.values()) {
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
