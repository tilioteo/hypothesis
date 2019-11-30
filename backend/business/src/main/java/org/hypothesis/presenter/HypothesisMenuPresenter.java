/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import net.engio.mbassy.listener.Handler;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.ProfileUpdatedEvent;
import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.interfaces.MenuPresenter;
import org.hypothesis.navigator.HypothesisViewType;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.menu.ValoMenuItemButton;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class HypothesisMenuPresenter implements MenuPresenter, HasMainEventBus {

    private static final String STYLE_VISIBLE = "valo-menu-visible";
    private final UserSettingsWindowPresenter userSettingsWindowPresenter;
    private Component content;
    private MenuItem settingsItem;

    public HypothesisMenuPresenter() {
        userSettingsWindowPresenter = new UserSettingsWindowPresenter();
    }

    @Override
    public void attach() {
        getBus().register(this);
    }

    @Override
    public void detach() {
        getBus().unregister(this);
    }

    @Override
    public Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());

        content = menuContent;
        return menuContent;
    }

    private User getCurrentUser() {
        return SessionManager.getLoggedUser();
    }

    private Component buildUserMenu() {
        final User user = getCurrentUser();

        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null);

        if (!User.GUEST.equals(user)
                // NOTE: VN specific - disable user profile editing
                && (user.getRoles().contains(RoleService.ROLE_MANAGER)
                || user.getRoles().contains(RoleService.ROLE_SUPERUSER))) {
            settingsItem.addItem(Messages.getString("Caption.Menu.EditProfile"),
                    selectedItem -> userSettingsWindowPresenter.showWindow(user));

            settingsItem.addSeparator();
        }

        String itemCaption = Messages.getString("Caption.Menu.Logout");
        if (User.GUEST.equals(user)) {
            itemCaption = Messages.getString("Caption.Menu.LoginOther");
            user.setUsername(Messages.getString("Caption.User.Guest"));
        }

        settingsItem.addItem(itemCaption, selectedItem -> getBus().post(new MainUIEvent.UserLoggedOutEvent()));

        updateUserName();

        return settings;
    }

    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button("Menu", e -> {
            if (content.getStyleName().contains(STYLE_VISIBLE)) {
                content.removeStyleName(STYLE_VISIBLE);
            } else {
                content.addStyleName(STYLE_VISIBLE);
            }
        });

        valoMenuToggleButton.setIcon(FontAwesome.LIST);
        valoMenuToggleButton.addStyleName("valo-menu-toggle");
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);

        return valoMenuToggleButton;
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        menuItemsLayout.setHeight(100.0f, Unit.PERCENTAGE);

        for (final HypothesisViewType view : HypothesisViewType.values()) {
            User user = getCurrentUser();

            if (user != null) {
                if (view.isAllowed(user.getRoles())) {
                    Component menuItemComponent = new ValoMenuItemButton(Messages.getString(view.getCaption()),
                            view.getViewName(), view.getIcon());

                    menuItemComponent.addAttachListener(e -> getBus().register(e.getSource()));
                    menuItemComponent.addDetachListener(e -> getBus().unregister(e.getSource()));

                    menuItemsLayout.addComponent(menuItemComponent);
                }
            }
        }
        return menuItemsLayout;

    }

    private void updateUserName() {
        User user = getCurrentUser();
        settingsItem.setText(user.getUsername());
    }

    @Handler
    public void updateUserName(final ProfileUpdatedEvent event) {
        updateUserName();
    }

}
