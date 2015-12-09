/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.menu;

import org.hypothesis.interfaces.MenuPresenter;

import com.vaadin.ui.CustomComponent;

/**
 * A responsive menu component providing user information and the controls for
 * primary navigation between the views.
 */
@SuppressWarnings({ "serial" })
public final class HypothesisMenu extends CustomComponent {

	private static final String ID = "hypothesis-menu";

	private MenuPresenter presenter;

	public HypothesisMenu(MenuPresenter presenter) {
		this.presenter = presenter;

		// addStyleName("valo-menu");
		addStyleName("valo-menu-color2");
		setId(ID);
		setSizeUndefined();

		setCompositionRoot(presenter.buildContent());
	}

	@Override
	public void attach() {
		super.attach();

		presenter.attach();
	}

	@Override
	public void detach() {
		presenter.detach();

		super.detach();
	}

	/*
	 * private Component buildTitle() { Label logo = new Label(
	 * "QuickTickets <strong>Dashboard</strong>", ContentMode.HTML);
	 * logo.setSizeUndefined(); HorizontalLayout logoWrapper = new
	 * HorizontalLayout(logo); logoWrapper.setComponentAlignment(logo,
	 * Alignment.MIDDLE_CENTER); logoWrapper.addStyleName("valo-menu-title");
	 * return logoWrapper; }
	 */

	/*
	 * private Component buildBadgeWrapper(final Component menuItemButton, final
	 * Component badgeLabel) { CssLayout dashboardWrapper = new
	 * CssLayout(menuItemButton); dashboardWrapper.addStyleName("badgewrapper");
	 * dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
	 * dashboardWrapper.setWidth(100.0f, Unit.PERCENTAGE);
	 * badgeLabel.addStyleName(ValoTheme.MENU_BADGE);
	 * badgeLabel.setWidthUndefined(); badgeLabel.setVisible(false);
	 * dashboardWrapper.addComponent(badgeLabel); return dashboardWrapper; }
	 */

	/*
	 * @Override public void attach() { super.attach();
	 * updateNotificationsCount(null); }
	 */

	/*
	 * @Handler public void postViewChange(final PostViewChangeEvent event) { //
	 * After a successful view change the menu can be hidden in mobile view.
	 * getCompositionRoot().removeStyleName(STYLE_VISIBLE); }
	 */

	/*
	 * @Handler public void updateNotificationsCount( final
	 * NotificationsCountUpdatedEvent event) { int unreadNotificationsCount =
	 * DashboardUI.getDataProvider() .getUnreadNotificationsCount();
	 * notificationsBadge.setValue(String.valueOf(unreadNotificationsCount));
	 * notificationsBadge.setVisible(unreadNotificationsCount > 0); }
	 */

	/*
	 * @Handler public void updateReportsCount(final ReportsCountUpdatedEvent
	 * event) { reportsBadge.setValue(String.valueOf(event.getCount()));
	 * reportsBadge.setVisible(event.getCount() > 0); }
	 */

}
