/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.navigator;

import static org.hypothesis.data.api.Roles.ROLE_MANAGER;
import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.data.api.Roles.ROLE_USER;

import java.util.HashSet;
import java.util.Set;

import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.interfaces.ViewPresenter;
import org.hypothesis.presenter.ControlPanelVNPresenter;
import org.hypothesis.presenter.ExportPresenterImpl;
import org.hypothesis.presenter.ExportScoreVNPresenterImpl;
import org.hypothesis.presenter.GroupManagementPresenter;
import org.hypothesis.presenter.PackSetManagementVNPresenter;
import org.hypothesis.presenter.SlideManagementPresenterImpl;
import org.hypothesis.presenter.UserManagementVNPresenter;
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
	PACKS("/packs", "Caption.View.Packs", UserPacksPresenter.class, FontAwesome.BARS, true, new String[] {ROLE_USER, ROLE_MANAGER, ROLE_SUPERUSER}),
	// VN specific - removed public packs
	//PUBLIC("/public", "Caption.View.Public", PublicPacksPresenter.class, FontAwesome.EYE, true, new String[] {null, ROLE_USER, ROLE_MANAGER, ROLE_SUPERUSER}),
	USERS("/users", "Caption.View.Users", UserManagementVNPresenter.class, FontAwesome.USER, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	GROUPS("/groups", "Caption.View.Groups", GroupManagementPresenter.class, FontAwesome.GROUP, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	PACK_SETS("/sets", "Caption.View.PackSets", PackSetManagementVNPresenter.class, FontAwesome.BOOK, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	CONTROL("/control", "Caption.View.ControlPanel", ControlPanelVNPresenter.class, FontAwesome.LIST, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	EXPORT("/export", "Caption.View.Export", ExportPresenterImpl.class, FontAwesome.TABLE, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	SCORES("/scores", "Caption.View.Scores", ExportScoreVNPresenterImpl.class, FontAwesome.BAR_CHART, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER}),
	SLIDES("/slides", "Caption.View.Slides", SlideManagementPresenterImpl.class, FontAwesome.FILE_CODE_O, true, new String[] {ROLE_MANAGER, ROLE_SUPERUSER});

	private final String viewName;
	private final String caption;
	private final Class<? extends ViewPresenter> presenterClass;
	private final Resource icon;
	private final boolean stateful;
	private final Set<String> roles;

	HypothesisViewType(final String viewName, final String caption, final Class<? extends ViewPresenter> presenterClass, final Resource icon, final boolean stateful, final String[] roles) {
		this.viewName = viewName;
		this.caption = caption;
		this.presenterClass = presenterClass;
		this.icon = icon;
		this.stateful = stateful;
		this.roles = new HashSet<>();

		initRoles(roles);
	}

	private void initRoles(String[] roles) {
		for (String role : roles) {
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

	public boolean isAllowed(Set<RoleDto> checkRoles) {
		if (null == checkRoles || checkRoles.isEmpty()) {
			return roles.contains(null);
		} else {
			for (RoleDto checkRole : checkRoles) {
				if (roles.contains(checkRole.getName())) {
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
