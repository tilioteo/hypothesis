package com.tilioteo.hypothesis.ui.view;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.PostViewChangeEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.ProfileUpdatedEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.server.SessionUtils;
import com.tilioteo.hypothesis.ui.UserSettingsWindow;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A responsive menu component providing user information and the controls for
 * primary navigation between the views.
 */
@SuppressWarnings({ "serial" })
public final class HypothesisMenu extends CustomComponent {

	public static final String ID = "hypothesis-menu";
	private static final String STYLE_VISIBLE = "valo-menu-visible";
	private MenuItem settingsItem;

	public HypothesisMenu() {
		//addStyleName("valo-menu");
		addStyleName("valo-menu-color2");
		setId(ID);
		setSizeUndefined();

		// There's only one DashboardMenu per UI so this doesn't need to be
		// unregistered from the UI-scoped DashboardEventBus.
		//HypothesisEventBus.register(this);
		
		MainEventBus.get().register(this);

		setCompositionRoot(buildContent());
	}

	private Component buildContent() {
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

		return menuContent;
	}

	/*private Component buildTitle() {
		Label logo = new Label("QuickTickets <strong>Dashboard</strong>",
				ContentMode.HTML);
		logo.setSizeUndefined();
		HorizontalLayout logoWrapper = new HorizontalLayout(logo);
		logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
		logoWrapper.addStyleName("valo-menu-title");
		return logoWrapper;
	}*/

	private User getCurrentUser() {
		return SessionUtils.getAttribute(User.class);
	}

	private Component buildUserMenu() {
		final MenuBar settings = new MenuBar();
		settings.addStyleName("user-menu");
		final User user = getCurrentUser();
		settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null);

		if (!User.GUEST.equals(user)) {
			settingsItem.addItem(Messages.getString("Caption.Menu.EditProfile"), new Command() {
				@Override
				public void menuSelected(final MenuItem selectedItem) {
					UserSettingsWindow.open(user);
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
				MainEventBus.get().post(new HypothesisEvent.UserLoggedOutEvent());
			}
		});

		updateUserName(new ProfileUpdatedEvent());
		
		return settings;
	}

	private Component buildToggleButton() {
		Button valoMenuToggleButton = new Button("Menu", new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				if (getCompositionRoot().getStyleName().contains(STYLE_VISIBLE)) {
					getCompositionRoot().removeStyleName(STYLE_VISIBLE);
				} else {
					getCompositionRoot().addStyleName(STYLE_VISIBLE);
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
			
			if (user != null) {
				if (view.isAllowed(user.getRoles())) {
					Component menuItemComponent = new ValoMenuItemButton(view);

					menuItemsLayout.addComponent(menuItemComponent);
				}
			}
		}
		return menuItemsLayout;

	}

	/*private Component buildBadgeWrapper(final Component menuItemButton, final Component badgeLabel) {
		CssLayout dashboardWrapper = new CssLayout(menuItemButton);
		dashboardWrapper.addStyleName("badgewrapper");
		dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
		dashboardWrapper.setWidth(100.0f, Unit.PERCENTAGE);
		badgeLabel.addStyleName(ValoTheme.MENU_BADGE);
		badgeLabel.setWidthUndefined();
		badgeLabel.setVisible(false);
		dashboardWrapper.addComponent(badgeLabel);
		return dashboardWrapper;
	}*/

	/*@Override
	public void attach() {
		super.attach();
		updateNotificationsCount(null);
	}*/

	/*@Subscribe
	public void postViewChange(final PostViewChangeEvent event) {
		// After a successful view change the menu can be hidden in mobile view.
		getCompositionRoot().removeStyleName(STYLE_VISIBLE);
	}*/

	/*@Subscribe
	public void updateNotificationsCount(
			final NotificationsCountUpdatedEvent event) {
		int unreadNotificationsCount = DashboardUI.getDataProvider()
				.getUnreadNotificationsCount();
		notificationsBadge.setValue(String.valueOf(unreadNotificationsCount));
		notificationsBadge.setVisible(unreadNotificationsCount > 0);
	}*/

	/*@Subscribe
	public void updateReportsCount(final ReportsCountUpdatedEvent event) {
		reportsBadge.setValue(String.valueOf(event.getCount()));
		reportsBadge.setVisible(event.getCount() > 0);
	}*/

	@Subscribe
	public void updateUserName(final ProfileUpdatedEvent event) {
		User user = getCurrentUser();
		settingsItem.setText(user.getUsername());
	}

	public final class ValoMenuItemButton extends Button {

		private static final String STYLE_SELECTED = "selected";

		private final HypothesisViewType view;

		public ValoMenuItemButton(final HypothesisViewType view) {
			this.view = view;
			setPrimaryStyleName("valo-menu-item");
			setIcon(view.getIcon());
			setCaption(Messages.getString(view.getCaption()));
			MainEventBus.get().register(this);
			addClickListener(new ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					UI.getCurrent().getNavigator().navigateTo(view.getViewName());
				}
			});

		}

		@Subscribe
		public void postViewChange(final PostViewChangeEvent event) {
			removeStyleName(STYLE_SELECTED);
			if (event.getView() == view) {
				addStyleName(STYLE_SELECTED);
			}
		}
	}
}
