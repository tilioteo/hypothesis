/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.navigator;

import java.util.HashSet;
import java.util.Set;

import org.hypothesis.data.model.Role;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.interfaces.ViewPresenter;
import org.hypothesis.presenter.ExportPresenterImpl;
import org.hypothesis.presenter.GroupManagementPresenter;
import org.hypothesis.presenter.PublicPacksPresenter;
import org.hypothesis.presenter.SlideManagementPresenterImpl;
import org.hypothesis.presenter.UserManagementPresenter;
import org.hypothesis.presenter.UserPacksPresenter;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public enum HypothesisViewType {
	PACKS("/packs", "Caption.View.Packs", UserPacksPresenter.class, FontAwesome.BARS, true, new Role[] {RoleService.ROLE_USER, RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	PUBLIC("/public", "Caption.View.Public", PublicPacksPresenter.class, FontAwesome.EYE, true, new Role[] {null, RoleService.ROLE_USER, RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	USERS("/users", "Caption.View.Users", UserManagementPresenter.class, FontAwesome.USER, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	GROUPS("/groups", "Caption.View.Groups", GroupManagementPresenter.class, FontAwesome.GROUP, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	EXPORT("/export", "Caption.View.Export", ExportPresenterImpl.class, FontAwesome.TABLE, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	SLIDES("/slides", "Caption.View.Slides", SlideManagementPresenterImpl.class, FontAwesome.FILE_CODE_O, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER});

	private final String viewName;
	private final String caption;
	private final Class<? extends ViewPresenter> presenterClass;
	private final Resource icon;
	private final boolean stateful;
	private final Set<Role> roles;

	HypothesisViewType(final String viewName, final String caption, final Class<? extends ViewPresenter> presenterClass, final Resource icon, final boolean stateful, final Role[] roles) {
		this.viewName = viewName;
		this.caption = caption;
		this.presenterClass = presenterClass;
		this.icon = icon;
		this.stateful = stateful;
		this.roles = new HashSet<>();

		initRoles(roles);
	}

	private void initRoles(Role[] roles) {
		for (Role role : roles) {
			this.roles.add(role);
		}
	}

	public String getViewName() {
		return viewName;
	}

	public String getCaption() {
		return caption;
	}

	public Class<? extends ViewPresenter> getViewPresenterClass() {
		return presenterClass;
	}

	public Resource getIcon() {
		return icon;
	}

	public boolean isStateful() {
		return stateful;
	}

	/**
	 * Check if role can access this view
	 * 
	 * @param checkRoles
	 * @return
	 */
	public boolean isAllowed(Set<Role> checkRoles) {
		if (null == checkRoles || checkRoles.isEmpty()) {
			return roles.contains(null);
		} else {
			for (Role checkRole : checkRoles) {
				if (roles.contains(checkRole)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * look for view type by view name
	 * 
	 * @param viewName
	 * @return requested view or null if not found
	 */
	public static HypothesisViewType getByViewName(final String viewName) {
		HypothesisViewType result = null;
		for (HypothesisViewType viewType : values()) {
			if (viewType.getViewName().equals(viewName)) {
				result = viewType;
				break;
			}
		}
		return result;
	}

}
