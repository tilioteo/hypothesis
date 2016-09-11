/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.ProfileUpdatedEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.MenuPresenter;
import org.hypothesis.navigator.HypothesisViewType;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.menu.ValoMenuItemButton;

import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class HypothesisMenuPresenter implements MenuPresenter {

	private static final String STYLE_VISIBLE = "valo-menu-visible";

	private Component content;
	private MenuItem settingsItem;

	private final MainEventBus bus;
	private final UserSettingsWindowPresenter userSettingsWindowPresenter;

	/**
	 * Construct with bus
	 * 
	 * @param bus
	 */
	public HypothesisMenuPresenter(MainEventBus bus) {
		this.bus = bus;

		userSettingsWindowPresenter = new UserSettingsWindowPresenter(bus);
	}

	@Override
	public void attach() {
		bus.register(this);
	}

	@Override
	public void detach() {
		bus.unregister(this);
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

		// menuContent.addComponent(buildTitle());
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

		if (!User.GUEST.equals(user)) {
			settingsItem.addItem(Messages.getString("Caption.Menu.EditProfile"), new Command() {
				@Override
				public void menuSelected(final MenuItem selectedItem) {
					userSettingsWindowPresenter.showWindow(user);
				}
			});

			settingsItem.addSeparator();
		}

		String itemCaption = Messages.getString("Caption.Menu.Logout");
		if (User.GUEST.equals(user)) {
			itemCaption = Messages.getString("Caption.Menu.LoginOther");
			user.setUsername(Messages.getString("Caption.User.Guest"));
		}

		settingsItem.addItem(itemCaption, new Command() {
			@Override
			public void menuSelected(final MenuItem selectedItem) {
				bus.post(new MainUIEvent.UserLoggedOutEvent());
			}
		});

		updateUserName();

		return settings;
	}

	private Component buildToggleButton() {
		Button valoMenuToggleButton = new Button("Menu", new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				if (content.getStyleName().contains(STYLE_VISIBLE)) {
					content.removeStyleName(STYLE_VISIBLE);
				} else {
					content.addStyleName(STYLE_VISIBLE);
				}
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

			if (user != null && view.isAllowed(user.getRoles())) {
				Component menuItemComponent = new ValoMenuItemButton(Messages.getString(view.getCaption()),
						view.getViewName(), view.getIcon());

				menuItemComponent.addAttachListener(new AttachListener() {
					@Override
					public void attach(AttachEvent event) {
						bus.register(event.getSource());
					}
				});
				menuItemComponent.addDetachListener(new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						bus.unregister(event.getSource());
					}
				});

				menuItemsLayout.addComponent(menuItemComponent);
			}
		}

		return menuItemsLayout;
	}

	private void updateUserName() {
		User user = getCurrentUser();
		settingsItem.setText(user.getUsername());
	}

	/**
	 * Update user name on change
	 * 
	 * @param event
	 */
	@Handler
	public void updateUserName(final ProfileUpdatedEvent event) {
		updateUserName();
	}

}
