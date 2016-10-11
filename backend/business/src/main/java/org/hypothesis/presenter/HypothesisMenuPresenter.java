/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.hypothesis.annotations.Title;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.PostViewChangeEvent;
import org.hypothesis.event.interfaces.MainUIEvent.ProfileUpdatedEvent;
import org.hypothesis.interfaces.MenuPresenter;
import org.hypothesis.interfaces.UserSettingsWindowPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.menu.ValoMenuItemButton;
import org.hypothesis.utility.BeanUtility;
import org.hypothesis.utility.ViewUtility;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.navigator.View;
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

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalUIScoped
public class HypothesisMenuPresenter implements MenuPresenter {

	private static final String STYLE_VISIBLE = "valo-menu-visible";

	private Component content;
	private MenuItem settingsItem;
	private List<ValoMenuItemButton> itemButtons = new ArrayList<>();

	@Inject
	private Event<MainUIEvent> mainEvent;

	@Inject
	private UserSettingsWindowPresenter userSettingsWindowPresenter;

	@Inject
	private BeanManager beanManager;

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
				mainEvent.fire(new MainUIEvent.UserLoggedOutEvent());
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
		itemButtons.clear();

		CssLayout menuItemsLayout = new CssLayout();
		menuItemsLayout.addStyleName("valo-menuitems");
		menuItemsLayout.setHeight(100.0f, Unit.PERCENTAGE);

		Set<Bean<?>> allViews = beanManager.getBeans(View.class, new AnnotationLiteral<Any>() {
		});

		Consumer<Bean<?>> buildMenuItem = e -> {
			Title title = BeanUtility.getAnnotation(e, Title.class);
			ValoMenuItemButton menuItemButton = new ValoMenuItemButton(Messages.getString(title.value()),
					BeanUtility.getAnnotation(e, CDIView.class).value(), title.icon());

			menuItemsLayout.addComponent(menuItemButton);
			itemButtons.add(menuItemButton);
		};

		// @formatter:off
		allViews.stream()
				.filter(ViewUtility.filterCDIViewsForMainUI.and(ViewUtility.filterByRoles))
				.sorted(ViewUtility.titleIndexComparator)
				.forEach(buildMenuItem);
		// @formatter:on

		return menuItemsLayout;
	}

	public void postViewChange(@Observes final PostViewChangeEvent event) {
		itemButtons.forEach(e -> e.afterViewChange(event.getViewName()));
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
	public void updateUserName(@Observes final ProfileUpdatedEvent event) {
		updateUserName();
	}

}
