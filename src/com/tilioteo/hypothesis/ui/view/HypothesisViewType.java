package com.tilioteo.hypothesis.ui.view;

import java.util.HashSet;
import java.util.Set;

import com.tilioteo.hypothesis.entity.Role;
import com.tilioteo.hypothesis.persistence.RoleService;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum HypothesisViewType {
	PACKS("/packs", "Caption.View.Packs", PacksView.class, FontAwesome.BARS, true, new Role[] {RoleService.ROLE_USER, RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	PUBLIC("/public", "Caption.View.Public", PublicPacksView.class, FontAwesome.EYE, true, new Role[] {null, RoleService.ROLE_USER, RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	USERS("/users", "Caption.View.Users", UsersManagementView.class, FontAwesome.USER, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	GROUPS("/groups", "Caption.View.Groups", GroupsManagementView.class, FontAwesome.GROUP, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER}),
	EXPORT("/export", "Caption.View.Export", ExportView.class, FontAwesome.TABLE, true, new Role[] {RoleService.ROLE_MANAGER, RoleService.ROLE_SUPERUSER});

	private final String viewName;
	private final String caption;
	private final Class<? extends View> viewClass;
	private final Resource icon;
	private final boolean stateful;
	private final Set<Role> roles;

	private HypothesisViewType(final String viewName, final String caption,
			final Class<? extends View> viewClass, final Resource icon, final boolean stateful, final Role[] roles) {
		this.viewName = viewName;
		this.caption = caption;
		this.viewClass = viewClass;
		this.icon = icon;
		this.stateful = stateful;
		this.roles = new HashSet<Role>();
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

	public Class<? extends View> getViewClass() {
		return viewClass;
	}

	public Resource getIcon() {
		return icon;
	}

	public boolean isStateful() {
		return stateful;
	}
	
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
